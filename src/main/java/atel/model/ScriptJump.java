package atel.model;

import atel.AtelScriptObject;
import main.DataAccess;
import main.StringHelper;
import model.CommandDataObject;

import java.util.*;

public class ScriptJump {
    private static final boolean OPTIMIZE_REDUNDANT_B0_INSTRUCTIONS = false;

    public ScriptWorker parentWorker;
    public AtelScriptObject parentScript;
    public int addr;
    public final int workerIndex;
    public final int jumpIndex;
    public final boolean isEntryPoint;

    public String rAType;
    public String rXType;
    public String rYType;
    public Map<Integer, String> tempITypes = new HashMap<>();
    public List<ScriptJump> reachableFrom;
    public boolean hardMisaligned = false;
    public ScriptLine targetLine;
    public Integer battleWorkerEntryPointSlot;

    private String label;

    public ScriptJump(ScriptWorker parentWorker, int addr, int jumpIndex, boolean isEntryPoint) {
        this.parentWorker = parentWorker;
        this.parentScript = parentWorker != null ? parentWorker.parentScript : null;
        this.addr = addr;
        this.workerIndex = parentWorker != null ? parentWorker.workerIndex : -1;
        this.jumpIndex = jumpIndex;
        this.isEntryPoint = isEntryPoint;
    }

    public void markAsHardMisaligned() {
        this.hardMisaligned = true;
    }

    public String getLabel() {
        return label != null ? label : getDefaultLabel();
    }

    public String getLabelWithAddr() {
        return getLabel() + " (" + StringHelper.formatHex4(addr) + ")";
    }

    public List<ScriptInstruction> toInstructionList() {
        List<ScriptInstruction> list = new ArrayList<>();
        List<ScriptLine> lines = getLines();
        lines.forEach(line -> list.addAll(line.instructions));
        return list;
    }

    public List<ScriptLine> getLines() {
        List<ScriptLine> list = new ArrayList<>();
        Stack<ScriptLine> linesToCheck = new Stack<>();
        linesToCheck.push(targetLine);
        ScriptLine lastLine = null;
        while (!linesToCheck.isEmpty()) {
            ScriptLine cursor = linesToCheck.pop();
            while (cursor.predecessor != null && !list.contains(cursor.predecessor)) {
                cursor = cursor.predecessor;
            }
            while (cursor != null && !list.contains(cursor)) {
                if (OPTIMIZE_REDUNDANT_B0_INSTRUCTIONS && lastLine != null && !lastLine.continues() && lastLine.branch != null && cursor.equals(lastLine.branch.targetLine)) {
                    if (lastLine.incomingJumps != null && !lastLine.incomingJumps.isEmpty()) {
                        cursor.incomingJumps.addAll(lastLine.incomingJumps);
                        lastLine.incomingJumps = new ArrayList<>();
                    }
                    list.remove(list.size() - 1);
                }
                list.add(cursor);
                ScriptJump branch = cursor.branch;
                if (branch != null) {
                    if (cursor.continues()) {
                        linesToCheck.add(0, branch.targetLine);
                    } else {
                        linesToCheck.push(branch.targetLine);
                        if (OPTIMIZE_REDUNDANT_B0_INSTRUCTIONS && cursor.incomingJumps != null && !cursor.incomingJumps.isEmpty()) {
                            branch.targetLine.incomingJumps.addAll(cursor.incomingJumps);
                            cursor.incomingJumps = new ArrayList<>();
                        }
                    }
                }
                lastLine = cursor;
                cursor = cursor.continues() ? cursor.successor : null;
            }
        }
        return list;
    }

