package atel;

import atel.model.*;
import main.StringHelper;
import model.strings.LocalizedFieldStringObject;
import reading.BytesHelper;

import java.util.*;
import java.util.stream.Collectors;

import static main.StringHelper.*;
import static reading.BytesHelper.*;

public class AtelScriptObject {
    public static final boolean RECOMPILE_ATEL = true;
    private static final int STATIC_HEADER_LENGTH = 0x38;
    private static final int JUMP_LINE_MINLENGTH = 16;
    private static final int HEX_LINE_MINLENGTH = COLORS_USE_CONSOLE_CODES ? 58 : 48;
    private static final int JUMP_PLUS_HEX_LINE_MINLENGTH = JUMP_LINE_MINLENGTH + HEX_LINE_MINLENGTH + 1;

    private static final boolean PRINT_META_STRUCT = false;
    private static final boolean PRINT_REF_INTS_FLOATS = false;
    private static final boolean PRINT_JUMP_TABLE = false;
    private static final boolean INFER_BITWISE_OPS_AS_BITFIELDS = false;
    private static final boolean PRINT_CODE_BY_ENTRY_POINTS = false;
    private static final boolean KEEP_EXISTING_REF_INTS_FLOATS_ORDER = true;

    private final boolean hasWorkerLocalPrivateData;
    private final int[] bytes;
    private final int[] battleWorkerMappingBytes;
    private int battleWorkerCount;
    private int battleWorkerSlotCount;

    private int[] actualScriptCodeBytes;
    public List<ScriptWorker> workers;
    public int[] refFloats;
    public int[] refInts;
    public List<ScriptVariable> variableDeclarations;
    private int variableStructsTableOffset;
    private int intTableOffset;
    private int floatTableOffset;
    private int sharedDataOffset;
    private int jumpTablesOffset;
    private int map_start;
    private int creatorTagAddress;
    private int scriptIdAddress;
    private int jumpsEndAddress;
    private int amountOfType2or3Scripts;
    private int amountOfType4Scripts;
    private int amountOfType5Scripts;
    private int areaNameBytes;
    private int areaNameIndexesOffset;
    private int scriptMetaStructOffset;
    private int mainScriptIndex;
    private int unknown1A;
    public int eventDataOffset;
    private int unknownTable24Offset;
    public int scriptCodeLength;
    private int scriptCodeStartAddress;
    private int scriptCodeEndAddress;
    private int namespaceCount;  // Total number of workers
    private int actorCount; // Total number of workers except subroutines
    public List<LocalizedFieldStringObject> strings;
    public List<Integer> areaNameIndexes;
    public MapEntranceObject[] mapEntrances;
    public MapTableObject[] mapTableObjects;
    public String creatorTag;
    public String scriptId;
    public ScriptMetaStruct scriptMetaStruct;
    Stack<StackObject> stack = new Stack<>();
    Map<Integer, String> currentTempITypes = new HashMap<>();
    Map<Integer, List<StackObject>> varEnums = new HashMap<>();
    Map<Integer, StackObject> constants = new HashMap<>();
    int currentWorkerIndex = 0;
    String currentRAType = "unknown";
    String currentRXType = "unknown";
    String currentRYType = "unknown";
    boolean gatheringInfo = true;
    Map<Integer, List<ScriptJump>> scriptJumpsByDestination;
    List<ScriptJump> currentExecutionLines;

    int lineCount = 0;
    String textScriptLine;
    List<String> warningsOnLine;
    List<String> offsetLines;
    List<String> textScriptLines;
    List<String> hexScriptLines;
    List<String> jumpLines;
    List<String> warnLines;
    List<ScriptInstruction> instructions = new ArrayList<>();
    List<ScriptLine> scriptLines = new ArrayList<>();

    int[] eventDataBytes = new int[0];
    int[] sharedDataBytes = new int[0];
    int[] unknownTable24Data = new int[0];

    public AtelScriptObject(int[] bytes, int[] battleWorkerMappingBytes) {
        this.bytes = bytes;
        this.battleWorkerMappingBytes = battleWorkerMappingBytes;
        this.hasWorkerLocalPrivateData = battleWorkerMappingBytes == null;
        mapFields();
        parseWorkers();
        parseEventSharedData();
    }

