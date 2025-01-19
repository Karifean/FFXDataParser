package model;

import main.StringHelper;
import reading.Chunk;

import java.util.ArrayList;
import java.util.List;

/**
 * macrodic.dcp
 */
public class MacroDictionaryFile {
    List<List<String>> allStrings = new ArrayList<>();

    private String localization;

    public MacroDictionaryFile(List<Chunk> chunks, String localization) {
        this.localization = localization;
        for (Chunk chunk : chunks) {
            allStrings.add(mapStringsForChunk(chunk));
        }
    }

    private List<String> mapStringsForChunk(Chunk chunk) {
        if (chunk.offset == 0) {
            return List.of();
        }
        return StringHelper.readStringData(chunk.bytes, false);
    }

    public void publishStrings() {
        int chunkCount = allStrings.size();
        for (int i = 0; i < chunkCount; i++) {
            publishStringsOfChunk(i);
        }
    }

    private void publishStringsOfChunk(int i) {
        List<String> list = allStrings.get(i);
        int stringCount = list.size();
        for (int j = 0; j < stringCount; j++) {
            StringHelper.MACRO_LOOKUP.computeIfAbsent(i * 0x100 + j, k -> new LocalizedStringObject()).setLocalizedContent(localization, list.get(j));
        }
    }

    @Override
    public String toString() {
        StringBuilder full = new StringBuilder();
        for (int i = 0; i < allStrings.size(); i++) {
            List<String> list = allStrings.get(i);
            full.append("- Macro Dict #").append(i).append(" -\n");
            for (int j = 0; j < list.size(); j++) {
                full.append("String #").append(j).append(":").append(list.get(j)).append('\n');
            }
        }
        return full.toString();
    }
}
