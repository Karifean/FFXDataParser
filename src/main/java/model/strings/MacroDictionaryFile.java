package model.strings;

import main.StringHelper;
import reading.Chunk;
import reading.BytesHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * macrodic.dcp
 */
public class MacroDictionaryFile {
    List<List<MacroString>> allStrings = new ArrayList<>();

    private final String localization;

    public MacroDictionaryFile(int[] bytes, String localization) {
        this.localization = localization;
        List<Chunk> chunks = BytesHelper.bytesToChunks(bytes, 16, 0);
        for (Chunk chunk : chunks) {
            allStrings.add(mapStringsForChunk(chunk));
        }
    }

    private List<MacroString> mapStringsForChunk(Chunk chunk) {
        if (chunk.offset == 0) {
            return List.of();
        }
        return MacroString.fromStringData(chunk.bytes, StringHelper.localizationToCharset(localization));
    }

    public void publishStrings() {
        int chunkCount = allStrings.size();
        for (int i = 0; i < chunkCount; i++) {
            publishStringsOfChunk(i);
        }
    }

    private void publishStringsOfChunk(int i) {
        List<MacroString> list = allStrings.get(i);
        int stringCount = list.size();
        for (int j = 0; j < stringCount; j++) {
            StringHelper.MACRO_LOOKUP.computeIfAbsent(i * 0x100 + j, k -> new LocalizedMacroStringObject()).setLocalizedContent(localization, list.get(j));
        }
    }

    @Override
    public String toString() {
        StringBuilder full = new StringBuilder();
        for (int i = 0; i < allStrings.size(); i++) {
            List<MacroString> list = allStrings.get(i);
            full.append("- Macro Dict ").append(StringHelper.hex2WithSuffix(i)).append(" -\n");
            for (int j = 0; j < list.size(); j++) {
                MacroString macroString = list.get(j);
                full.append("String ").append(StringHelper.hex2WithSuffix(j)).append(": ").append(macroString.toString());
                full.append('\n');
            }
        }
        return full.toString();
    }
}