    public String getLinesString() {
        List<ScriptLine> alreadyVisited = new ArrayList<>();
        List<String> lineStrings = new ArrayList<>();
        Stack<ScriptJump> linesToCheck = new Stack<>();
        linesToCheck.push(this);
        while (!linesToCheck.isEmpty()) {
            ScriptJump entryPoint = linesToCheck.pop();
            ScriptState state = new ScriptState(entryPoint);
            ScriptLine cursor = entryPoint.targetLine;
            while (cursor != null && !alreadyVisited.contains(cursor)) {
                alreadyVisited.add(cursor);
                ScriptJump branch = cursor.branch;
                if (branch != null) {
                    branch.setTypes(state);
                    if (cursor.continues()) {
                        linesToCheck.add(0, branch);
                    } else {
                        linesToCheck.push(branch);
                    }
                }
                lineStrings.add(cursor.asString(state));
                cursor = cursor.continues() ? cursor.successor : null;
            }
        }
        return String.join("\n", lineStrings);
    }

    public String getDefaultLabel() {
        if (!isEntryPoint) {
            return "j" + StringHelper.formatHex2(jumpIndex);
        } else {
            String wPrefix = "w" + StringHelper.formatHex2(parentWorker.workerIndex);
            if (jumpIndex == 0) {
                return wPrefix + "init";
            } else if (jumpIndex == 1) {
                return wPrefix + "main";
            } else {
                if (parentWorker.battleWorkerType != null) {
                    String wePrefix = wPrefix + "e" + StringHelper.formatHex2(jumpIndex);
                    return wePrefix + battleWorkerEntryPointToString();
                } else {
                    return wPrefix + eventWorkerEntryPointToStringWithFallback(parentWorker.eventWorkerType, jumpIndex);
                }
            }
        }
    }

    public void setTypes(ScriptState state) {
        this.rAType = state.rAType;
        this.rXType = state.rXType;
        this.rYType = state.rYType;
    }

    public void setTypes(String rAType, String rXType, String rYType, Map<Integer, String> tempITypes) {
        this.rAType = rAType;
        this.rXType = rXType;
        this.rYType = rYType;
        // this.tempITypes = tempITypes;
    }

    public void setBattleWorkerEntryPointSlot(int entryPointSlot) {
        battleWorkerEntryPointSlot = entryPointSlot;
    }