    public AtelScriptObjectBytes toBytes() {
        List<Integer> scriptBytesList = new ArrayList<>();
        List<ScriptVariable> newVariableDeclarations;
        if (variableDeclarations != null && variableDeclarations.size() > 0) {
            newVariableDeclarations = new ArrayList<>(variableDeclarations);
        }  else {
            newVariableDeclarations = new ArrayList<>();
        }
        List<Integer> newRefInts;
        if (KEEP_EXISTING_REF_INTS_FLOATS_ORDER && refInts != null && refInts.length > 0) {
            newRefInts = new ArrayList<>(Arrays.stream(refInts).boxed().toList());
        } else {
            newRefInts = new ArrayList<>();
        }
        List<Integer> newRefFloats;
        if (KEEP_EXISTING_REF_INTS_FLOATS_ORDER && refFloats != null && refFloats.length > 0) {
            newRefFloats = new ArrayList<>(Arrays.stream(refFloats).boxed().toList());
        } else {
            newRefFloats = new ArrayList<>();
        }
        int cursor = 0;
        for (ScriptWorker worker : workers) {
            Map<ScriptLine, ScriptJump> workerJumpTargets = new HashMap<>();
            List<ScriptLine> workerLines = worker.getLines();
            for (ScriptLine line : workerLines) {
                line.rereference(cursor, workerJumpTargets, newVariableDeclarations, newRefInts, newRefFloats);
                scriptBytesList.addAll(line.toBytesList());
                cursor = scriptBytesList.size();
            }
            worker.setJumpTargets(workerJumpTargets);
        }
        int scriptCodeLength = scriptBytesList.size();
        int[] scriptCodeBytes = intListToArray(scriptBytesList);
        int[] refIntsArray = BytesHelper.intListToArray(newRefInts);
        int[] refFloatsArray = BytesHelper.intListToArray(newRefFloats);
        int varCount = newVariableDeclarations.size();
        for (ScriptWorker worker : workers) {
            worker.variableDeclarations = variableDeclarations;
            worker.refInts = refIntsArray;
            worker.refFloats = refFloatsArray;
            worker.variablesCount = varCount;
            worker.refIntCount = refIntsArray.length;
            worker.refFloatCount = refFloatsArray.length;
            worker.entryPointCount = worker.entryPoints.size();
            worker.jumpCount = worker.jumps.size();
        }
        int[] staticHeaderBytes = new int[STATIC_HEADER_LENGTH];
        write4Bytes(staticHeaderBytes, 0x00, scriptCodeLength);
        int amountOfType2or3Scripts = (int) workers.stream().filter(w -> w.eventWorkerType == 2 || w.eventWorkerType == 3).count();
        write2Bytes(staticHeaderBytes, 0x14, amountOfType2or3Scripts);
        int amountOfType4Scripts = (int) workers.stream().filter(w -> w.eventWorkerType == 4).count();
        write2Bytes(staticHeaderBytes, 0x16, amountOfType4Scripts);
        write2Bytes(staticHeaderBytes, 0x18, mainScriptIndex);
        write2Bytes(staticHeaderBytes, 0x1A, unknown1A);
        int amountOfType5Scripts = (int) workers.stream().filter(w -> w.eventWorkerType == 5).count();
        write2Bytes(staticHeaderBytes, 0x1C, amountOfType5Scripts);
        int workerCount = workers.size();
        write2Bytes(staticHeaderBytes, 0x34, workerCount);
        int actorCount = (int) workers.stream().filter(w -> w.eventWorkerType != 0).count();
        write2Bytes(staticHeaderBytes, 0x36, actorCount);
        int workerHeaderLength = workerCount * (ScriptWorker.LENGTH + 4);
        int[] workerHeaderBytes = new int[workerHeaderLength];
        for (int i = 0; i < workerCount; i++) {
            write4Bytes(workerHeaderBytes, i * 4, STATIC_HEADER_LENGTH + workerCount * 4 + i * ScriptWorker.LENGTH);
        }
        int newMapEntrancesOffset = STATIC_HEADER_LENGTH + workerHeaderLength;
        int mapEntrancesLength = mapEntrances != null ? mapEntrances.length * MapEntranceObject.LENGTH : 0;
        int[] mapEntrancesBytes = new int[mapEntrancesLength];
        if (mapEntrancesLength == 0) {
            write4Bytes(staticHeaderBytes, 0x04, 0);
        } else {
            write4Bytes(staticHeaderBytes, 0x04, newMapEntrancesOffset);
            for (int i = 0; i < mapEntrances.length; i++) {
                System.arraycopy(mapEntrances[i].toBytes(), 0, mapEntrancesBytes, i * MapEntranceObject.LENGTH, MapEntranceObject.LENGTH);
            }
        }
        int newCreatorTagOffset = newMapEntrancesOffset + mapEntrancesLength;
        write4Bytes(staticHeaderBytes, 0x08, newCreatorTagOffset);
        int[] creatorTagBytes = utf8StringToBytes(creatorTag);
        int newScriptIdAddress = padLengthTo(newCreatorTagOffset + creatorTagBytes.length, 2);
        write4Bytes(staticHeaderBytes, 0x0C, newScriptIdAddress);
        int[] scriptIdBytes = utf8StringToBytes(scriptId);
        int newVariableDeclarationsOffset = (newScriptIdAddress + scriptIdBytes.length + 6) & 0xFFFFFFFC;
        int newRefIntsOffset = newVariableDeclarationsOffset + varCount * 8;
        int newRefFloatsOffset = newRefIntsOffset + newRefInts.size() * 4;
        int referencesLength = varCount * 8 + newRefInts.size() * 4 + newRefFloats.size() * 4;
        int[] referencesBytes = new int[referencesLength];
        for (int i = 0; i < varCount; i++) {
            System.arraycopy(newVariableDeclarations.get(i).toBytes(), 0, referencesBytes, i * 8, 8);
        }
        for (int i = 0; i < newRefInts.size(); i++) {
            write4Bytes(referencesBytes, varCount * 8 + i * 4, newRefInts.get(i));
        }
        for (int i = 0; i < newRefFloats.size(); i++) {
            write4Bytes(referencesBytes, varCount * 8 + newRefInts.size() * 4 + i * 4, newRefFloats.get(i));
        }
        int newScriptCodeOffset = newVariableDeclarationsOffset + referencesLength;
        write4Bytes(staticHeaderBytes, 0x30, newScriptCodeOffset);
        int newSharedDataOffset = (newScriptCodeOffset + scriptCodeLength + 0x1F) & 0xFFFFFFF0;
        int sharedDataLength = sharedDataBytes.length;
        int newMetaStructOffset = (newSharedDataOffset + sharedDataLength + 0x0F) & 0xFFFFFFF0;
        int[] metaStructBytes;
        if (scriptMetaStruct != null) {
            ScriptMetaStruct.ScriptMetaStructBytes metaStructBytesObj = scriptMetaStruct.toBytes(newMetaStructOffset);
            write4Bytes(staticHeaderBytes, 0x2C, metaStructBytesObj.enterOffset());
            metaStructBytes = metaStructBytesObj.bytes();
        } else {
            write4Bytes(staticHeaderBytes, 0x2C, 0);
            metaStructBytes = new int[0];
        }
        int metaStructLength = metaStructBytes.length;
        int newEventDataOffset = newMetaStructOffset + metaStructLength;
        int eventDataLength = eventDataBytes.length;
        write4Bytes(staticHeaderBytes, 0x20, newEventDataOffset);

        int mapTableLength = mapTableObjects != null && mapTableObjects.length > 0 ? mapTableObjects.length * MapTableObject.LENGTH + 4 : 0;
        int[] mapTableBytes = new int[mapTableLength];
        int newMapTableOffset = newEventDataOffset + eventDataLength;
        if (mapTableLength == 0) {
            write4Bytes(staticHeaderBytes, 0x24, 0);
        } else {
            write4Bytes(staticHeaderBytes, 0x24, newMapTableOffset);
            write4Bytes(mapTableBytes, 0, mapTableObjects.length);
            for (int i = 0; i < mapTableObjects.length; i++) {
                System.arraycopy(mapTableObjects[i].toBytes(), 0, mapTableBytes, 4 + i * MapTableObject.LENGTH, MapTableObject.LENGTH);
            }
        }

        int newAreaNameIndexesOffset = newMapTableOffset + mapTableBytes.length;
        int areaNameCount = areaNameIndexes != null ? areaNameIndexes.size() : 0;
        int areaNameIndexesLength = areaNameCount * 2;
        int[] areaNameIndexesBytes = new int[areaNameIndexesLength];
        if (areaNameCount == 0) {
            write2Bytes(staticHeaderBytes, 0x1E, 0);
            write4Bytes(staticHeaderBytes, 0x28, 0);
        } else {
            for (int i = 0; i < areaNameCount; i++) {
                write2Bytes(areaNameIndexesBytes, i * 2, areaNameIndexes.get(i));
            }
            int newAreaIndexesBytes = 0x8000 | areaNameCount;
            write2Bytes(staticHeaderBytes, 0x1E, newAreaIndexesBytes);
            write4Bytes(staticHeaderBytes, 0x28, newAreaNameIndexesOffset);
        }
        int newJumpTablesOffset = newAreaNameIndexesOffset + areaNameIndexesLength;
        int jumpTableLength = 0;
        for (ScriptWorker worker : workers) {
            worker.variableDeclarationsOffset = newVariableDeclarationsOffset;
            worker.refIntsOffset = newRefIntsOffset;
            worker.refFloatsOffset = newRefFloatsOffset;
            worker.privateDataLengthPadded = (worker.privateDataLength + 0x0F) & 0xFFFFFFF0;
            worker.sharedDataOffset = newSharedDataOffset;
            int workerListLength = (worker.entryPointCount * 4) + (worker.jumpCount * 4) + worker.privateDataLengthPadded;
            jumpTableLength += workerListLength;
        }
        int jumpTableCursor = 0;
        int[] jumpTableBytes = new int[jumpTableLength];
        for (int i = 0; i < workerCount; i++) {
            ScriptWorker worker = getWorker(i);
            worker.scriptEntryPointsOffset = newJumpTablesOffset + jumpTableCursor;
            for (int j = 0; j < worker.entryPointCount; j++) {
                write4Bytes(jumpTableBytes, jumpTableCursor, worker.getEntryPoint(j).addr);
                jumpTableCursor += 4;
            }
            worker.jumpsOffset = newJumpTablesOffset + jumpTableCursor;
            for (int j = 0; j < worker.jumpCount; j++) {
                write4Bytes(jumpTableBytes, jumpTableCursor, worker.getJump(j).addr);
                jumpTableCursor += 4;
            }
            if (worker.privateDataLength > 0 && hasWorkerLocalPrivateData) {
                worker.privateDataOffset = newJumpTablesOffset + jumpTableCursor;
                System.arraycopy(worker.privateDataBytes, 0, jumpTableBytes, jumpTableCursor, worker.privateDataLength);
                jumpTableCursor += worker.privateDataLengthPadded;
            }
            System.arraycopy(worker.toBytes(), 0, workerHeaderBytes, workerCount * 4 + i * ScriptWorker.LENGTH, ScriptWorker.LENGTH);
        }
        int totalLength = (newJumpTablesOffset + jumpTableLength + 0x14) & 0xFFFFFFF0;
        write4Bytes(staticHeaderBytes, 0x10, totalLength);
        int[] scriptObjectFullBytes = new int[totalLength];
        System.arraycopy(staticHeaderBytes, 0, scriptObjectFullBytes, 0, STATIC_HEADER_LENGTH);
        System.arraycopy(workerHeaderBytes, 0, scriptObjectFullBytes, STATIC_HEADER_LENGTH, workerHeaderLength);
        System.arraycopy(mapEntrancesBytes, 0, scriptObjectFullBytes, newMapEntrancesOffset, mapEntrancesLength);
        System.arraycopy(creatorTagBytes, 0, scriptObjectFullBytes, newCreatorTagOffset, creatorTagBytes.length);
        System.arraycopy(scriptIdBytes, 0, scriptObjectFullBytes, newScriptIdAddress, scriptIdBytes.length);
        System.arraycopy(referencesBytes, 0, scriptObjectFullBytes, newVariableDeclarationsOffset, referencesLength);
        System.arraycopy(scriptCodeBytes, 0, scriptObjectFullBytes, newScriptCodeOffset, scriptCodeLength);
        System.arraycopy(sharedDataBytes, 0, scriptObjectFullBytes, newSharedDataOffset, sharedDataLength);
        System.arraycopy(metaStructBytes, 0, scriptObjectFullBytes, newMetaStructOffset, metaStructBytes.length);
        System.arraycopy(eventDataBytes, 0, scriptObjectFullBytes, newEventDataOffset, eventDataLength);
        System.arraycopy(mapTableBytes, 0, scriptObjectFullBytes, newMapTableOffset, mapTableLength);
        if (newAreaNameIndexesOffset != 0) {
            System.arraycopy(areaNameIndexesBytes, 0, scriptObjectFullBytes, newAreaNameIndexesOffset, areaNameIndexesBytes.length);
        }
        System.arraycopy(jumpTableBytes, 0, scriptObjectFullBytes, newJumpTablesOffset, jumpTableLength);
        return new AtelScriptObjectBytes(scriptObjectFullBytes, getBattleWorkerMappingBytes());
    }

