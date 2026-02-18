package atel.model;

import atel.AtelScriptObject;
import main.DataAccess;
import main.StringHelper;
import model.CommandDataObject;

import java.util.*;

public class ScriptJump {
    private static final boolean OPTIMIZE_REDUNDANT_B0_INSTRUCTIONS = true;

    public ScriptWorker parentWorker;
    public AtelScriptObject parentScript;
    public int addr;
    public final int workerIndex;
    public int jumpIndex;
    public final boolean isEntryPoint;

    public String rAType = "unknown";
    public String rXType = "unknown";
    public String rYType = "unknown";
    public Map<Integer, String> tempITypes = new HashMap<>();
    public List<ScriptJump> reachableFrom;
    public boolean hardMisaligned = false;
    public ScriptLine targetLine;
    public Integer battleWorkerEntryPointSlot;

    public String declaredLabel;

    public ScriptJump(ScriptWorker parentWorker, int addr, int jumpIndex, boolean isEntryPoint) {
        this.parentWorker = parentWorker;
        this.parentScript = parentWorker != null ? parentWorker.parentScript : null;
        this.addr = addr;
        this.workerIndex = parentWorker != null ? parentWorker.workerIndex : -1;
        this.jumpIndex = jumpIndex;
        this.isEntryPoint = isEntryPoint;
    }

    public ScriptJump cloneEntryPointRecursively(ScriptWorker targetWorker) {
        if (!isEntryPoint) {
            throw new IllegalStateException("Can only clone entry points!");
        }
        ScriptJump clone = new ScriptJump(targetWorker, -1, targetWorker.entryPoints.size(), true);
        clone.targetLine = targetLine.cloneRecursively(targetWorker, new HashMap<>());
        clone.targetLine.incomingJumps.add(clone);
        return clone;
    }

    public void markAsHardMisaligned() {
        this.hardMisaligned = true;
    }

    public String getLabel() {
        return declaredLabel != null ? declaredLabel : getDefaultLabel();
    }

    public void setDeclaredLabel(String label) {
        declaredLabel = label != null && !label.isEmpty() ? label : null;
    }

    public String getLabelWithAddr() {
        return getLabel() + " (" + StringHelper.formatHex4(addr) + ")";
    }