    private String battleWorkerEntryPointToString() {
        int battleWorkerType = parentWorker.battleWorkerType != null ? parentWorker.battleWorkerType : -1;
        int battleWorkerSlot = parentWorker.purposeSlot != null ? parentWorker.purposeSlot : -1;
        if (battleWorkerType == 0) {
            ScriptField s = ScriptConstants.getEnumMap("cameraHandlerTag").get(battleWorkerEntryPointSlot);
            if (s != null) {
                return "Cam" + s.getLabel();
            }
        } else if (battleWorkerType == 1) {
            ScriptField s = ScriptConstants.getEnumMap("motionHandlerTag").get(battleWorkerEntryPointSlot);
            if (s != null) {
                return "Motion" + s.getLabel();
            }
        } else if (battleWorkerType == 2) {
            ScriptField s = ScriptConstants.getEnumMap("combatHandlerTag").get(battleWorkerEntryPointSlot);
            if (s != null) {
                String prefix = "";
                if (battleWorkerSlot >= 0x25 && battleWorkerSlot <= 0x28) {
                    String chr = StackObject.enumToScriptField("playerChar", battleWorkerSlot - 0x17).getName();
                    prefix = "Base" + chr + ".";
                }
                if (battleWorkerSlot >= 0x29 && battleWorkerSlot <= 0x3A) {
                    String chr = StackObject.enumToScriptField("playerChar", battleWorkerSlot - 0x29).getName();
                    prefix = chr + ".";
                }
                return prefix + s.getLabel();
            }
        } else if (battleWorkerType == 3) {
            ScriptField s = ScriptConstants.getEnumMap("battleGruntHandlerTag").get(battleWorkerEntryPointSlot);
            if (s != null) {
                return "Grunt" + s.getLabel();
            }
        } else if (battleWorkerType == 4) {
            return "btlScene" + StringHelper.formatHex2(battleWorkerEntryPointSlot);
        } else if (battleWorkerType == 5) {
            if (battleWorkerSlot >= 0x6D && battleWorkerSlot <= 0x7E) {
                ScriptField s = ScriptConstants.getEnumMap("voiceHandlerTag").get(battleWorkerEntryPointSlot);
                if (s != null) {
                    String chr = StackObject.enumToScriptField("playerChar", battleWorkerSlot - 0x6D).getName();
                    return chr + "Voice" + s.getLabel();
                }
            }
        } else if (battleWorkerType == 6) {
            ScriptField s = ScriptConstants.getEnumMap("battleStartEndHookTag").get(battleWorkerEntryPointSlot);
            if (s != null) {
                return "Hook" + s.getLabel();
            }
        }
        if (battleWorkerSlot == 0x00) {
            return "?BattleCam" + StringHelper.formatHex2(battleWorkerEntryPointSlot);
        }
        if (battleWorkerSlot >= 0x41 && battleWorkerSlot <= 0x4C) {
            int cmd;
            if (battleWorkerSlot == 0x41 || battleWorkerSlot == 0x42 || battleWorkerSlot == 0x43) {
                cmd = 0x3000 + battleWorkerEntryPointSlot;
            } else if (battleWorkerSlot == 0x44 || battleWorkerSlot == 0x45 || battleWorkerSlot == 0x46) {
                cmd = 0x2000 + battleWorkerEntryPointSlot;
            } else if (battleWorkerSlot == 0x47 || battleWorkerSlot == 0x48 || battleWorkerSlot == 0x49) {
                cmd = 0x4000 + battleWorkerEntryPointSlot;
            } else {
                cmd = 0x6000 + battleWorkerEntryPointSlot;
            }
            CommandDataObject command = DataAccess.getCommand(cmd);
            if (command != null) {
                return "MagicCam" + StringHelper.formatHex4(cmd) + "(" + command.getName() + ")";
            } else {
                return "MagicCam" + StringHelper.formatHex4(cmd);
            }
        }
        String strWorkerPurposeSlot = "s" + StringHelper.formatHex2(battleWorkerSlot);
        String strWorkerType = "t" + StringHelper.formatHex2(battleWorkerType);
        String strEntryPointPurposeSlot = "p" + StringHelper.formatHex2(battleWorkerEntryPointSlot);
        return strWorkerPurposeSlot + strWorkerType + strEntryPointPurposeSlot;
    }

    private static String eventWorkerEntryPointToStringWithFallback(int eventWorkerType, int eventWorkerEntryPoint) {
        String label = eventWorkerEntryPointToString(eventWorkerType, eventWorkerEntryPoint);
        if (label != null) {
            return label;
        }
        return "e" + StringHelper.formatHex2(eventWorkerEntryPoint);
    }

    private static String eventWorkerEntryPointToString(int eventWorkerType, int eventWorkerEntryPoint) {
        return switch (eventWorkerType) {
            case 1 -> // FieldObject
                    switch (eventWorkerEntryPoint) {
                        case 2 -> "talk";
                        case 3 -> "scout";
                        case 4 -> "fo?c";
                        case 5 -> "touch";
                        default -> null;
                    };
            case 2 -> // PlayerEdge
                    switch (eventWorkerEntryPoint) {
                        case 2 -> "pe?t";
                        case 3 -> "pe?s";
                        case 4 -> "cross";
                        case 5 -> "touch";
                        case 6 -> "pe?e";
                        case 7 -> "pe?l";
                        default -> null;
                    };
            case 3 -> // PlayerZone
                    switch (eventWorkerEntryPoint) {
                        case 2 -> "talk";
                        case 3 -> "scout";
                        case 4 -> "cross";
                        case 5 -> "touch";
                        case 6 -> "enter";
                        case 7 -> "leave";
                        default -> null;
                    };
            case 5 -> // Edge
                    switch (eventWorkerEntryPoint) {
                        case 2 -> "cross";
                        default -> null;
                    };
            case 6 -> // Zone
                    switch (eventWorkerEntryPoint) {
                        case 2 -> "enter";
                        case 3 -> "leave";
                        default -> null;
                    };
            default -> null;
        };
    }
}