    private void mapFields() {
        scriptCodeLength = read4Bytes(0x00);
        map_start = read4Bytes(0x04);
        creatorTagAddress = read4Bytes(0x08);
        scriptIdAddress = read4Bytes(0x0C);
        jumpsEndAddress = read4Bytes(0x10);
        amountOfType2or3Scripts = read2Bytes(0x14);
        amountOfType4Scripts = read2Bytes(0x16);
        mainScriptIndex = read2Bytes(0x18);
        unknown1A = read2Bytes(0x1A);
        amountOfType5Scripts = read2Bytes(0x1C);
        areaNameBytes = read2Bytes(0x1E);
        eventDataOffset = read4Bytes(0x20);
        unknownTable24Offset = read4Bytes(0x24);
        areaNameIndexesOffset = read4Bytes(0x28);
        scriptMetaStructOffset = read4Bytes(0x2C);
        scriptCodeStartAddress = read4Bytes(0x30);
        namespaceCount = read2Bytes(0x34);
        actorCount = read2Bytes(0x36);

        creatorTag = getUtf8String(bytes, creatorTagAddress);
        scriptId = getUtf8String(bytes, scriptIdAddress);

        if (map_start > 0 && map_start < creatorTagAddress) {
            int mapStartLength = (creatorTagAddress - map_start);
            int mapStartCount = mapStartLength / 0x20;
            mapEntrances = new MapEntranceObject[mapStartCount];
            for (int i = 0; i < mapStartCount; i++) {
                mapEntrances[i] = new MapEntranceObject(Arrays.copyOfRange(bytes, map_start + i * MapEntranceObject.LENGTH, map_start + (i + 1) * MapEntranceObject.LENGTH));
            }
        }

        if (unknownTable24Offset > 0) {
            int unknownTableCount = read4Bytes(unknownTable24Offset);
            mapTableObjects = new MapTableObject[unknownTableCount];
            for (int i = 0; i < unknownTableCount; i++) {
                mapTableObjects[i] = new MapTableObject(Arrays.copyOfRange(bytes, unknownTable24Offset + 4 + i * MapTableObject.LENGTH, unknownTable24Offset + 4 + (i + 1) * MapTableObject.LENGTH));
            }
        }

        if (areaNameBytes > 0 && areaNameIndexesOffset > 0) {
            if ((areaNameBytes & 0x8000) != 0) {
                int count = areaNameBytes & 0x7FFF;
                areaNameIndexes = new ArrayList<>(count);
                for (int i = 0; i < count; i++) {
                    areaNameIndexes.add(read2Bytes(areaNameIndexesOffset + 2 * i));
                }
            } else {
                areaNameIndexes = new ArrayList<>(1);
                areaNameIndexes.add(areaNameBytes);
            }
        }

        if (scriptMetaStructOffset > 0) {
            scriptMetaStruct = new ScriptMetaStruct(this, bytes, scriptMetaStructOffset);
        }
    }

    private void parseWorkers() {
        scriptJumpsByDestination = new HashMap<>();
        workers = new ArrayList<>(namespaceCount);
        for (int i = 0; i < namespaceCount; i++) {
            int offset = read4Bytes(0x38 + i * 4);
            ScriptWorker scriptWorker = new ScriptWorker(this, i, Arrays.copyOfRange(bytes, offset, offset + ScriptWorker.LENGTH));
            workers.add(scriptWorker);
            scriptWorker.parseReferences(bytes);
            for (ScriptJump entryPoint : scriptWorker.entryPoints) {
                scriptJumpsByDestination.computeIfAbsent(entryPoint.addr, (x) -> new ArrayList<>()).add(entryPoint);
            }
            for (ScriptJump jump : scriptWorker.jumps) {
                scriptJumpsByDestination.computeIfAbsent(jump.addr, (x) -> new ArrayList<>()).add(jump);
            }
        }
        syncVarIntFloatTables();
        parseBattleWorkerTypes();
    }

    private void parseEventSharedData() {
        int offsetAfter = areaNameIndexesOffset != 0 ? areaNameIndexesOffset : (jumpTablesOffset != 0 ? jumpTablesOffset : jumpsEndAddress);
        if (unknownTable24Offset > 0) {
            unknownTable24Data = Arrays.copyOfRange(bytes, unknownTable24Offset, offsetAfter);
            offsetAfter = unknownTable24Offset;
        }
        if (eventDataOffset > 0) {
            eventDataBytes = Arrays.copyOfRange(bytes, eventDataOffset, offsetAfter);
            offsetAfter = eventDataOffset;
        }
        if (sharedDataOffset > 0) {
            int nextOffset = scriptMetaStruct != null ? scriptMetaStruct.getStartOffset() : offsetAfter;
            sharedDataBytes = Arrays.copyOfRange(bytes, sharedDataOffset, nextOffset);
        }
    }

    public void addLocalizations(List<LocalizedFieldStringObject> strings) {
        if (strings == null) {
            return;
        }
        if (this.strings == null) {
            this.strings = strings;
            return;
        }
        for (int i = 0; i < strings.size(); i++) {
            LocalizedFieldStringObject localizationStringObject = strings.get(i);
            if (i < this.strings.size()) {
                LocalizedFieldStringObject stringObject = this.strings.get(i);
                if (stringObject != null && localizationStringObject != null) {
                    localizationStringObject.copyInto(stringObject);
                }
            } else {
                this.strings.add(localizationStringObject);
            }
        }
    }

    public void addVariableNamings(List<String> namings) {
        if (namings == null || namings.isEmpty()) {
            return;
        }
        for (String line : namings) {
            String[] split = line.split("=");
            if (split.length < 2) {
                System.err.println("Malformed declaration: " + line);
                continue;
            }
            int index = Integer.parseInt(split[0], 16);
            if (variableDeclarations != null && index >= 0 && index < variableDeclarations.size()) {
                ScriptVariable vr = variableDeclarations.get(index);
                vr.declaredLabel = "".equals(split[1]) ? null : split[1];
                if (split.length > 2) {
                    vr.declaredType = "".equals(split[2]) ? null : split[2];
                    if (split.length > 3) {
                        vr.declaredIndexType = "".equals(split[3]) ? null : split[3];
                    }
                }
            } else {
                System.err.println("Variable naming declaration out of bounds: " + StringHelper.formatHex2(index));
            }
        }
    }