    public List<ScriptLine> getLines() {
        List<ScriptLine> list = new ArrayList<>();
        Stack<ScriptLine> linesToCheck = new Stack<>();
        linesToCheck.push(targetLine);
        while (!linesToCheck.isEmpty()) {
            ScriptLine cursor = linesToCheck.pop();
            while (cursor.predecessor != null && !list.contains(cursor.predecessor)) {
                cursor = cursor.predecessor;
            }
            while (cursor.successor != null && cursor.incomingJumps.isEmpty()) {
                cursor = cursor.successor;
            }
            while (cursor != null && !list.contains(cursor)) {
                ScriptJump branch = cursor.branch;
                if (branch != null) {
                    if (cursor.continues()) {
                        linesToCheck.add(0, branch.targetLine);
                    } else {
                        linesToCheck.push(branch.targetLine);
                        if (OPTIMIZE_REDUNDANT_B0_INSTRUCTIONS) {
                            List<ScriptJump> selfIncomingJumps = cursor.incomingJumps;
                            if (selfIncomingJumps != null && !selfIncomingJumps.isEmpty()) {
                                branch.targetLine.incomingJumps.addAll(selfIncomingJumps);
                                selfIncomingJumps.forEach(j -> j.targetLine = branch.targetLine);
                                cursor.incomingJumps = new ArrayList<>();
                            }
                        }
                    }
                }
                list.add(cursor);
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
            String wPrefix = parentWorker.getReferenceLabel() + ".";
            if (jumpIndex == 0) {
                return wPrefix + "init";
            } else if (jumpIndex == 1) {
                return wPrefix + "main";
            } else {
                if (parentWorker.battleWorkerType != null) {
                    return wPrefix + battleWorkerEntryPointToString();
                } else {
                    return wPrefix + eventWorkerEntryPointToStringWithFallback(parentWorker.eventWorkerType, jumpIndex);
                }
            }
        }
    }

    public String getIndexLabel() {
        return "w" + StringHelper.formatHex2(parentWorker.workerIndex) + ".e" + StringHelper.formatHex2(jumpIndex);
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

    public boolean canDelete() {
        if (!isEntryPoint) {
            return true;
        }
        if (jumpIndex <= 1) {
            return false;
        }
        if (parentWorker.battleWorkerType != null) {
            return true;
        }
        if (parentWorker.eventWorkerType == 5) {
            return jumpIndex > 2;
        } else if (parentWorker.eventWorkerType == 6) {
            return jumpIndex > 3;
        } else if (parentWorker.eventWorkerType == 1) {
            return jumpIndex > 5;
        } else if (parentWorker.eventWorkerType == 2 || parentWorker.eventWorkerType == 3) {
            return jumpIndex > 7;
        } else {
            return true;
        }
    }

    private String battleWorkerEntryPointToString() {
        int battleWorkerType = parentWorker.battleWorkerType != null ? parentWorker.battleWorkerType : -1;
        int battleWorkerSlot = parentWorker.purposeSlot != null ? parentWorker.purposeSlot : -1;
        int battleWorkerEntryPointSlot = this.battleWorkerEntryPointSlot != null ? this.battleWorkerEntryPointSlot : -1;
        if (battleWorkerType == 0) {
            ScriptField s = ScriptConstants.FFX.getEnumMap("cameraHandlerTag").get(battleWorkerEntryPointSlot);
            if (s != null) {
                return "Cam" + s.getLabel();
            }
        } else if (battleWorkerType == 1) {
            ScriptField s = ScriptConstants.FFX.getEnumMap("motionHandlerTag").get(battleWorkerEntryPointSlot);
            if (s != null) {
                return "Motion" + s.getLabel();
            }
        } else if (battleWorkerType == 2) {
            ScriptField s = ScriptConstants.FFX.getEnumMap("combatHandlerTag").get(battleWorkerEntryPointSlot);
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
            ScriptField s = ScriptConstants.FFX.getEnumMap("battleGruntHandlerTag").get(battleWorkerEntryPointSlot);
            if (s != null) {
                return "Grunt" + s.getLabel();
            }
        } else if (battleWorkerType == 4) {
            return "btlScene" + StringHelper.formatHex2(battleWorkerEntryPointSlot);
        } else if (battleWorkerType == 5) {
            if (battleWorkerSlot >= 0x6D && battleWorkerSlot <= 0x7E) {
                ScriptField s = ScriptConstants.FFX.getEnumMap("voiceHandlerTag").get(battleWorkerEntryPointSlot);
                if (s != null) {
                    String chr = StackObject.enumToScriptField("playerChar", battleWorkerSlot - 0x6D).getName();
                    return chr + "Voice" + s.getLabel();
                }
            }
        } else if (battleWorkerType == 6) {
            ScriptField s = ScriptConstants.FFX.getEnumMap("battleStartEndHookTag").get(battleWorkerEntryPointSlot);
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
        String strEntryPointIndexPrefix = "e" + StringHelper.formatHex2(jumpIndex);
        String strWorkerPurposeSlot = "s" + StringHelper.formatHex2(battleWorkerSlot);
        String strWorkerType = "t" + StringHelper.formatHex2(battleWorkerType);
        String strEntryPointPurposeSlot = "p" + StringHelper.formatHex2(battleWorkerEntryPointSlot);
        return strEntryPointIndexPrefix + strWorkerPurposeSlot + strWorkerType + strEntryPointPurposeSlot;
    }

    private static String eventWorkerEntryPointToStringWithFallback(int eventWorkerType, int eventWorkerEntryPoint) {
        String label = eventWorkerEntryPointToString(eventWorkerType, eventWorkerEntryPoint);
        if (label != null) {
            return label;
        }
        return "e" + StringHelper.formatHex2(eventWorkerEntryPoint);
    }

    public static String eventWorkerEntryPointToString(int eventWorkerType, int eventWorkerEntryPoint) {
        if (eventWorkerEntryPoint == 0) {
            return "init";
        }
        if (eventWorkerEntryPoint == 1) {
            return "main";
        }
        return switch (eventWorkerType) {
            case 1 -> // FieldObject
                    switch (eventWorkerEntryPoint) {
                        case 2 -> "talk";
                        case 3 -> "scout";
                        case 4 -> "?cross"; // cross on FieldObject maybe does something?
                        case 5 -> "touch";
                        default -> null;
                    };
            case 2 -> // PlayerEdge
                    switch (eventWorkerEntryPoint) {
                        case 2 -> "?talk"; // talk on PlayerEdge probably does nothing?
                        case 3 -> "?scout"; // scout on PlayerEdge probably does nothing?
                        case 4 -> "cross";
                        case 5 -> "touch";
                        case 6 -> "?enter"; // enter on PlayerEdge probably does work
                        case 7 -> "?leave"; // leave on PlayerEdge probably does nothing?
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