    public String getDeclarationsAsString() {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < variableDeclarations.size(); i++) {
            String index = String.format("%02X", i);
            ScriptVariable vr = variableDeclarations.get(i);
            StringBuilder stringBuilder = new StringBuilder(index).append('=');
            boolean anyDeclaration = false;
            if (vr.declaredLabel != null) {
                anyDeclaration = true;
                stringBuilder.append(vr.declaredLabel);
            }
            stringBuilder.append('=');
            if (vr.declaredType != null) {
                anyDeclaration = true;
                stringBuilder.append(vr.declaredType);
            } else if (vr.inferredType != null && !"unknown".equals(vr.inferredType)) {
                anyDeclaration = true;
                stringBuilder.append(vr.inferredType);
            }
            stringBuilder.append('=');
            if (vr.declaredIndexType != null) {
                anyDeclaration = true;
                stringBuilder.append(vr.declaredIndexType);
            }
            if (anyDeclaration) {
                String str = stringBuilder.toString();
                while (str.endsWith("=")) {
                    str = str.substring(0, str.length() - 1);
                }
                lines.add(str);
            }
        }
        return String.join("\n", lines);
    }

    public void setStrings(List<LocalizedFieldStringObject> strings) {
        this.strings = strings;
    }

    public void parseScript() {
        scriptCodeEndAddress = scriptCodeStartAddress + scriptCodeLength;
        actualScriptCodeBytes = Arrays.copyOfRange(bytes, scriptCodeStartAddress, scriptCodeEndAddress);
        syntacticParseScriptCode();

        gatheringInfo = true;
        semanticParseScriptCode();
        inferBooleans();

        gatheringInfo = false;
        semanticParseScriptCode();
        for (ScriptWorker worker : workers) {
            worker.parseWorkerAtelCode(actualScriptCodeBytes);
        }
    }

    public ScriptWorker getWorker(int workerIndex) {
        return workerIndex >= 0 && workerIndex < workers.size() ? workers.get(workerIndex) : null;
    }

    private void syncVarIntFloatTables() {
        variableStructsTableOffset = 0;
        intTableOffset = 0;
        floatTableOffset = 0;
        sharedDataOffset = 0;
        jumpTablesOffset = 0;
        boolean first = true;
        for (ScriptWorker worker : workers) {
            if (first) {
                first = false;
                variableStructsTableOffset = worker.variableDeclarationsOffset;
                variableDeclarations = worker.variableDeclarations;
                intTableOffset = worker.refIntsOffset;
                refInts = worker.refInts;
                floatTableOffset = worker.refFloatsOffset;
                refFloats = worker.refFloats;
                sharedDataOffset = worker.sharedDataOffset;
                jumpTablesOffset = worker.scriptEntryPointsOffset;
            } else {
                if (worker.variableDeclarationsOffset != variableStructsTableOffset || worker.variablesCount != variableDeclarations.size()) {
                    System.err.println("WARNING, variables table mismatch!");
                }
                worker.variableDeclarations = variableDeclarations;
                if (worker.refIntsOffset != intTableOffset || worker.refIntCount != refInts.length) {
                    System.err.println("WARNING, int table mismatch!");
                }
                worker.refInts = refInts;
                if (worker.refFloatsOffset != floatTableOffset || worker.refFloatCount != refFloats.length) {
                    System.err.println("WARNING, float table mismatch!");
                }
                worker.refFloats = refFloats;
                if (worker.sharedDataOffset != sharedDataOffset) {
                    System.err.println("WARNING, shared data offset mismatch!");
                }
                worker.sharedDataOffset = sharedDataOffset;
            }
        }
    }

    private void parseBattleWorkerTypes() {
        if (battleWorkerMappingBytes == null || battleWorkerMappingBytes.length == 0) {
            return;
        }
        battleWorkerCount = battleWorkerMappingBytes[0];
        battleWorkerSlotCount = battleWorkerMappingBytes[1];
        // map from section index to purpose slot
        Map<Integer, Integer> slotMap = new HashMap<>();
        for (int i = 0; i < battleWorkerSlotCount; i++) {
            if (battleWorkerMappingBytes[i + 2] != 0xFF) {
                slotMap.put(battleWorkerMappingBytes[i + 2], i);
            }
        }
        int sectionsLineOffset = (battleWorkerSlotCount + 0x03) & 0xFFFFFFFE;
        Integer firstOffset = null;
        for (int i = 0; i < battleWorkerCount; i++) {
            int offset = sectionsLineOffset + i * 4;
            int workerIndex = battleWorkerMappingBytes[offset];
            int battleWorkerType = battleWorkerMappingBytes[offset + 1];
            int sectionOffset = BytesHelper.read2Bytes(battleWorkerMappingBytes, offset + 2);
            if (i == 0) {
                firstOffset = sectionOffset;
            } else if (offset >= firstOffset) {
                // System.err.println("WARNING - Offset number mismatch at index " + i + " expected " + workersToMapSupposedly);
                break;
            }
            ScriptWorker worker = getWorker(workerIndex);
            if (worker != null) {
                if (slotMap.containsKey(i)) {
                    worker.setPurposeSlot(slotMap.get(i));
                }
                int entryPointPayloadOffset = sectionOffset + 2;
                int entryPointSlotCount = BytesHelper.read2Bytes(battleWorkerMappingBytes, sectionOffset);
                int[] entryPointPayload = Arrays.copyOfRange(battleWorkerMappingBytes, entryPointPayloadOffset, entryPointPayloadOffset + entryPointSlotCount * 2);
                worker.setBattleWorkerTypes(battleWorkerType, entryPointSlotCount, entryPointPayload);
            } else {
                System.err.println("WARNING - no worker with index " + workerIndex + " at section " + i + "!");
            }
        }
    }

    private int[] getBattleWorkerMappingBytes() {
        if (battleWorkerMappingBytes == null) {
            return null;
        }
        int headerLength = battleWorkerSlotCount + 2;
        int[] battleWorkerMappingHeaderBytes = new int[headerLength];
        Arrays.fill(battleWorkerMappingHeaderBytes, 0xFF);
        List<Integer> workerIndexes = new ArrayList<>();
        for (int i = 0; i < workers.size(); i++) {
            ScriptWorker worker = getWorker(i);
            if (worker.purposeSlot != null) {
                battleWorkerMappingHeaderBytes[worker.purposeSlot + 2] = workerIndexes.size();
                workerIndexes.add(i);
            }
        }
        int newBattleWorkerCount = workerIndexes.size();
        battleWorkerMappingHeaderBytes[0] = newBattleWorkerCount;
        battleWorkerMappingHeaderBytes[1] = battleWorkerSlotCount;
        int battleWorkerPayloadsOffset = (battleWorkerSlotCount + 0x03) & 0xFFFFFFFE;
        int[] battleWorkerPayloadsBytes = new int[newBattleWorkerCount * 4];
        int workerSectionOffset = battleWorkerPayloadsOffset + newBattleWorkerCount * 4;
        int cursor = workerSectionOffset;
        int[][] workerSectionBytesList = new int[newBattleWorkerCount][];
        for (int i = 0; i < newBattleWorkerCount; i++) {
            int workerIndex = workerIndexes.get(i);
            battleWorkerPayloadsBytes[i * 4] = workerIndex;
            ScriptWorker worker = getWorker(workerIndex);
            battleWorkerPayloadsBytes[i * 4 + 1] = worker.battleWorkerType;
            write2Bytes(battleWorkerPayloadsBytes, i * 4 + 2, cursor);
            int[] slotsBytes = worker.getBattleWorkerEntryPointSlotsBytes();
            workerSectionBytesList[i] = slotsBytes;
            cursor += slotsBytes.length;
        }
        int[] fullBytes = new int[cursor];
        System.arraycopy(battleWorkerMappingHeaderBytes, 0, fullBytes, 0, headerLength);
        System.arraycopy(battleWorkerPayloadsBytes, 0, fullBytes, headerLength, newBattleWorkerCount * 4);
        cursor = workerSectionOffset;
        for (int i = 0; i < newBattleWorkerCount; i++) {
            int sectionLength = workerSectionBytesList[i].length;
            System.arraycopy(workerSectionBytesList[i], 0, fullBytes, cursor, sectionLength);
            cursor += sectionLength;
        }
        return fullBytes;
    }

    protected void syntacticParseScriptCode() {
        lineCount = 0;
        scriptLines = new ArrayList<>();
        instructions = new ArrayList<>();
        offsetLines = new ArrayList<>();
        hexScriptLines = new ArrayList<>();
        jumpLines = new ArrayList<>();
        List<ScriptInstruction> lineInstructions = new ArrayList<>();
        List<ScriptJump> jumpsOnLine = new ArrayList<>();
        int currentScriptLineOffset = 0;
        int cursor = 0;
        while (cursor < scriptCodeLength) {
            List<ScriptJump> jumpsOnInstruction = new ArrayList<>();
            int offset = cursor;
            nextAiByte(cursor, jumpsOnInstruction, false);
            int opcode = actualScriptCodeBytes[cursor];
            cursor++;
            ScriptInstruction instruction;
            if (hasArgs(opcode)) {
                nextAiByte(cursor, jumpsOnInstruction, true);
                final int arg1 = actualScriptCodeBytes[cursor];
                cursor++;
                nextAiByte(cursor, jumpsOnInstruction, true);
                final int arg2 = actualScriptCodeBytes[cursor];
                cursor++;
                instruction = new ScriptInstruction(offset, opcode, arg1, arg2);
            } else {
                instruction = new ScriptInstruction(offset, opcode);
            }
            instruction.incomingJumps = jumpsOnInstruction;
            jumpsOnLine.addAll(jumpsOnInstruction);
            lineInstructions.add(instruction);
            instructions.add(instruction);
            if (getLineEnd(opcode)) {
                ScriptLine scriptLine = new ScriptLine(null, currentScriptLineOffset, lineInstructions, jumpsOnLine);
                jumpsOnLine.forEach(j -> j.targetLine = scriptLine);
                scriptLines.add(scriptLine);
                lineCount++;
                offsetLines.add(StringHelper.formatHex4(currentScriptLineOffset));
                currentScriptLineOffset = cursor;
                hexScriptLines.add(getHexLine(lineInstructions));
                jumpLines.add(getJumpLine(jumpsOnLine));
                textScriptLine = "";
                jumpsOnLine.clear();
                lineInstructions = new ArrayList<>();
            }
        }
    }

    private void semanticParseScriptCode() {
        currentExecutionLines = new ArrayList<>();
        textScriptLines = new ArrayList<>();
        warnLines = new ArrayList<>();
        textScriptLine = "";
        warningsOnLine = new ArrayList<>();
        List<ScriptInstruction> nonNullInstructionsOnLine = new ArrayList<>();
        List<ScriptJump> softMisalignedOnLine = new ArrayList<>();
        List<ScriptJump> hardMisalignedOnLine = new ArrayList<>();
        for (ScriptInstruction instruction : instructions) {
            if (!instruction.incomingJumps.isEmpty()) {
                restoreTypingsFromJumps(instruction.incomingJumps);
                instruction.incomingJumps.forEach(j -> j.reachableFrom = currentExecutionLines);
                currentExecutionLines = new ArrayList<>(currentExecutionLines);
                currentExecutionLines.addAll(instruction.incomingJumps);
                List<ScriptJump> hardMisaligned = instruction.incomingJumps.stream().filter(j -> j.hardMisaligned).collect(Collectors.toList());
                if (!hardMisaligned.isEmpty()) {
                    hardMisalignedOnLine.addAll(hardMisaligned);
                }
                if (!nonNullInstructionsOnLine.isEmpty()) {
                    softMisalignedOnLine.addAll(instruction.incomingJumps);
                }
            }
            if (instruction.opcode != 0x00) {
                nonNullInstructionsOnLine.add(instruction);
            }
            processInstruction(instruction);
            if (getLineEnd(instruction.opcode)) {
                if (!stack.empty()) {
                    warningsOnLine.add("Stack not empty (" + stack.size() + "): " + stack);
                    stack.clear();
                }
                if (!hardMisalignedOnLine.isEmpty()) {
                    softMisalignedOnLine.removeAll(hardMisalignedOnLine);
                    warningsOnLine.add("Broken jumps: " + hardMisalignedOnLine.stream().map(j -> j.getLabelWithAddr()).collect(Collectors.joining(",")));
                    hardMisalignedOnLine.clear();
                }
                if (!softMisalignedOnLine.isEmpty()) {
                    warningsOnLine.add("Soft-broken jumps: " + softMisalignedOnLine.stream().map(j -> j.getLabelWithAddr()).collect(Collectors.joining(",")));
                    softMisalignedOnLine.clear();
                }
                textScriptLines.add(textScriptLine);
                warnLines.add(warningsOnLine.isEmpty() ? null : (" " + String.join("; ", warningsOnLine)));
                textScriptLine = "";
                warningsOnLine = new ArrayList<>();
                nonNullInstructionsOnLine.clear();
            }
        }
    }

    private static String getJumpLine(List<ScriptJump> jumpsOnLine) {
        if (jumpsOnLine == null || jumpsOnLine.isEmpty()) {
            return "";
        }
        return jumpsOnLine.stream().map(j -> j.getLabel()).collect(Collectors.joining(",")) + ':';
    }

    private static String getHexLine(List<ScriptInstruction> lineInstructions) {
        Iterator<ScriptInstruction> iterator = lineInstructions.iterator();
        int zeroesInARow = 0;
        List<String> segments = new ArrayList<>();
        while (iterator.hasNext()) {
            ScriptInstruction ins = iterator.next();
            if (ins.opcode == 0x00) {
                zeroesInARow++;
                if (zeroesInARow == 1) {
                    segments.add("00");
                } else if (zeroesInARow == 2) {
                    segments.add("...");
                }
            } else {
                if (zeroesInARow > 1) {
                    segments.add("00");
                }
                zeroesInARow = 0;
                segments.add(ins.asHexString());
            }
        }
        if (zeroesInARow > 1) {
            segments.add("00");
        }
        return String.join(" ", segments);
    }

    protected void nextAiByte(int cursor, List<ScriptJump> jumpsOnLine, boolean isArgByte) {
        if (scriptJumpsByDestination.containsKey(cursor)) {
            List<ScriptJump> jumps = scriptJumpsByDestination.get(cursor);
            restoreTypingsFromJumps(jumps);
            jumpsOnLine.addAll(jumps);
            if (isArgByte) {
                jumps.forEach(ScriptJump::markAsHardMisaligned);
            }
        }
    }

    protected void restoreTypingsFromJumps(List<ScriptJump> jumps) {
        if (jumps == null || jumps.isEmpty()) {
            return;
        }
        jumps.stream().filter(j -> j.isEntryPoint).findFirst().ifPresent(j -> {
            currentWorkerIndex = j.workerIndex;
            currentTempITypes = j.tempITypes;
        });
        jumps.stream().filter(j -> j.rAType != null && !"unknown".equals(j.rAType)).findFirst().ifPresent(j -> currentRAType = j.rAType);
        jumps.stream().filter(j -> j.rXType != null && !"unknown".equals(j.rXType)).findFirst().ifPresent(j -> currentRXType = j.rXType);
        jumps.stream().filter(j -> j.rYType != null && !"unknown".equals(j.rYType)).findFirst().ifPresent(j -> currentRYType = j.rYType);
    }

    protected void processInstruction(ScriptInstruction ins) {
        final int opcode = ins.opcode;
        final int argv = ins.argv;
        StackObject p1 = null, p2 = null, p3 = null;
        try {
            switch (getStackPops(opcode)) {
                case 3: p3 = stack.pop();
                case 2: p2 = stack.pop();
                case 1: p1 = stack.pop();
                case 0:
                default:
                    break;
            }
        } catch (EmptyStackException e) {
            warningsOnLine.add("Empty stack for opcode " + StringHelper.formatHex2(opcode));
            return;
        }
        if (opcode == 0x00 || opcode == 0x1D || opcode == 0x1E) { // NOP, LABEL, TAG
            // No handling yet, they should probably be written a certain way parsed out but are never actually used
        } else {
            ScriptWorker currentWorker = getWorker(currentWorkerIndex);
            if (opcode >= 0x01 && opcode <= 0x18) {
                ScriptOpcode op = ScriptOpcode.OPCODES[opcode];
                String resultType = op.type;
                String p1s = p1.toString();
                String p2s = p2.toString();

                if (opcode == 0x03 || opcode == 0x05 || opcode == 0x06 || opcode == 0x07) {
                    String p1t = resolveType(p1);
                    String p2t = resolveType(p2);
                    boolean p1w = !p1.expression && isWeakType(p1t);
                    boolean p2w = !p2.expression && isWeakType(p2t);
                    if (INFER_BITWISE_OPS_AS_BITFIELDS && (opcode == 0x03 || opcode == 0x05)) {
                        if (p1w) {
                            p1t = opcode == 0x05 && inferIsNegationValue(p1) ? "bitfieldNegated" : "bitfield";
                            p1 = new StackObject(p1t, p1);
                            p1s = p1.toString();
                        }
                        if (p2w) {
                            p2t = opcode == 0x05 && inferIsNegationValue(p2) ? "bitfieldNegated" : "bitfield";
                            p2 = new StackObject(p2t, p2);
                            p2s = p2.toString();
                        }
                    }
                    if (isWeakType(p1t) && !isWeakType(p2t)) {
                        if (opcode == 0x05 && ("bitfield".equals(p2t) || p2t.endsWith("Bitfield")) && inferIsNegationValue(p1)) {
                            p1s = typed(p1, p2t + "Negated");
                        } else {
                            p1s = typed(p1, p2t);
                        }
                    } else if (isWeakType(p2t) && !isWeakType(p1t)) {
                        if (opcode == 0x05 && ("bitfield".equals(p1t) || p1t.endsWith("Bitfield")) && inferIsNegationValue(p2)) {
                            p2s = typed(p2, p1t + "Negated");
                        } else {
                            p2s = typed(p2, p1t);
                        }
                    }
                }
                if (p1.maybeBracketize) {
                    p1s = '(' + p1s + ')';
                }
                if (p2.maybeBracketize) {
                    p2s = '(' + p2s + ')';
                }
                String content = String.format(op.format, p1s, p2s);
                StackObject stackObject = new StackObject(currentWorker, ins, resultType, true, content);
                stackObject.maybeBracketize = true;
                stack.push(stackObject);
            } else if (opcode == 0x19) { // OPNOT / NOT_LOGIC
                String p1s = p1.toString();
                if (p1.maybeBracketize) {
                    p1s = '(' + p1s + ')';
                }
                stack.push(new StackObject(currentWorker, ins, "bool", true, "!" + p1s));
            } else if (opcode == 0x1A) { // OPUMINUS / NEG
                String p1s = p1.toString();
                if (p1.maybeBracketize) {
                    p1s = '(' + p1s + ')';
                }
                stack.push(new StackObject(currentWorker, ins, p1.type, true, "-" + p1s));
            } else if (opcode == 0x1C) { // OPBNOT / NOT
                String p1s = p1.toString();
                if (p1.maybeBracketize) {
                    p1s = '(' + p1s + ')';
                }
                stack.push(new StackObject(currentWorker, ins, p1.type, true, "~" + p1s));
            } else if (opcode == 0x25) { // POPA / SET_RETURN_VALUE
                textScriptLine += p1 + ";";
                currentRAType = resolveType(p1);
            } else if (opcode == 0x26) { // PUSHA / GET_RETURN_VALUE
                stack.push(new StackObject(currentWorker, ins, currentRAType, true, "LastCallResult"));
            } else if (opcode == 0x28) { // PUSHX / GET_TEST
                stack.push(new StackObject(currentWorker, ins, currentRXType, true, "test"));
            } else if (opcode == 0x29) { // PUSHY / GET_CASE
                stack.push(new StackObject(currentWorker, ins, currentRYType, true, "case"));
            } else if (opcode == 0x2A) { // POPX / SET_TEST
                textScriptLine += "Set test = " + p1;
                currentRXType = resolveType(p1);
            } else if (opcode == 0x2B) { // REPUSH / COPY
                if ("float".equals(resolveType(p1))) {
                    warningsOnLine.add("Repush of float value does not work!");
                }
                stack.push(new StackObject(p1.type, p1));
                stack.push(new StackObject(p1.type, p1));
            } else if (opcode == 0x2C) { // POPY / SET_CASE
                textScriptLine += "switch " + p1;
                currentRYType = resolveType(p1);
            } else if (opcode == 0x34) { // RTS / RETURN
                textScriptLine += "return from subroutine;";
                resetRegisterTypes();
            } else if (opcode >= 0x36 && opcode <= 0x38) { // REQ / SIG_NOACK
                String cmd = "run";
                if (opcode == 0x37) { // REQSW / SIG_ONSTART
                    cmd += "AndAwaitStart";
                } else if (opcode == 0x38) { // REQEW / SIG_ONEND
                    cmd += "AndAwaitEnd";
                }
                String level = p1.expression ? ""+p1 : ""+p1.valueSigned;
                ScriptWorker worker = getWorker(p2.valueSigned);
                ScriptJump entryPoint = worker != null ? worker.getEntryPoint(p3.valueSigned) : null;
                boolean direct = !p2.expression && !p3.expression && isWeakType(p2.type) && isWeakType(p3.type) && entryPoint != null;
                String w = p2.expression ? "(" + p2 + ")" : format2Or4Byte(p2.valueSigned);
                String e = p3.expression ? "(" + p3 + ")" : format2Or4Byte(p3.valueSigned);
                String scriptLabel = direct ? entryPoint.getLabel() : ("w" + w + "e" + e);
                String content = cmd + " " + scriptLabel + " (Level " + level + ")";
                stack.push(new StackObject(currentWorker, ins, "worker", true, content));
            } else if (opcode == 0x39) { // PREQ
                String content = "PREQ(" + p1 + ", " + p2 + ", " + p3 + ")";
                stack.push(new StackObject(currentWorker, ins, "unknown", true, content));
            } else if (opcode == 0x3C) { // RET / END
                textScriptLine += "return;";
                resetRegisterTypes();
            } else if (opcode == 0x3D) { // Never used: RETN / CLEANUP_END
            } else if (opcode == 0x3E) { // Never used: RETT / TO_MAIN
            } else if (opcode == 0x3F) { // RETTN / CLEANUP_TO_MAIN
                textScriptLine += "return (RETTN): " + p1;
                resetRegisterTypes();
            } else if (opcode == 0x40) { // HALT / DYNAMIC
                textScriptLine += "halt";
                resetRegisterTypes();
            } else if (opcode == 0x46) { // TREQ
                String content = "TREQ(" + p1 + ", " + p2 + ", " + p3 + ")";
                stack.push(new StackObject(currentWorker, ins, "unknown", true, content));
            } else if (opcode == 0x54) { // DRET / CLEANUP_ALL_END
                textScriptLine += "direct return;";
                resetRegisterTypes();
            } else if (opcode >= 0x59 && opcode <= 0x5C) { // POPI0..3 / SET_INT
                String p1t = resolveType(p1);
                int tempIndex = opcode - 0x59;
                String tmpIType = currentTempITypes.get(tempIndex);
                if (isWeakType(tmpIType)) {
                    tmpIType = p1t;
                    currentTempITypes.put(tempIndex, p1t);
                }
                String val = typed(p1, tmpIType);
                textScriptLine += "Set tmpI" + tempIndex + " = " + val + ";";
            } else if (opcode >= 0x5D && opcode <= 0x66) { // POPF0..9 / SET_FLOAT
                int tempIndex = opcode - 0x5D;
                textScriptLine += "Set tmpF" + tempIndex + " = " + p1 + ";";
            } else if (opcode >= 0x67 && opcode <= 0x6A) { // PUSHI0..3 / GET_INT
                int tempIndex = opcode - 0x67;
                StackObject stackObject = new StackObject(currentWorker, ins, "tmpI", true, "tmpI" + tempIndex);
                stackObject.referenceIndex = tempIndex;
                stack.push(stackObject);
            } else if (opcode >= 0x6B && opcode <= 0x74) { // PUSHF0..9 / GET_FLOAT
                int tempIndex = opcode - 0x6B;
                StackObject stackObject = new StackObject(currentWorker, ins, "float", true, "tmpF" + tempIndex);
                stackObject.referenceIndex = tempIndex;
                stack.push(stackObject);
            } else if (opcode == 0x77) { // REQWAIT / WAIT_DELETE
                ScriptWorker worker = getWorker(p1.valueSigned);
                ScriptJump entryPoint = worker != null ? worker.getEntryPoint(p2.valueSigned) : null;
                boolean direct = !p1.expression && !p2.expression && isWeakType(p1.type) && isWeakType(p2.type) && entryPoint != null;
                String w = p1.expression ? "(" + p1 + ")" : format2Or4Byte(p1.valueSigned);
                String e = p2.expression ? "(" + p2 + ")" : format2Or4Byte(p2.valueSigned);
                String scriptLabel = direct ? entryPoint.getLabel() : ("w" + w + "e" + e);
                textScriptLine += "await " + scriptLabel + ";";
            } else if (opcode == 0x78) { // Never used: PREQWAIT / WAIT_SPEC_DELETE
            } else if (opcode == 0x79) { // REQCHG / EDIT_ENTRY_TABLE
                int oldIdx = p2.valueSigned + 2;
                int newIdx = p3.valueSigned;
                ScriptJump oldEntryPoint = currentWorker.getEntryPoint(oldIdx);
                ScriptJump newEntryPoint = currentWorker.getEntryPoint(newIdx);
                boolean direct = !p2.expression && !p3.expression && isWeakType(p2.type) && isWeakType(p3.type) && oldEntryPoint != null && newEntryPoint != null;
                String oldScriptLabel = direct ? oldEntryPoint.getLabel() : ("e" + (p2.expression ? "(" + p2 + ")" : format2Or4Byte(oldIdx)));
                String newScriptLabel = direct ? newEntryPoint.getLabel() : ("e" + (p3.expression ? "(" + p3 + ")" : format2Or4Byte(newIdx)));
                String tableHolder = p1.expression ? ""+p1 : ""+p1.valueSigned;
                if (p1.parentInstruction.opcode == 0xA7) {
                    addVarType(p1.valueSigned, "workerEventTable");
                }
                textScriptLine += "Replace script " + oldScriptLabel + " with " + newScriptLabel + " (store table at " + tableHolder + ")";;
                // textScriptLine += "REQCHG(" + p1 + ", " + p2 + ", " + p3 + ");";
            } else if (opcode == 0x7A) { // Never used: ACTREQ / SET_EDGE_TRIGGER
            } else if (opcode == 0x9F) { // PUSHV / GET_DATUM
                StackObject stackObject = new StackObject(currentWorker, ins, "var", true, getVariableLabel(argv));
                stackObject.referenceIndex = argv;
                stack.push(stackObject);
            } else if (opcode == 0xA0 || opcode == 0xA1) { // POPV(L) / SET_DATUM_(W/T)
                addVarType(argv, resolveType(p1));
                if (gatheringInfo) {
                    if (!varEnums.containsKey(argv)) {
                        varEnums.put(argv, new ArrayList<>());
                    }
                    varEnums.get(argv).add(p1);
                }
                textScriptLine += "Set ";
                if (opcode == 0xA1) {
                    textScriptLine += "(limit) ";
                }
                String val = typed(p1, getVariableType(argv));
                textScriptLine += getVariableLabel(argv) + " = " + val + ";";
            } else if (opcode == 0xA2) { // PUSHAR / GET_DATUM_INDEX
                StackObject stackObject = new StackObject(currentWorker, ins, "var", true, ensureVariableValidWithArray(argv, p1));
                stackObject.referenceIndex = argv;
                stack.push(stackObject);
            } else if (opcode == 0xA3 || opcode == 0xA4) { // POPAR(L) / SET_DATUM_INDEX_(W/T)
                addVarType(argv, resolveType(p2));
                textScriptLine += "Set ";
                if (opcode == 0xA4) {
                    textScriptLine += "(limit) ";
                }
                String val = typed(p2, getVariableType(argv));
                textScriptLine += ensureVariableValidWithArray(argv, p1) + " = " + val + ";";
            } else if (opcode == 0xA7) { // PUSHARP / GET_DATUM_DESC
                StackObject stackObject = new StackObject(currentWorker, ins, "pointer", true, "&" + ensureVariableValidWithArray(argv, p1));
                stackObject.referenceIndex = argv;
                stack.push(stackObject);
            } else if (opcode == 0xAD) { // PUSHI / CONST_INT
                int refInt = refInts[argv];
                StackObject stackObject = new StackObject(currentWorker, ins, "int32", refInt, refInt);
                stackObject.referenceIndex = argv;
                stack.push(stackObject);
            } else if (opcode == 0xAE) { // PUSHII / IMM
                StackObject stackObject = new StackObject(currentWorker, ins, "int16", ins.argvSigned, ins.argv);
                stack.push(stackObject);
            } else if (opcode == 0xAF) { // PUSHF / CONST_FLOAT
                int refFloat = refFloats[argv];
                StackObject stackObject = new StackObject(currentWorker, ins, "float", refFloat, refFloat);
                stackObject.referenceIndex = argv;
                stack.push(stackObject);
            } else if (opcode == 0xB0) { // JMP / JUMP
                ScriptJump jump = referenceJump(argv);
                textScriptLine += "Jump to " + (jump != null ? jump.getLabelWithAddr() : ("j" + StringHelper.formatHex2(argv)));
                resetRegisterTypes();
            } else if (opcode == 0xB1) { // Never used: CJMP / BNEZ
            } else if (opcode == 0xB2) { // Never used: NCJMP / BEZ
            } else if (opcode == 0xB3) { // JSR
                ScriptWorker worker = getWorker(argv);
                textScriptLine += "Jump to subroutine " + (worker != null ? worker.getIndexLabel() : ("w" + StringHelper.formatHex2(argv)));
            } else if (opcode == 0xB5) { // CALL / FUNC_RET
                List<StackObject> params = popParamsForFunc(argv);
                ScriptFunc func = getAndTypeFuncCall(argv, params);
                StackObject stackObject = new StackObject(currentWorker, ins, func.getType(params), true, func.callB5(params, null));
                stackObject.referenceIndex = argv;
                stack.push(stackObject);
            } else if (opcode == 0xD6) { // POPXCJMP / SET_BNEZ
                ScriptJump jump = referenceJump(argv);
                textScriptLine += "(" + p1 + ") -> " + (jump != null ? jump.getLabelWithAddr() : ("j" + StringHelper.formatHex2(argv)));
            } else if (opcode == 0xD7) { // POPXNCJMP / SET_BEZ
                ScriptJump jump = referenceJump(argv);
                textScriptLine += "Check (" + p1 + ") else jump to " + (jump != null ? jump.getLabelWithAddr() : ("j" + StringHelper.formatHex2(argv)));
            } else if (opcode == 0xD8) { // CALLPOPA / FUNC
                List<StackObject> params = popParamsForFunc(argv);
                ScriptFunc func = getAndTypeFuncCall(argv, params);
                currentRAType = func.getType(params);
                String call = func.callD8(params, null);
                textScriptLine += call + ';';
            } else if (opcode == 0xF6) { // SYSTEM
                textScriptLine += "System " + StringHelper.formatHex2(argv);
            }
        }
    }

    private ScriptJump referenceJump(int argv) {
        ScriptWorker worker = getWorker(currentWorkerIndex);
        if (worker == null) {
            return null;
        }
        return referenceJump(worker.getJump(argv));
    }

    private ScriptJump referenceJump(ScriptJump jump) {
        if (jump == null) {
            return null;
        }
        if (jump.hardMisaligned) {
            warningsOnLine.add("Referencing broken jump: " + jump.getLabelWithAddr());
        }
        setJumpTypes(jump);
        return jump;
    }

    private void setJumpTypes(ScriptJump jump) {
        if (jump == null) {
            return;
        }
        jump.setTypes(currentRAType, currentRXType, currentRYType, currentTempITypes);
    }

    protected List<StackObject> popParamsForFunc(int idx) {
        List<StackObject> params = new ArrayList<>();
        try {
            int functionParamCount = getFunctionParamCount(idx);
            switch (functionParamCount) {
                case 9: params.add(0, stack.pop());
                case 8: params.add(0, stack.pop());
                case 7: params.add(0, stack.pop());
                case 6: params.add(0, stack.pop());
                case 5: params.add(0, stack.pop());
                case 4: params.add(0, stack.pop());
                case 3: params.add(0, stack.pop());
                case 2: params.add(0, stack.pop());
                case 1: params.add(0, stack.pop());
                case 0:
                default:
                    break;
            }
        } catch (EmptyStackException e) {
            warningsOnLine.add("Empty stack for func " + StringHelper.formatHex4(idx));
        }
        return params;
    }

    protected ScriptFunc getAndTypeFuncCall(int idx, List<StackObject> params) {
        ScriptFunc func = ScriptFuncLib.FFX.get(idx, params);
        if (func == null) {
            func = new ScriptFunc("Unknown:" + StringHelper.formatHex4(idx), "unknown", null, false);
        }
        List<ScriptField> inputs = func.inputs;
        if (inputs != null && !inputs.isEmpty() && !params.isEmpty()) {
            int len = Math.min(inputs.size(), params.size());
            for (int i = 0; i < len; i++) {
                typed(params.get(i), inputs.get(i).type);
            }
        }
        return func;
    }

    protected void addVarType(int var, String type) {
        if (!gatheringInfo) {
            return;
        }
        ScriptVariable variable = getVariable(var);
        if (variable != null) {
            variable.inferType(type);
        }
    }

    protected String resolveType(StackObject obj) {
        if (obj == null || obj.type == null) {
            return "unknown";
        }
        if ("var".equals(obj.type)) {
            return getVariableType(obj.referenceIndex);
        }
        if ("tmpI".equals(obj.type)) {
            return currentTempITypes.getOrDefault(obj.referenceIndex, "unknown");
        }
        return obj.type;
    }

    protected static boolean isWeakType(String type) {
        return type == null || "unknown".equals(type);
    }

    protected String typed(StackObject obj, String type) {
        if (obj == null) {
            return type + ":null";
        } else {
            if ("var".equals(obj.type)) {
                addVarType(obj.referenceIndex, type);
            }
            if ("tmpI".equals(obj.type) && type != null && !"unknown".equals(type)) {
                currentTempITypes.put(obj.referenceIndex, type);
            }
            if (obj.expression || type == null || "unknown".equals(type)) {
                return obj.toString();
            } else {
                return new StackObject(type, obj).toString();
            }
        }
    }

    public ScriptVariable getVariable(int index) {
        if (variableDeclarations != null && index >= 0 && index < variableDeclarations.size()) {
            return variableDeclarations.get(index);
        }
        return null;
    }

    public String getVariableType(int index) {
        ScriptVariable variable = getVariable(index);
        if (variable != null) {
            return variable.getType();
        }
        warningsOnLine.add("Variable index " + StringHelper.formatHex2(index) + " out of bounds!");
        return "unknown";
    }

    public String getVariableLabel(int index) {
        ScriptVariable variable = getVariable(index);
        if (variable != null) {
            return variable.getLabel(getWorker(currentWorkerIndex));
        }
        String hexIdx = StringHelper.formatHex2(index);
        warningsOnLine.add("Variable index " + hexIdx + " out of bounds!");
        return "var" + hexIdx;
    }

    public void addNewVariable() {
        ScriptVariable newVar = new ScriptVariable(workers.getFirst(), variableDeclarations.size(), 0, 0);
        newVar.location = 3;
        newVar.format = 3;
        newVar.declaredType = "int";
        newVar.elementCount = 1;
        variableDeclarations.add(newVar);
    }

    private String ensureVariableValidWithArray(int index, StackObject p1) {
        String varLabel = getVariableLabel(index);
        String indexType = resolveType(p1);
        if (isWeakType(indexType) && (variableDeclarations != null && index >= 0 && index < variableDeclarations.size())) {
            indexType = variableDeclarations.get(index).getArrayIndexType();
        }
        String arrayIndex = !p1.expression && isWeakType(indexType) ? ""+p1.valueSigned : typed(p1, indexType);
        return varLabel + '[' + arrayIndex + ']';
    }

    private boolean inferIsNegationValue(StackObject obj) {
        int inactiveBits = StackObject.negatedBitfieldToList(obj.type, obj.valueUnsigned).size();
        if (inactiveBits == 1) {
            return true;
        }
        int activeBits = StackObject.bitfieldToList(obj.type, obj.valueUnsigned).size();
        return inactiveBits < activeBits;
    }

    protected static boolean hasArgs(int opcode) {
        return opcode >= 0x80 && opcode != 0xFF;
    }

    protected int getStackPops(int opcode) {
        return ScriptOpcode.OPCODES[opcode].inputs.size();
    }

    protected int getFunctionParamCount(int idx) {
        ScriptFunc func = ScriptFuncLib.FFX.get(idx, null);
        if (func == null) {
            warningsOnLine.add("Undefined stackpops for func " + StringHelper.formatHex4(idx));
            return 0;
        }
        return func.inputs != null ? func.inputs.size() : 0;
    }

    protected static boolean getLineEnd(int opcode) {
        return ScriptOpcode.OPCODES[opcode].isLineEnd;
    }

    protected void inferBooleans() {
        if (variableDeclarations == null) {
            return;
        }
        for (int varIdx = 0; varIdx < variableDeclarations.size(); varIdx++) {
            ScriptVariable var = getVariable(varIdx);
            if (isWeakType(var.inferredType) && varEnums.containsKey(varIdx)) {
                List<StackObject> enums = varEnums.get(varIdx);
                if (enums.size() == 1 && !enums.get(0).expression) {
                    constants.put(varIdx, enums.get(0));
                } else if (enums.stream().noneMatch(a -> a.expression)) {
                    Set<Integer> distinctContents = enums.stream().map(a -> a.valueSigned).collect(Collectors.toSet());
                    if (distinctContents.size() == 2 && distinctContents.contains(0) && (distinctContents.contains(0x01) || distinctContents.contains(0xFF))) {
                        var.inferType("bool");
                    }
                }
            }
        }
    }

    private int read2Bytes(int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100;
    }

    private int read4Bytes(int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100 + bytes[offset+2] * 0x10000 + bytes[offset+3] * 0x1000000;
    }

    private static String format2Or4Byte(int b) {
        return String.format(b > 0x100 ? "%04X" : "%02X", b);
    }

    private void resetRegisterTypes() {
        if (!currentExecutionLines.isEmpty()) {
            // currentExecutionLines.forEach(this::setJumpTypes);
            currentExecutionLines = new ArrayList<>();
        }
        currentRAType = "unknown";
        currentRXType = "unknown";
        currentRYType = "unknown";
    }

    @Override
    public String toString() {
        return "- Script Code -" + '\n' +
                allLinesString() +
                "- Script Workers -" + '\n' +
                workersString() + '\n';
    }

    public String allLinesString() {
        if (PRINT_CODE_BY_ENTRY_POINTS) {
            return allWorkerCodeString();
        }
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < lineCount; i++) {
            lines.add(fullLineString(i));
        }
        return String.join("\n", lines) + '\n';
    }

    public String fullLineString(int line) {
        String ol = String.format("%-5s", offsetLines.get(line) + ' ');
        String jl = consoleColorIfEnabled(ANSI_PURPLE) + String.format("%-" + JUMP_LINE_MINLENGTH + "s", jumpLines.get(line)) + ' ';
        String jhl = String.format("%-" + JUMP_PLUS_HEX_LINE_MINLENGTH + "s", jl + consoleColorIfEnabled(ANSI_BLUE) +  hexScriptLines.get(line)) + ' ';
        String tl = consoleColorIfEnabled(ANSI_RESET) + textScriptLines.get(line);
        String warnLine = warnLines.get(line);
        String wl = warnLine == null || warnLine.isEmpty() ? "" : (consoleColorIfEnabled(ANSI_RED) + warnLine);
        return ol + jhl + tl + wl + consoleColorIfEnabled(ANSI_RESET);
    }

    public String allWorkerCodeString() {
        List<String> lines = new ArrayList<>();
        for (ScriptWorker worker : workers) {
            lines.add("w" + StringHelper.formatHex2(worker.workerIndex));
            worker.parseWorkerAtelCode(actualScriptCodeBytes);
            for (ScriptJump ep : worker.entryPoints) {
                lines.add(ep.getLabel());
                lines.add(ep.getLinesString());
                lines.add("");
            }
        }
        return String.join("\n", lines) + '\n';
    }

    public String allInstructionsAsmString() {
        List<String> lines = new ArrayList<>();
        int offset = 0;
        for (ScriptInstruction ins : instructions) {
            String ol = String.format("%-6s", StringHelper.formatHex4(offset) + ' ');
            String jl = consoleColorIfEnabled(ANSI_PURPLE) + String.format("%-11s", getJumpLine(scriptJumpsByDestination.get(offset)) + ' ');
            String hl = consoleColorIfEnabled(ANSI_BLUE) + String.format("%-11s", ins.asSeparatedHexString() + ' ');
            String asml = consoleColorIfEnabled(ANSI_GREEN) + (ins.hasArgs ? String.format("%-10s", ins.getOpcodeLabel() + ' ') + consoleColorIfEnabled(ANSI_YELLOW) + ins.getArgLabel() : ins.getOpcodeLabel());
            lines.add(ol + jl + hl + asml + consoleColorIfEnabled(ANSI_RESET));
            offset += ins.length;
        }
        return String.join("\n", lines) + '\n';
    }

    public String workersString() {
        if (workers == null || workers.isEmpty()) {
            return "No Workers";
        }
        List<String> lines = new ArrayList<>();
        lines.add("Script Code Start Address: " + StringHelper.formatHex4(scriptCodeStartAddress));
        if (creatorTag != null) {
            lines.add("Creator: " + creatorTag);
        }
        if (mainScriptIndex != 0xFFFF) {
            lines.add("Main Worker: w" + StringHelper.formatHex2(mainScriptIndex));
        }
        if (mapEntrances != null) {
            lines.add(mapEntrances.length + " Map Entrances");
            for (int i = 0; i < mapEntrances.length; i++) {
                lines.add("Entrance " + StringHelper.formatHex2(i) + " " + mapEntrances[i].toString());
            }
        }
        if (unknown1A != 0) {
            lines.add("unknown1A=" + StringHelper.formatHex4(unknown1A));
        }
        if (PRINT_META_STRUCT) {
            if (scriptMetaStruct != null) {
                lines.add("Meta Struct");
                lines.add(scriptMetaStruct.toString());
            } else {
                lines.add("Meta Struct is null");
            }
        }
        if (areaNameIndexes != null) {
            int firstAreaNameIndex = areaNameIndexes.get(0);
            List<Integer> differentAreaNameIndexes = areaNameIndexes.stream().filter((id) -> id != firstAreaNameIndex).toList();
            lines.add("Area Names");
            lines.add(MACRO_LOOKUP.get(0xB00 + firstAreaNameIndex).toString());
            differentAreaNameIndexes.forEach(i -> lines.add(MACRO_LOOKUP.get(0xB00 + i).toString()));
        }
        lines.add(namespaceCount > 1 ? namespaceCount + " Workers Total" : "1 Worker Total");
        for (int i = 0; i < namespaceCount; i++) {
            lines.add("w" + StringHelper.formatHex2(i) + ": " + getWorker(i).getNonCommonString());
        }
        lines.add("Variables (" + variableDeclarations.size() + " at offset " + StringHelper.formatHex4(variableStructsTableOffset) + ")");
        if (variableDeclarations.size() > 0) {
            List<String> refsStrings = new ArrayList<>();
            for (int i = 0; i < variableDeclarations.size(); i++) {
                refsStrings.add("Variable " + StringHelper.formatHex2(i) + ": " + getVariable(i));
            }
            lines.add(String.join("\n", refsStrings));
        }
        if (PRINT_REF_INTS_FLOATS) {
            lines.add("Integers (" + refInts.length + " at offset " + StringHelper.formatHex4(intTableOffset) + ")");
            if (refInts.length > 0) {
                List<String> intStrings = new ArrayList<>();
                for (int i = 0; i < refInts.length; i++) {
                    intStrings.add("refI" + StringHelper.formatHex2(i) + ": " + refInts[i] + " [" + String.format("%08X", refInts[i]) + "h]");
                }
                lines.add(String.join(", ", intStrings));
            }
            lines.add("Floats (" + refFloats.length + " at offset " + StringHelper.formatHex4(floatTableOffset) + ")");
            if (refFloats.length > 0) {
                List<String> floatStrings = new ArrayList<>();
                for (int i = 0; i < refFloats.length; i++) {
                    floatStrings.add("refF" + StringHelper.formatHex2(i) + ": " + Float.intBitsToFloat(refFloats[i]) + " [" + String.format("%08X", refFloats[i]) + "h]");
                }
                lines.add(String.join(", ", floatStrings));
            }
        }
        if (PRINT_JUMP_TABLE) {
            lines.add("- Jump Table -");
            for (int i = 0; i < namespaceCount; i++) {
                lines.add("w" + StringHelper.formatHex2(i));
                ScriptWorker h = getWorker(i);
                lines.add(h.getEntryPointsLine());
                lines.add(h.getJumpsLine());
            }
        }
        return lines.stream().filter(s -> s != null && !s.isEmpty()).collect(Collectors.joining("\n"));
    }

    public int[] getBytes() {
        return bytes;
    }

    public record AtelScriptObjectBytes(int[] bytes, int[] battleWorkerMappingBytes) {}
}
