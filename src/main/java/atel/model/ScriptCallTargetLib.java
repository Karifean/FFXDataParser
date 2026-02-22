package atel.model;

import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptCallTargetLib {
    public static ScriptCallTargetLib FFX = new ScriptCallTargetLib();
    private ScriptCallTarget[] CALL_TARGETS;

    private boolean initialized = false;

    private ScriptCallTargetLib() {}

    private static ScriptField p(int index) {
        return p("p" + index, "unknown");
    }
    
    private static ScriptField p(String typeAndName) {
        return new ScriptField(typeAndName);
    }

    private static ScriptField p(String name, String type) {
        return new ScriptField(name, type);
    }

    private void putUnknownCt(int idx, String internalName, int inputs) {
        putUnknownCt(idx, internalName, "unknown", inputs);
    }

    private void putUnknownCt(int idx, String internalName, String returnType, int inputs) {
        ScriptField[] inputList = new ScriptField[inputs];
        for (int i = 0; i < inputs; i++) {
            inputList[i] = p(i+1);
        }
        putUnknownCt(idx, internalName, returnType, inputList);
    }

    private void putUnknownCt(int idx, String internalName, String returnType, ScriptField... inputList) {
        ScriptCallTarget func = new ScriptCallTarget(null, returnType, internalName, inputList);
        putCtWithIdx(idx, func);
    }

    private void putUnknownCt(int idx, int inputs) {
        putUnknownCt(idx, null, inputs);
    }

    private void putCtWithIdx(int idx, ScriptCallTarget ct) {
        ct.idx = idx;
        ct.funcspace = idx / 0x1000;
        CALL_TARGETS[idx] = ct;
    }

    private void putVoidWithIdx(int idx, ScriptCallTarget ct) {
        putCtWithIdx(idx, ct);
        ct.canCallAsVoid = true;
    }

    public ScriptCallTarget get(int idx, List<StackObject> params) {
        return CALL_TARGETS[idx];
    }

    public void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        CALL_TARGETS = new ScriptCallTarget[0x10000];
        putVoidWithIdx(0x0000, new ScriptCallTarget("wait", "unknown", null, p("frames", "int")));
        putVoidWithIdx(0x0001, new ScriptCallTarget("loadModel", "unknown", null, p("model")));
        putVoidWithIdx(0x0002, new ScriptCallTarget("attachToCamera", "unknown", null, p("ID", "int"), p("int"), p("unused", "int")));
        putVoidWithIdx(0x0003, new ScriptCallTarget("attachToSomething?", "unknown", null, p("int"))); // arg always zero?
        putVoidWithIdx(0x0004, new ScriptCallTarget("attachToMapGroup", "unknown", null, p("groupIndex", "int")));
        putVoidWithIdx(0x0005, new ScriptCallTarget("unloadActor", "unknown", null, true));
        putUnknownCt(0x0006, 3);
        putUnknownCt(0x0007, 1);
        putCtWithIdx(0x0010, new ScriptCallTarget("getEntranceIndex", "int", null, true));
        putVoidWithIdx(0x0011, new ScriptCallTarget("transitionToRoom?", "unknown", null, p("room"), p("spawnpoint", "int")));
        putUnknownCt(0x0012, 2);
        putVoidWithIdx(0x0013, new ScriptCallTarget("setPosition", "unknown", null, p("x", "float"), p("y", "float"), p("z", "float")));
        putVoidWithIdx(0x0015, new ScriptCallTarget("setDestination", "unknown", null, p("x", "float"), p("y", "float"), p("z", "float")));
        putVoidWithIdx(0x0016, new ScriptCallTarget("setMovementSpeed", "unknown", null, p("speed", "float")));
        putUnknownCt(0x0017, 1); // noclip: set motion threshold/radius, used for collision and checking if a destination is reached
        putVoidWithIdx(0x0018, new ScriptCallTarget("startMotion", "unknown", null, p("activeBits", "int"), p("flags", "bitfield"), p("targetWorker", "worker")));
        putVoidWithIdx(0x0019, new ScriptCallTarget("startRotation", "unknown", null, p("activeBits", "int"), p("flags", "bitfield"), p("targetWorker", "worker"))); // see noclip for flags
        putVoidWithIdx(0x001A, new ScriptCallTarget("waitForMotion", "unknown", null));
        putVoidWithIdx(0x001B, new ScriptCallTarget("waitForRotation", "unknown", null));
        putVoidWithIdx(0x001C, new ScriptCallTarget("setMotionTiming", "unknown", null, p("currTime?", "float"), p("duration?", "int")));
        putVoidWithIdx(0x001D, new ScriptCallTarget("setRotationTiming", "unknown", null, p("currTime?", "float"), p("duration?", "int")));
        putCtWithIdx(0x001F, new ScriptCallTarget("destinationToYaw", "float", null));
        putCtWithIdx(0x0020, new ScriptCallTarget("destinationToPitch", "float", null));
        putUnknownCt(0x0021, 1);
        putCtWithIdx(0x0023, new ScriptCallTarget("setVelocityYaw", "unknown", null, p("angle", "float"))); // Never used by the game
        putCtWithIdx(0x0024, new ScriptCallTarget("setVelocityPitch", "unknown", null, p("angle", "float"))); // Never used by the game
        putUnknownCt(0x0025, 1); // noclip: 25-7 set a contextually-dependent vector, sometimes euler angles, 26/27 are never used by the game
        putCtWithIdx(0x0028, new ScriptCallTarget("setRotationTarget1", "unknown", null, p("angle", "float")));
        putCtWithIdx(0x0029, new ScriptCallTarget("setRotationTarget2", "unknown", null, p("angle", "float")));
        putCtWithIdx(0x002A, new ScriptCallTarget("setRotationTarget3", "unknown", null, p("angle", "float")));
        putCtWithIdx(0x002B, new ScriptCallTarget("setYawTurnStep", "unknown", null, p("float")));
        putCtWithIdx(0x002C, new ScriptCallTarget("setPitchTurnStep", "unknown", null, p("float")));
        putUnknownCt(0x002D, 1);
        putCtWithIdx(0x002E, new ScriptCallTarget("setRotationSpeed1", "unknown", null, p("float")));
        putCtWithIdx(0x002F, new ScriptCallTarget("setRotationSpeed2", "unknown", null, p("float")));
        putCtWithIdx(0x0030, new ScriptCallTarget("setRotationSpeed3", "unknown", null, p("float")));
        putCtWithIdx(0x0033, new ScriptCallTarget("getWorkerIndex", "worker", null, p("workerOrSelf")));
        putCtWithIdx(0x0034, new ScriptCallTarget("enableFieldInteraction", "unknown", null, p("type", "fieldInteraction")));
        putCtWithIdx(0x0035, new ScriptCallTarget("disableFieldInteraction", "unknown", null, p("type", "fieldInteraction")));
        putCtWithIdx(0x0036, new ScriptCallTarget("stopMotion", "unknown", null, p("worker")));
        putUnknownCt(0x0037, 1);
        putCtWithIdx(0x0038, new ScriptCallTarget("getWorkerX", "float", null, p("worker")));
        putCtWithIdx(0x0039, new ScriptCallTarget("getWorkerY", "float", null, p("worker")));
        putCtWithIdx(0x003A, new ScriptCallTarget("getWorkerZ", "float", null, p("worker")));
        putCtWithIdx(0x003D, new ScriptCallTarget("saveRotation", "unknown", null, true));
        putCtWithIdx(0x003F, new ScriptCallTarget("SavedRotation1", "unknown", null, false));
        putCtWithIdx(0x0042, new ScriptCallTarget("linkWorkerToChr", "unknown", null, p("btlChr")));
        putCtWithIdx(0x0043, new ScriptCallTarget("setWorkerAsPlayer", "unknown", null, true));
        putCtWithIdx(0x0044, new ScriptCallTarget("PressedButtonsBitfield?", "unknown", null, false)); // used in some trials, not sure the condition exactly
        putCtWithIdx(0x0046, new ScriptCallTarget("HeldButtonsBitfield", "bitfieldFrom_controllerButton", null, false));
        putUnknownCt(0x0047, 0);
        putCtWithIdx(0x004C, new ScriptCallTarget("controllerButtonPressed1?", "bool", null, p("controllerButton")));
        putCtWithIdx(0x004D, new ScriptCallTarget("controllerButtonPressed2?", "bool", null, p("controllerButton")));
        putUnknownCt(0x0050, 1);
        putCtWithIdx(0x0051, new ScriptCallTarget("controllerButtonPressed3?", "bool", null, p("controllerButton")));
        putCtWithIdx(0x0054, new ScriptCallTarget("setCollisionEdgePosition", "unknown", null, p("x1", "float"), p("y1", "float"), p("z1", "float"), p("x2", "float"), p("y2", "float"), p("z2", "float")));
        putCtWithIdx(0x0055, new ScriptCallTarget("setCollisionHeightRange", "unknown", null, p("range", "float")));
        putCtWithIdx(0x0056, new ScriptCallTarget("setCollisionShapeActive", "unknown", null, p("active", "bool")));
        putCtWithIdx(0x0057, new ScriptCallTarget("setCollisionZoneCenter", "unknown", null, p("x", "float"), p("y", "float"), p("z", "float")));
        putCtWithIdx(0x0058, new ScriptCallTarget("setCollisionZoneSize", "unknown", null, p("xRange", "float"), p("zRange", "float")));
        putCtWithIdx(0x0059, new ScriptCallTarget("setCollisionZoneSize", "unknown", null, p("xRange", "float"), p("yRange", "float"), p("zRange", "float")));
        putUnknownCt(0x005A, 1); // noclip: collision detection
        putCtWithIdx(0x005C, new ScriptCallTarget("setCollisionShapeActive", "unknown", null, p("active", "bool"))); // same as 006C
        putCtWithIdx(0x005D, new ScriptCallTarget("enablePlayerControl?", "unknown", null, true));
        putCtWithIdx(0x005E, new ScriptCallTarget("disablePlayerControl?", "unknown", null, true));
        putCtWithIdx(0x005F, new ScriptCallTarget("halt", "unknown", "halt", true));
        putUnknownCt(0x0060, 1);
        putCtWithIdx(0x0061, new ScriptCallTarget("setTalkRange", "unknown", null, p("range", "float")));
        putUnknownCt(0x0062, 1); // noclip: collision detection
        putCtWithIdx(0x0063, new ScriptCallTarget("setTalkAngleTolerance", "unknown", null, p("tolerance", "float"))); // noclip: collision detection
        putCtWithIdx(0x0064, new ScriptCallTarget("displayFieldString", "unknown", null, p("msgWindow", "int"), p("string", "localString")));
        putCtWithIdx(0x0065, new ScriptCallTarget("positionMessage", "unknown", null, p("msgWindow", "int"), p("x", "int"), p("y", "int"), p("align", "textAlignment")));
        putCtWithIdx(0x0066, new ScriptCallTarget("setMessageBoxTransparent", "unknown", null, p("msgWindow", "int"), p("transparent", "bool")));
        putCtWithIdx(0x0069, new ScriptCallTarget(p("msgWindow", "int"), p("int"), p("int")));
        putCtWithIdx(0x006A, new ScriptCallTarget("?showMessage", "unknown", null, p("msgWindow", "int"), p(2)));
        putCtWithIdx(0x006B, new ScriptCallTarget("?closeMessage", "unknown", null, p("msgWindow", "int")));
        putCtWithIdx(0x006C, new ScriptCallTarget("setMovementSpeed", "unknown", null, p("speed", "float")));
        putCtWithIdx(0x006D, new ScriptCallTarget("setYawTurnStepAllLevels", "unknown", null, p("float"))); // affects *all* motions on this worker, not just the current one
        putCtWithIdx(0x006E, new ScriptCallTarget("setPitchTurnStepAllLevels", "unknown", null, p("float")));
        putCtWithIdx(0x006F, new ScriptCallTarget("setAllRotationRate1", "unknown", null, p("rate", "float")));
        putCtWithIdx(0x0070, new ScriptCallTarget("setAllRotationRate2", "unknown", null, p("rate", "float")));
        putCtWithIdx(0x0071, new ScriptCallTarget("setAllRotationRate3", "unknown", null, p("rate", "float")));
        putCtWithIdx(0x0074, new ScriptCallTarget("setTurningDuration", "unknown", null, p("frames", "int")));
        putCtWithIdx(0x0076, new ScriptCallTarget("getWorkerType", "eventWorkerType", null));
        putCtWithIdx(0x0077, new ScriptCallTarget("stopWorkerMotion", "unknown", null, p("worker")));
        putCtWithIdx(0x0078, new ScriptCallTarget("stopWorkerRotation", "unknown", null, p("worker")));
        putCtWithIdx(0x007A, new ScriptCallTarget("setCollisionEdgeLength", "unknown", null, p("leftLength", "float"), p("rightLength", "float"))); // distance along line defined by positions
        putCtWithIdx(0x007C, new ScriptCallTarget("waitForText", "unknown", null, p("msgWindow", "int"))); // equivalent to 0x0084 with second param 0
        putCtWithIdx(0x007D, new ScriptCallTarget("?awaitPlayerDialogueChoice", "int", null, p("msgWindow", "int")));
        putUnknownCt(0x007F, 1);
        putCtWithIdx(0x0080, new ScriptCallTarget("getCurrentSpawnpointX", "float", null, true));
        putCtWithIdx(0x0081, new ScriptCallTarget("getCurrentSpawnpointY", "float", null, true));
        putCtWithIdx(0x0082, new ScriptCallTarget("getCurrentSpawnpointZ", "float", null, true));
        putCtWithIdx(0x0083, new ScriptCallTarget("getCurrentSpawnpointYaw", "float", null, true));
        putCtWithIdx(0x0084, new ScriptCallTarget("waitForText", "unknown", null, p("msgWindow", "int"), p(2)));
        putCtWithIdx(0x0085, new ScriptCallTarget("setTouchRadius", "unknown", null, p("radius", "float"))); // noclip: setCollisionRadius()
        putCtWithIdx(0x0086, new ScriptCallTarget("CurrentMap", "map", null, false)); // the ID of the actual map geometry file, multiple "map"s can share these
        putCtWithIdx(0x0087, new ScriptCallTarget("CurrentRoom", "room", null, false));
        putCtWithIdx(0x0088, new ScriptCallTarget("LastMap", "map", null, false));
        putCtWithIdx(0x0089, new ScriptCallTarget("LastRoom", "room", null, false));
        putUnknownCt(0x008B, 0); // noclip: checking line crossing
        putUnknownCt(0x008D, 1);
        putUnknownCt(0x008E, 1); // noclip: setCollisionHeight()
        putCtWithIdx(0x008F, new ScriptCallTarget("?canTextAdvance", "bool", null, p("msgWindow", "int")));
        putCtWithIdx(0x0090, new ScriptCallTarget("?getYaw", "float", null, true));
        putUnknownCt(0x0091, 0);
        putCtWithIdx(0x0092, new ScriptCallTarget("?getWorkerYaw", "float", null, p("worker")));
        putUnknownCt(0x0093, 1);
        putCtWithIdx(0x0094, new ScriptCallTarget("setGravity", "unknown", null, p("g", "int")));
        putCtWithIdx(0x0095, new ScriptCallTarget("setRotationYaw", "unknown", null, p("float")));
        putCtWithIdx(0x0096, new ScriptCallTarget("setRotationPitch", "unknown", null, p("float")));
        putCtWithIdx(0x0097, new ScriptCallTarget("getMessageWindowState", "messageWindowState", null, p("msgWindow", "int")));
        putCtWithIdx(0x0098, new ScriptCallTarget("setMotionSpline", "unknown", null, p("duration", "int"), p("spline", "pointer")));
        putUnknownCt(0x009A, 1);
        putUnknownCt(0x009B, 2);
        putCtWithIdx(0x009D, new ScriptCallTarget("setTextFlags", "unknown", null, p("msgWindow", "int"), p("textFlags", "textFlagBitfield")));
        putCtWithIdx(0x009E, new ScriptCallTarget("setStringVariable", "unknown", null, p("msgWindow", "int"), p("varIndex", "int"), p("stringVarType"), p("value", "unknown")));
        putCtWithIdx(0x009F, new ScriptCallTarget("setStringVariableValue", "unknown", null, p("msgWindow", "int"), p("varIndex", "int"), p("value", "unknown"))); // Unused by the game
        putCtWithIdx(0x00A0, new ScriptCallTarget("setStringVariableType", "unknown", null, p("msgWindow", "int"), p("varIndex", "int"), p("stringVarType"))); // Unused by the game
        putUnknownCt(0x00A2, 5);
        putCtWithIdx(0x00A3, new ScriptCallTarget("getWorkerPosition", "unknown", null, p("worker"), p("xDest", "pointer"), p("yDest", "pointer"), p("zDest", "pointer")));
        putCtWithIdx(0x00A4, new ScriptCallTarget("workerDist", "unknown", null, p("workerA", "worker"), p("workerB", "worker")));
        putCtWithIdx(0x00A5, new ScriptCallTarget("horizontalWorkerDist", "unknown", null, p("workerA", "worker"), p("workerB", "worker")));
        putCtWithIdx(0x00A6, new ScriptCallTarget("GetRandomInRange", "int", null, p("maxExclusive", "int")));
        putCtWithIdx(0x00A7, new ScriptCallTarget("deactivate", "unknown", null, p("worker")));
        putCtWithIdx(0x00A8, new ScriptCallTarget("activate", "unknown", null, p("worker")));
        putCtWithIdx(0x00A9, new ScriptCallTarget("GetRandomValue", "int", null, true));
        putCtWithIdx(0x00AA, new ScriptCallTarget("SetRandomEncountersActive", "unknown", null, p("active", "bool")));
        putUnknownCt(0x00AB, 2);
        putUnknownCt(0x00AC, 1);
        putCtWithIdx(0x00B1, new ScriptCallTarget("setInitialYVel", "unknown", null, p("float")));
        putUnknownCt(0x00B2, 1);
        putUnknownCt(0x00B3, 1);
        putUnknownCt(0x00B4, 1);
        putUnknownCt(0x00BA, 0);
        putUnknownCt(0x00BB, 1);
        putUnknownCt(0x00BC, 1);
        putUnknownCt(0x00BD, 1);
        putUnknownCt(0x00BE, 1);
        putCtWithIdx(0x00BF, new ScriptCallTarget("getSpawnpointX", "float", null, p("spawnpoint", "int")));
        putCtWithIdx(0x00C0, new ScriptCallTarget("getSpawnpointY", "float", null, p("spawnpoint", "int")));
        putCtWithIdx(0x00C1, new ScriptCallTarget("getSpawnpointZ", "float", null, p("spawnpoint", "int")));
        putCtWithIdx(0x00C2, new ScriptCallTarget("getSpawnpointYaw", "float", null, p("spawnpoint", "int")));
        putUnknownCt(0x00C4, 0);
        putCtWithIdx(0x00C5, new ScriptCallTarget("registerTurnMotions?", "unknown", null, p("p1", "motion"), p("p2", "motion"), p("p3", "motion"), p("p4", "motion")));
        putUnknownCt(0x00C6, 0);
        putUnknownCt(0x00C7, null, "motion", 1);
        putCtWithIdx(0x00C8, new ScriptCallTarget("computeRotationDuration", "unknown", null, p("flags", "int"), p("shortDuration", "int"), p("longDuration", "int")));
        putCtWithIdx(0x00C9, new ScriptCallTarget("getWorkerFunctionForSpawnpoint", "int", null, p("spawnpoint", "int"))); // a signal is sent for this function of an event-defined worker when the map loads
        putCtWithIdx(0x00CA, new ScriptCallTarget("addPartyMember", "unknown", null, p("playerChar")));
        putCtWithIdx(0x00CB, new ScriptCallTarget("removePartyMember", "unknown", null, p("playerChar"))); // Party member is greyed out in the list
        putUnknownCt(0x00CC, 1);
        putUnknownCt(0x00CE, 1);
        putCtWithIdx(0x00CF, new ScriptCallTarget("saveMotion", "unknown", null, p("motion")));
        putCtWithIdx(0x00D0, new ScriptCallTarget("SavedMotion", "unknown", null, false));
        putCtWithIdx(0x00D1, new ScriptCallTarget("stopWorkerMotion", "unknown", null, true));
        putCtWithIdx(0x00D2, new ScriptCallTarget("stopWorkerRotation", "unknown", null, true));
        putCtWithIdx(0x00D5, new ScriptCallTarget("playFieldVoiceLine", "unknown", null, p("voiceFile")));
        putUnknownCt(0x00D6, 0);
        putCtWithIdx(0x00D7, new ScriptCallTarget("?abortFieldVoiceLine", "unknown", null, true));
        putUnknownCt(0x00D8, 0);
        putUnknownCt(0x00D9, 0);
        putUnknownCt(0x00DA, 0);
        putCtWithIdx(0x00DB, new ScriptCallTarget("loadAndPlayMagic", "unknown", null, p("magicFile")));
        putCtWithIdx(0x00DC, new ScriptCallTarget("awaitLoadingOfMagic", "unknown", null, true));
        putUnknownCt(0x00DE, 0);
        putCtWithIdx(0x00DF, new ScriptCallTarget("playSfx", "unknown", null, p("sfx")));
        putCtWithIdx(0x00E0, new ScriptCallTarget(p("sfx")));
        putUnknownCt(0x00E1, 0);
        putUnknownCt(0x00E2, 1);
        putUnknownCt(0x00E3, 1);
        putUnknownCt(0x00E4, 1);
        putUnknownCt(0x00E6, 1);
        putCtWithIdx(0x00E7, new ScriptCallTarget("putPartyMemberInSlot", "unknown", null, p("slot", "int"), p("playerChar")));
        putCtWithIdx(0x00E8, new ScriptCallTarget("storeFrontlineInArray", "unknown", null, p("slot1", "pointer"), p("slot2", "pointer"), p("slot3", "pointer")));
        putCtWithIdx(0x00E9, new ScriptCallTarget("setMenuEnabled", "unknown", null, p("enabled", "bool")));
        putUnknownCt(0x00EB, 0);
        putCtWithIdx(0x00EC, new ScriptCallTarget("findPartyMemberFormationIndex", "int", null, p("playerChar")));
        putCtWithIdx(0x00ED, new ScriptCallTarget("getWorkerLinkedChr", "btlChr", null, true)); // get ID of attached actor
        putUnknownCt(0x00EE, 1);
        putUnknownCt(0x00EF, 1);
        putUnknownCt(0x00F2, 1);
        putUnknownCt(0x00F3, 1);
        putCtWithIdx(0x00F4, new ScriptCallTarget("pointToYaw?", "float", null, p("x", "float"), p("y", "float"), p("z", "float"))); // y is unused
        putCtWithIdx(0x00F5, new ScriptCallTarget("pointToPitch?", "float", null, p("x", "float"), p("y", "float"), p("z", "float")));
        putCtWithIdx(0x00F6, new ScriptCallTarget("waitForWorker", "float", null, p("priority", "int"), p("worker"))); // waits until no signal is active or queued at level for the worker
        putUnknownCt(0x00F8, 3);
        putUnknownCt(0x00F9, 0);
        putUnknownCt(0x00FA, 1);
        putCtWithIdx(0x00FB, new ScriptCallTarget("getCurrentHoveredDialogueChoice", "int", null, p("msgWindow", "int")));
        putCtWithIdx(0x00FD, new ScriptCallTarget("playSfxWithParams?", "unknown", null, p("sfx"), p(2), p(3)));
        putUnknownCt(0x00FE, 2);
        putUnknownCt(0x0100, 3);
        putCtWithIdx(0x0102, new ScriptCallTarget("setBgmToLoad?", "unknown", null, p("bgm")));
        putCtWithIdx(0x0103, new ScriptCallTarget("unloadBgm?", "unknown", null, p("bgm")));
        putCtWithIdx(0x0104, new ScriptCallTarget("playBgm?", "unknown", null, p("bgm")));
        putCtWithIdx(0x0105, new ScriptCallTarget("loadBgm?", "unknown", null, true));
        putUnknownCt(0x0106, 3);
        putUnknownCt(0x0107, 2);
        putUnknownCt(0x0108, 5);
        putUnknownCt(0x0109, 1);
        putUnknownCt(0x010A, 1);
        putCtWithIdx(0x010B, new ScriptCallTarget("warpToRoom?", "unknown", null, p("room"), p("spawnpoint", "int")));
        putUnknownCt(0x010C, 2);
        putCtWithIdx(0x010D, new ScriptCallTarget("SetPrimerCollected", "unknown", null, p("primerIndex", "int")));
        putCtWithIdx(0x010E, new ScriptCallTarget("CollectedPrimersBitfield", "bitfield", null, false));
        putUnknownCt(0x010F, 4);
        putUnknownCt(0x0110, 1);
        putCtWithIdx(0x0111, new ScriptCallTarget("StorePartyMemberSetup", "unknown", null, true));
        putCtWithIdx(0x0112, new ScriptCallTarget("RestorePartyMemberSetup", "unknown", null, true));
        putUnknownCt(0x0114, 2);
        putUnknownCt(0x0115, 1);
        putUnknownCt(0x0116, 1);
        putUnknownCt(0x0117, 1);
        putUnknownCt(0x0119, 1);
        putCtWithIdx(0x011A, new ScriptCallTarget(p("p1", "pointer"), p(2), p("p3", "pointer")));
        putCtWithIdx(0x011B, new ScriptCallTarget("setSplashSprite", "unknown", null, p("splashIndex", "int"), p("pointer")));
        putCtWithIdx(0x011C, new ScriptCallTarget("hideSplash", "unknown", null, p("splashIndex", "int")));
        putCtWithIdx(0x011D, new ScriptCallTarget("?setSplashAlpha", "unknown", null, p("splashIndex", "int"), p("alpha", "int")));
        // 011E sets a brightness value of splash
        putCtWithIdx(0x011F, new ScriptCallTarget("setSplashPosition", "unknown", null, p("splashIndex", "int"), p("x?", "int"), p("y?", "int")));
        putCtWithIdx(0x0120, new ScriptCallTarget("scaleSplash", "unknown", null, p("splashIndex", "int"), p("scaleX?", "float"), p("scaleY?", "float")));
        putCtWithIdx(0x0121, new ScriptCallTarget("movementStickTilt?", "int", null, p("axis", "int")));
        putUnknownCt(0x0122, 1);
        putCtWithIdx(0x0126, new ScriptCallTarget("warpToPoint", "float", null, p("x", "float"), p("y", "float"), p("z", "float")));
        putUnknownCt(0x0127, 1);
        putCtWithIdx(0x0128, new ScriptCallTarget("workerWithinRadius", "bool", null, p("worker"), p("radius", "float")));
        putCtWithIdx(0x0129, new ScriptCallTarget("setSplashColor", "unknown", null, p("splashIndex", "int"), p("r", "int"), p("b", "int"), p("g", "int")));
        putUnknownCt(0x012A, 2);
        putUnknownCt(0x012B, 1);
        putCtWithIdx(0x012D, new ScriptCallTarget("GetAffectionValue", "int", null, p("char")));
        putCtWithIdx(0x012E, new ScriptCallTarget("IncreaseAffectionValue", "unknown", null, p("char"), p("amount", "int")));
        putCtWithIdx(0x012F, new ScriptCallTarget("DecreaseAffectionValue", "unknown", null, p("char"), p("amount", "int"))); // Never used, will set to 0 if it would go negative
        putCtWithIdx(0x0130, new ScriptCallTarget("SetAffectionValue", "unknown", null, p("char"), p("amount", "int")));
        putCtWithIdx(0x0132, new ScriptCallTarget("setActorCollisionActive", "unknown", null, p("worker"), p("active", "bool")));
        putCtWithIdx(0x0133, new ScriptCallTarget("setActorCollisionActive2", "unknown", null, p("worker"), p("active", "bool"))); // same as 0132
        putCtWithIdx(0x0134, new ScriptCallTarget("loadModel2", "unknown", null, p("model"))); // same as 0001
        putCtWithIdx(0x0135, new ScriptCallTarget("PlayerTotalGil", "int", null, false));
        putCtWithIdx(0x0136, new ScriptCallTarget("obtainGil", "unknown", null, p("amount", "int")));
        putCtWithIdx(0x0137, new ScriptCallTarget("tryPayGil", "bool", null, p("amount", "int")));
        putUnknownCt(0x0138, 1);
        putUnknownCt(0x0139, 8);
        putCtWithIdx(0x0139, new ScriptCallTarget("requestNumericInput?", "int", null, p(1), p(2), p(3), p(4), p(5), p("x?", "int"), p("y?", "int"), p("align", "textAlignment")));
        putCtWithIdx(0x013B, new ScriptCallTarget("displayFieldChoice", "int", null, p("msgWindow", "int"), p("string", "localString"), p(3), p(4), p("x", "int"), p("y", "int"), p("align", "textAlignment")));
        putUnknownCt(0x013D, 1);
        putUnknownCt(0x013E, 1);
        putCtWithIdx(0x013F, new ScriptCallTarget("resolveStringMacro", "macroString", null, p("section", "stringVarType"), p("entryIndex", "unknown")));
        putUnknownCt(0x0140, 6);
        putCtWithIdx(0x0141, new ScriptCallTarget("?setMessageToConstructedString", "unknown", null, p("msgWindow", "int"), p("constructedString", "unknown"), p("pointer?", "pointer")));
        putUnknownCt(0x0142, 2);
        putUnknownCt(0x0143, 1);
        putUnknownCt(0x0144, 1);
        putUnknownCt(0x0145, 2);
        putUnknownCt(0x0146, 1);
        putUnknownCt(0x0148, 2);
        putUnknownCt(0x0149, 1);
        putCtWithIdx(0x014A, new ScriptCallTarget("SetBattleFlags", "unknown", null, p("p1", "battleFlags1Bitfield"), p(2), p(3)));
        putCtWithIdx(0x014D, new ScriptCallTarget("HighestAffectionChar", "char", null, false));
        putCtWithIdx(0x014E, new ScriptCallTarget("analogStickAngle", "float", null, p("deadzone", "float"), p("x", "float"), p("y", "float")));
        putUnknownCt(0x014F, 3);
        putUnknownCt(0x0151, 1);
        putUnknownCt(0x0154, 0);
        putCtWithIdx(0x0155, new ScriptCallTarget("SetFootSoundFlag", "unknown", null, p("flag", "footSoundFlag")));
        putUnknownCt(0x0156, 0);
        putCtWithIdx(0x0157, new ScriptCallTarget("UnsetFootSoundFlag", "unknown", null, p("flag", "footSoundFlag")));
        putUnknownCt(0x0158, 1);
        putUnknownCt(0x0159, 2);
        putCtWithIdx(0x015B, new ScriptCallTarget("obtainTreasure", "unknown", null, p("msgWindow", "int"), p("treasure")));
        putUnknownCt(0x015D, 1);
        putUnknownCt(0x015E, 1);
        putCtWithIdx(0x015F, new ScriptCallTarget("?addKeyItem", "unknown", null, p("keyItem")));
        putCtWithIdx(0x0160, new ScriptCallTarget("hasKeyItem", "bool", null, p("keyItem")));
        putCtWithIdx(0x0161, new ScriptCallTarget("?removeKeyItem", p("keyItem")));
        putUnknownCt(0x0166, 1);
        putUnknownCt(0x0167, 0);
        putCtWithIdx(0x016A, new ScriptCallTarget(p("bgm"), p(2)));
        putCtWithIdx(0x016B, new ScriptCallTarget(p("bgm"), p(2), p(3)));
        putUnknownCt(0x016C, 0);
        putCtWithIdx(0x016D, new ScriptCallTarget(p("bgm")));
        putUnknownCt(0x016E, 2);
        putCtWithIdx(0x016F, new ScriptCallTarget("updateSavedRoomAndSpawnpoint", "unknown", null, p("spawnpoint", "int")));
        putCtWithIdx(0x0170, new ScriptCallTarget("updateSavedRoomAndSpawnpoint", "unknown", null, p("room"), p("spawnpoint", "int"))); // Never used
        putCtWithIdx(0x0171, new ScriptCallTarget("fillPartyMemberHp", "unknown", null, p("playerChar")));
        putCtWithIdx(0x0172, new ScriptCallTarget("fillPartyMemberMp", "unknown", null, p("playerChar")));
        putCtWithIdx(0x0174, new ScriptCallTarget("VelocityPitch", "unknown", null, false));
        putUnknownCt(0x0177, 1);
        putUnknownCt(0x0179, 1);
        putUnknownCt(0x017A, 1);
        putUnknownCt(0x017B, 1);
        putUnknownCt(0x017C, 1);
        putCtWithIdx(0x017E, new ScriptCallTarget("playBgmWithVol?", "unknown", null, p("bgm"), p(2)));
        putUnknownCt(0x017F, 1);
        putCtWithIdx(0x0180, new ScriptCallTarget("changeCurrentBgmVol?", "unknown", null, p("oldVol?", "int"), p("newVol?", "int")));
        putUnknownCt(0x0181, 0);
        putUnknownCt(0x0184, 1);
        putUnknownCt(0x0185, 2);
        putCtWithIdx(0x0188, new ScriptCallTarget("fadeoutBgm?", "unknown", null, p("frames", "int")));
        putUnknownCt(0x0189, 1);
        putUnknownCt(0x018A, 1);
        putUnknownCt(0x018B, 1);
        putUnknownCt(0x018F, 2);
        putUnknownCt(0x0192, 1);
        putCtWithIdx(0x0193, new ScriptCallTarget(p("sfx")));
        putUnknownCt(0x0194, 1);
        putUnknownCt(0x0195, 0); // some sort of wait related to move animation/magic
        putCtWithIdx(0x0196, new ScriptCallTarget("bindGfxToSelf", "unknown", null, p("gfxIndex", "int"))); // only works for level geometry
        putCtWithIdx(0x0197, new ScriptCallTarget("attachToMapPart", "unknown", null, p("partIndex", "int")));
        putCtWithIdx(0x0198, new ScriptCallTarget("enableVisualEffectIndex", "unknown", null, p("mapPartIndex", "int"), p("effectType"), p("runOnce", "bool")));
        putCtWithIdx(0x0199, new ScriptCallTarget("enableOwnVisualEffectIndex", "unknown", null, p("effectType"), p("runOnce", "bool")));
        putCtWithIdx(0x019C, new ScriptCallTarget("True", "bool", null, false));
        putCtWithIdx(0x019D, new ScriptCallTarget("detach", "unknown", null, p("unloadModel", "bool")));
        putUnknownCt(0x019E, 0);
        putUnknownCt(0x019F, 1);
        putUnknownCt(0x01A0, 3);
        putCtWithIdx(0x01A4, new ScriptCallTarget("setVisualEffectParameter", "unknown", null, p("partIndex", "int"), p("value", "float")));
        putCtWithIdx(0x01A5, new ScriptCallTarget("setAnimatedTextureFrame", "unknown", null, p("textureIndex", "int"), p("frame", "int"))); // used for blitzball scoreboard
        putUnknownCt(0x01A6, 0);
        putCtWithIdx(0x01A7, new ScriptCallTarget("obtainTreasureSilently", "unknown", null, p("treasure")));
        putUnknownCt(0x01A8, 1);
        putCtWithIdx(0x01AB, new ScriptCallTarget("TotalBattlesFought", "int", null, false));
        putCtWithIdx(0x01AC, new ScriptCallTarget("getMonsterArenaCaptures", "int", null, p("monsterArenaIndex", "int")));
        putCtWithIdx(0x01AD, new ScriptCallTarget("incrementMonsterArenaCaptures", "unknown", null, p("monsterArenaIndex", "int")));
        putCtWithIdx(0x01AE, new ScriptCallTarget("decrementMonsterArenaCaptures", "unknown", null, p("monsterArenaIndex", "int"))); // never used in game
        putCtWithIdx(0x01AF, new ScriptCallTarget("changeMonsterArenaCaptures", "unknown", null, p("monsterArenaIndex", "int"), p("add", "int")));
        putUnknownCt(0x01B0, 2);
        putUnknownCt(0x01B1, 0);
        putCtWithIdx(0x01B2, new ScriptCallTarget("getItemCount", "int", null, p("item", "command")));
        putUnknownCt(0x01B5, 0);
        putCtWithIdx(0x01B6, new ScriptCallTarget("?isBrotherhoodUnpowered", "bool", null, true));
        putCtWithIdx(0x01B7, new ScriptCallTarget("applyBrotherhoodPowerup", "unknown", null, p("treasure")));
        putCtWithIdx(0x01B8, new ScriptCallTarget("enableBrotherhood", "unknown", null, true));
        putCtWithIdx(0x01BA, new ScriptCallTarget(p("worker"), p(2)));
        putUnknownCt(0x01BB, 0);
        putUnknownCt(0x01BC, 1);
        putUnknownCt(0x01BD, 1);
        putUnknownCt(0x01BF, 0);
        putUnknownCt(0x01C0, 2);
        putUnknownCt(0x01C2, 0);
        putUnknownCt(0x01C3, 1);
        putUnknownCt(0x01C4, 1);
        putUnknownCt(0x01C5, 2);
        putUnknownCt(0x01C6, 0);
        putCtWithIdx(0x01C8, new ScriptCallTarget("disableOwnVisualEffect", "unknown", null, p("effectType", "int"))); // Never used by the game
        putCtWithIdx(0x01C9, new ScriptCallTarget("disableVisualEffect", "unknown", null, p("mapPartIndex", "int"), p("effectType")));
        putUnknownCt(0x01CA, 0);
        putCtWithIdx(0x01CC, new ScriptCallTarget("CurrentPlayingMusic", "bgm", null, false));
        putUnknownCt(0x01CE, 0);
        putUnknownCt(0x01CF, 0);
        putUnknownCt(0x01D0, 0);
        putUnknownCt(0x01D1, 1);
        putCtWithIdx(0x01D2, new ScriptCallTarget("enteredAirshipPasswordEquals", "bool", null, p("string", "localString")));
        putUnknownCt(0x01D4, 2);
        putCtWithIdx(0x01D9, new ScriptCallTarget("?ActivateRikkuName", "unknown", null, true));
        putUnknownCt(0x01DA, 4);
        putUnknownCt(0x01DC, 0);
        putUnknownCt(0x01DE, 0);
        putUnknownCt(0x01E0, 1);
        putUnknownCt(0x01E2, 0);
        putUnknownCt(0x01E3, 0);
        putUnknownCt(0x01E4, 4);
        putUnknownCt(0x01E5, 3);
        putUnknownCt(0x01E6, 3);
        putUnknownCt(0x01E7, 2);
        putUnknownCt(0x01E9, 2);
        putUnknownCt(0x01EB, 1);
        putUnknownCt(0x01EE, 0);
        putUnknownCt(0x01F0, 1);
        putCtWithIdx(0x01F2, new ScriptCallTarget("getActiveAeonsBitfield", "aeonsBitfield", null, true));
        putCtWithIdx(0x01F3, new ScriptCallTarget("?constructAeonChoiceString", "unknown", null, p("choosableAeons", "aeonsBitfield"), p("totalAeons", "aeonsBitfield"), p("pointer")));
        putCtWithIdx(0x01F4, new ScriptCallTarget("?getAeonFromChoice", "playerChar", null, p("choice", "int"), p("aeonsBitfield")));
        putUnknownCt(0x01F5, 1);
        putCtWithIdx(0x01F6, new ScriptCallTarget("pressControllerButton", "unknown", null, p("controllerButton")));
        putUnknownCt(0x01F8, 2);
        putCtWithIdx(0x01F9, new ScriptCallTarget("removePartyMemberCompletely", "unknown", null, p("playerChar"))); // Party member is hidden in the list
        putUnknownCt(0x01FA, 0);
        putUnknownCt(0x01FB, 0);
        putCtWithIdx(0x01FC, new ScriptCallTarget("teachAbilityToPartyMemberSilently", "unknown", null, p("playerChar"), p("charCommand")));
        putUnknownCt(0x0200, 1);
        putUnknownCt(0x0201, 0);
        putUnknownCt(0x0202, 1);
        putCtWithIdx(0x0203, new ScriptCallTarget(p("model")));
        putUnknownCt(0x0204, 1);
        putUnknownCt(0x0205, 0);
        putUnknownCt(0x0206, 0);
        putUnknownCt(0x0207, 0);
        putUnknownCt(0x0209, 0);
        putUnknownCt(0x020A, 0);
        putUnknownCt(0x020B, 0);
        putUnknownCt(0x020D, 1);
        putUnknownCt(0x020F, 1);
        putCtWithIdx(0x0210, new ScriptCallTarget("setMonsterArenaUnlocked", "unknown", null, p("monsterArenaUnlock")));
        putUnknownCt(0x0212, 2);
        putUnknownCt(0x0213, 1);
        putCtWithIdx(0x0215, new ScriptCallTarget("grantCelestialUpgrade", "unknown", null, p("playerChar"), p("level", "int")));
        putCtWithIdx(0x0216, new ScriptCallTarget("teachAbilityToPartyMemberWithMsg", "unknown", null, p("msgWindow?", "int"), p("playerChar"), p("charCommand")));
        putUnknownCt(0x0217, 0);
        putCtWithIdx(0x0219, new ScriptCallTarget("changeMonsterArenaCaptures2", "unknown", null, p("monsterArenaIndex", "int"), p("add", "int")));
        putUnknownCt(0x021A, 2);
        putUnknownCt(0x021B, 2);
        putCtWithIdx(0x021D, new ScriptCallTarget("equipBrotherhoodToTidus", "unknown", null, true));
        putUnknownCt(0x021E, 0);
        putUnknownCt(0x0220, 1);
        putUnknownCt(0x0221, 1);
        putCtWithIdx(0x0225, new ScriptCallTarget("setShopPrices", "unknown", null, p("percentage", "int")));
        putUnknownCt(0x0226, 1);
        putUnknownCt(0x0229, 0);
        putUnknownCt(0x022B, 0);
        putUnknownCt(0x022D, 1);
        putUnknownCt(0x022E, 4);
        putUnknownCt(0x022F, 2);
        putUnknownCt(0x0231, 1);
        putCtWithIdx(0x0232, new ScriptCallTarget("pressControllerButton2", "unknown", null, p("controllerButton")));
        putUnknownCt(0x0233, 1);
        putUnknownCt(0x0234, 0);
        putUnknownCt(0x0235, 0);
        putCtWithIdx(0x0236, new ScriptCallTarget("?getStringAsChoice", "unknown", null, p("string", "localString")));
        putCtWithIdx(0x0237, new ScriptCallTarget("?constructChoiceStringFromChoiceArray", "unknown", null, p("choiceCount", "int"), p("?availableChoiceBitfield", "bitfield"), p("pointerToChoiceArray", "pointer"), p("pointer2", "pointer")));
        putUnknownCt(0x0239, 1);
        putUnknownCt(0x023A, 1);
        putCtWithIdx(0x023B, new ScriptCallTarget("?checkMonsterArenaUnlock", "bool", null, p("monsterArenaUnlock")));
        putCtWithIdx(0x023C, new ScriptCallTarget("?checkMultipleMonsterArenaUnlocks", "bool", null, p("from", "monsterArenaUnlock"), p("next", "int")));
        putUnknownCt(0x023D, 0);
        putUnknownCt(0x023E, 1);
        putUnknownCt(0x023F, 1);
        putCtWithIdx(0x0240, new ScriptCallTarget("setPartyMemberEquipmentHidden", "unknown", null, p("playerChar"), p("hide", "bool")));
        putUnknownCt(0x0241, 1);
        putUnknownCt(0x0242, 0);
        putUnknownCt(0x0243, 0);
        putUnknownCt(0x0244, 0);
        putUnknownCt(0x0245, 0);
        putUnknownCt(0x024A, 1);
        putUnknownCt(0x024B, 2);
        putUnknownCt(0x024C, 0);
        putUnknownCt(0x024D, 1);
        putUnknownCt(0x0251, 1);
        putUnknownCt(0x0253, 0);
        putCtWithIdx(0x0254, new ScriptCallTarget("setSphereGrid", "unknown", null, p("sphereGrid")));
        putUnknownCt(0x0255, 0);
        putUnknownCt(0x0256, 1);
        putUnknownCt(0x0257, 1);
        putUnknownCt(0x0259, 2);
        putCtWithIdx(0x025B, new ScriptCallTarget("unlockAchievement", "unknown", null, p("achievement")));
        putUnknownCt(0x025C, 0);
        putUnknownCt(0x025D, 1);
        putUnknownCt(0x025E, 1);
        putUnknownCt(0x025F, 2);
        putUnknownCt(0x0260, 1);
        putUnknownCt(0x0261, 1);
        putUnknownCt(0x0262, 0);
        putUnknownCt(0x0263, 0);
        putUnknownCt(0x0264, 1);
        putCtWithIdx(0x0265, new ScriptCallTarget("BlitzballDebugMode", "bool", null, false));
        putUnknownCt(0x0266, 0);
        putUnknownCt(0x0267, 7);
        putCtWithIdx(0x1000, new ScriptCallTarget(p(1), p(2), p(3), p("pointer")));
        putCtWithIdx(0x1001, new ScriptCallTarget("sin", "float", null, p("float")));
        putCtWithIdx(0x1002, new ScriptCallTarget("cos", "float", null, p("float")));
        putCtWithIdx(0x1005, new ScriptCallTarget("atan2", "float", null, p("y?", "float"), p("x?","float")));
        putCtWithIdx(0x1006, new ScriptCallTarget("sqrt", "float", null, p("float")));
        putCtWithIdx(0x100A, new ScriptCallTarget("toInteger", "int", null, p("float")));
        putCtWithIdx(0x1013, new ScriptCallTarget("normalizeAngle", "float", null, p("angle", "float")));
        putCtWithIdx(0x1015, new ScriptCallTarget("checkIsFacingTarget?", "bool", null, p("yaw", "float"), p("tolerance?", "float"), p("x", "float"), p("z", "float"), p("targetX", "float"), p("targetZ", "float")));
        putCtWithIdx(0x1019, new ScriptCallTarget("abs", "float", null, p("float")));
        putCtWithIdx(0x101A, new ScriptCallTarget("distance", "float", null, p("x1", "float"), p("y1", "float"), p("x2", "float"), p("y2", "float")));
        putCtWithIdx(0x101B, new ScriptCallTarget(p("x1", "float"), p("y1", "float"), p("z1", "float"), p("x2", "float"), p("y2", "float"), p("z2", "float")));
        putCtWithIdx(0x101C, new ScriptCallTarget(p(1), p("pointer")));
        putUnknownCt(0x4001, 1);
        putCtWithIdx(0x4003, new ScriptCallTarget("fadeinFromColor", "unknown", null, p("frames", "int"), p("red", "int"), p("green", "int"), p("blue", "int")));
        putCtWithIdx(0x4004, new ScriptCallTarget("fadeinFromBlack?", "unknown", null, p("frames", "int")));
        putCtWithIdx(0x4005, new ScriptCallTarget("fadeoutToBlack?", "unknown", null, p("frames", "int")));
        putCtWithIdx(0x4006, new ScriptCallTarget("fadeinFromWhite", "unknown", null, p("frames", "int")));
        putCtWithIdx(0x4007, new ScriptCallTarget("fadeoutToWhite", "unknown", null, p("frames", "int")));
        putCtWithIdx(0x4008, new ScriptCallTarget("setScreenOverlayColor", "unknown", null, p("red", "int"), p("green", "int"), p("blue", "int"), p("alpha", "int")));
        putCtWithIdx(0x4009, new ScriptCallTarget("dimScreen", "unknown", null, p("red", "int"), p("green", "int"), p("blue", "int"), p("factor", "int"))); // blends (screen - color)*factor, super weird if color isn't close to black
        putCtWithIdx(0x400A, new ScriptCallTarget("motionBlurEffect", "unknown", null, p("alpha", "int"))); // alpha for overlaying previous frame
        putCtWithIdx(0x400B, new ScriptCallTarget("disableFade", "unknown", null)); // only for the black and white fades
        putCtWithIdx(0x400C, new ScriptCallTarget("disableScreenOverlayColor", "unknown", null));
        putCtWithIdx(0x400D, new ScriptCallTarget("waitForFade", "unknown", null)); // only for the black and white fades
        putUnknownCt(0x400E, 1);
        putUnknownCt(0x400F, 1);
        putCtWithIdx(0x4013, new ScriptCallTarget("setActorLight", "unknown", null, p("lightIndex", "int"), p("r", "int"), p("b", "int"), p("g", "int"), p("polar", "float"), p("azimuth", "float")));
        putUnknownCt(0x4014, 1);
        putUnknownCt(0x4015, 2);
        putCtWithIdx(0x4016, new ScriptCallTarget("setMainCharMoveSpeed", "unknown", null, p("moveType"), p("speed", "float")));
        putCtWithIdx(0x4017, new ScriptCallTarget("setInvisibleWallState", "unknown", null, p("polygonGroup", "int"), p("state", "invisWallState")));
        putUnknownCt(0x4019, 1);
        putCtWithIdx(0x401A, new ScriptCallTarget("cameraCrossFade", "unknown", null, p("frames", "int"), p("alpha", "int")));
        putUnknownCt(0x401B, 1);
        putUnknownCt(0x401C, 1);
        putCtWithIdx(0x401D, new ScriptCallTarget("showModularMenu", "unknown", null, p("menu")));
        putUnknownCt(0x401F, 0);
        putUnknownCt(0x4020, 0);
        putUnknownCt(0x4022, 1);
        putUnknownCt(0x4023, 1);
        putUnknownCt(0x4024, 0);
        putUnknownCt(0x4025, 9);
        putUnknownCt(0x402A, 2);
        putUnknownCt(0x4034, 1);
        putCtWithIdx(0x4036, new ScriptCallTarget("setWindParams", "unknown", null, p("strength?", "float"), p("pitch", "float"), p("yaw", "float")));
        putUnknownCt(0x4039, 4);
        putUnknownCt(0x403A, 1);
        putUnknownCt(0x403B, 2);
        putUnknownCt(0x403C, 1);
        putUnknownCt(0x403E, 2);
        putUnknownCt(0x403F, 0);
        putUnknownCt(0x4040, 1);
        putUnknownCt(0x4043, 2);
        putUnknownCt(0x4044, 3);
        putUnknownCt(0x4045, 1);
        putUnknownCt(0x4046, 1);
        putCtWithIdx(0x5000, new ScriptCallTarget("playCharMotion", "unknown", null, p("motion")));
        putCtWithIdx(0x5001, new ScriptCallTarget("loadMapMotionBundle", "unknown", null, p("int"))); // loads a file with animations that only appear on this map (i.e. for cutscenes)
        putCtWithIdx(0x5002, new ScriptCallTarget("playCharMotion2", "unknown", null, p("motion")));
        putCtWithIdx(0x5003, new ScriptCallTarget("?awaitMotion", "unknown", null, true));
        putCtWithIdx(0x5004, new ScriptCallTarget("?shouldAwaitMotion", "bool", null, true));
        putCtWithIdx(0x5005, new ScriptCallTarget("resetMotion", "unknown", null, true));
        putCtWithIdx(0x5006, new ScriptCallTarget("scaleActor", "unknown", null, p("scaleXYZ", "float")));
        putCtWithIdx(0x5007, new ScriptCallTarget("scaleActor", "unknown", null, p("scaledDimensions","dimensionsBitfield"), p("scaleX", "float"), p("scaleY", "float"), p("scaleZ", "float")));
        putCtWithIdx(0x5008, new ScriptCallTarget("?setHidden", "unknown", null, p("hidden", "bool")));
        putCtWithIdx(0x5009, new ScriptCallTarget("setActorWeight", "float", null, p("weight", "float")));
        putUnknownCt(0x500A, 1); // cur_actor->__0x4f8 = p1
        putCtWithIdx(0x500B, new ScriptCallTarget("setActorGroundHeight", "unknown", null, p("height", "float")));
        putUnknownCt(0x500C, 1); // `chEnGravity`; noted invalid by dbgPrintf; En might stand for Enable?; supposed to set the gravity mode instead
        putCtWithIdx(0x500D, new ScriptCallTarget("?setHidden2", "unknown", null, p("bool")));
        putCtWithIdx(0x500E, new ScriptCallTarget("setIgnoreNavmesh", "unknown", null, p("ignore", "bool")));
        putCtWithIdx(0x500F, new ScriptCallTarget("?setCollisionDisabled", "unknown", null, p("disabled", "bool")));
        putCtWithIdx(0x5010, new ScriptCallTarget("loadModelMotionGroup", "unknown", null, p("model"), p("motionType")));
        putCtWithIdx(0x5011, new ScriptCallTarget("disposeMotionGroup", "unknown", null, p(1)));
        putCtWithIdx(0x5013, new ScriptCallTarget("setFieldModeAndMotionType", "unknown", null, p("motionType")));
        putCtWithIdx(0x5014, new ScriptCallTarget("setShade", "unknown", null, p("target?", "float"), p("latch?", "unknown")));
        putCtWithIdx(0x5015, new ScriptCallTarget("setShade", "unknown", null, p("targetR", "float"), p("targetG", "float"), p("targetB", "float"), p("latch?", "unknown")));
        putCtWithIdx(0x5016, new ScriptCallTarget("setOffset", "unknown", null, p("x", "float"), p("y", "float"), p("z", "float")));
        putCtWithIdx(0x5017, new ScriptCallTarget("setGravityMode", "unknown", "chSetGravMode", p("gravityMode")));
        putCtWithIdx(0x5018, new ScriptCallTarget("setMotionSpeed", "unknown", null, p("motionSpeed", "float")));
        putCtWithIdx(0x5019, new ScriptCallTarget(p("motion"), p(2), p(3), p(4), p(5)));
        putCtWithIdx(0x501A, new ScriptCallTarget("playCharMotionPortion", p("motion"), p(2), p("loops", "int"), p("startFrame", "int"), p("endFrame", "int")));
        putCtWithIdx(0x501B, new ScriptCallTarget("playCharMotion3", "unknown", null, p("motion")));
        putCtWithIdx(0x501D, new ScriptCallTarget("playCharMotion4", "unknown", null, p("motion")));
        putCtWithIdx(0x501E, new ScriptCallTarget("?awaitMotion2", "unknown", null, true)); // also sets something in relation to g_debugVoiceSync
        putCtWithIdx(0x501F, new ScriptCallTarget("?shouldAwaitMotion2", "bool", null, true));
        putCtWithIdx(0x5020, new ScriptCallTarget("resetMotion2", "unknown", null, true));
        putCtWithIdx(0x5021, new ScriptCallTarget("setMotionSpeed2", "float", null, p("motionSpeed", "float")));
        putUnknownCt(0x5022, 1); // cur_actor->__0x737 = p1
        putUnknownCt(0x5023, 1); // Doesn't do anything
        putUnknownCt(0x5024, 1); // Doesn't do anything
        putCtWithIdx(0x5025, new ScriptCallTarget("setMotionMoves", "unknown", null, p(1), p("motion")));
        putUnknownCt(0x5026, 5);
        putUnknownCt(0x5027, 8);
        putCtWithIdx(0x5028, new ScriptCallTarget("?setMoveSetChangeRun", "unknown", null, p("float")));
        putCtWithIdx(0x5029, new ScriptCallTarget("setShadowMode", "unknown", null, p(1)));
        putCtWithIdx(0x502A, new ScriptCallTarget("?setClip", "unknown", null, p("bool")));
        putUnknownCt(0x502B, 0);
        putUnknownCt(0x502C, 2);
        putCtWithIdx(0x502D, new ScriptCallTarget("setAttachedToWorker", "unknown", null, p("worker"), p("attachmentPoint", "int")));
        putCtWithIdx(0x5030, new ScriptCallTarget("?getSound", "unknown", null, true)); // Never used by the game so not sure how it would be used with other functions
        putCtWithIdx(0x5031, new ScriptCallTarget("?getActorSound", "unknown", null, p("worker"))); // Never used by the game so not sure how it would be used with other functions
        putCtWithIdx(0x5032, new ScriptCallTarget("playDefaultCharMotion", "unknown", null, true));
        putCtWithIdx(0x5033, new ScriptCallTarget("?setTransparent", "unknown", null, p("?target"), p("?latch")));
        putCtWithIdx(0x5034, new ScriptCallTarget("?setMotionHokan", "unknown", null, p(1)));
        putCtWithIdx(0x5035, new ScriptCallTarget("getSequenceFrame", "int", null, true));
        putUnknownCt(0x5036, 5);
        putUnknownCt(0x5037, 4);
        putUnknownCt(0x5038, 4);
        putCtWithIdx(0x5039, new ScriptCallTarget("setThickness", "float", null, p("thickness", "float")));
        putCtWithIdx(0x503A, new ScriptCallTarget("setClipZ", "float", null, p("clipZ", "float")));
        putCtWithIdx(0x503B, new ScriptCallTarget("disposeActorMotionGroup", "unknown", null, p("btlChr"), p(2)));
        putUnknownCt(0x503C, 1);
        putUnknownCt(0x503D, 1);
        putUnknownCt(0x503E, 2);
        putCtWithIdx(0x503F, new ScriptCallTarget("playCharMotion4", "unknown", null, p("motion")));
        putCtWithIdx(0x5040, new ScriptCallTarget("playCharMotion5", "unknown", null, p("motion")));
        putCtWithIdx(0x5042, new ScriptCallTarget(p("motion"), p(2), p(3), p(4), p(5)));
        putCtWithIdx(0x5043, new ScriptCallTarget(p("worker"), p("motion"), p(3), p(4), p(5), p(6)));
        putCtWithIdx(0x5044, new ScriptCallTarget(p("worker"), p("motion"), p(3), p(4), p(5), p(6)));
        putCtWithIdx(0x5045, new ScriptCallTarget(p("worker"), p("motion"), p(3), p(4), p(5), p(6)));
        putCtWithIdx(0x5046, new ScriptCallTarget(p("worker"), p("motion"), p(3), p(4), p(5), p(6)));
        putCtWithIdx(0x5047, new ScriptCallTarget("playActorMotion", "unknown", null, p("worker"), p("motion")));
        putCtWithIdx(0x5048, new ScriptCallTarget("playActorMotion2", "unknown", null, p("worker"), p("motion")));
        putCtWithIdx(0x5049, new ScriptCallTarget("?awaitActorMotion", "unknown", null, p("worker")));
        putCtWithIdx(0x504A, new ScriptCallTarget("?shouldAwaitActorMotion", "bool", null, p("worker")));
        putCtWithIdx(0x504B, new ScriptCallTarget("resetActorMotion", "unknown", null, p("worker")));
        putCtWithIdx(0x504C, new ScriptCallTarget("setActorMotionSpeed", "unknown", null, p("worker"), p("motionSpeed", "float")));
        putUnknownCt(0x504D, 1);
        putCtWithIdx(0x504E, new ScriptCallTarget("?loadModelAsync", "unknown", null, p("model")));
        putCtWithIdx(0x504F, new ScriptCallTarget("?loadModelSync", "unknown", null, p("model")));
        putUnknownCt(0x5050, null, "unknown", p("worker"), p(2)); // same as 5022 but on target actor
        putCtWithIdx(0x5051, new ScriptCallTarget("getMotionFrame", "int", null, true));
        putUnknownCt(0x5052, 5);
        putUnknownCt(0x5053, 2);
        putCtWithIdx(0x5054, new ScriptCallTarget("modelSetHide", "unknown", null, p(1), p(2)));
        putCtWithIdx(0x5055, new ScriptCallTarget("modelGetHide", "unknown", null, p(1))); // never used by the game
        putCtWithIdx(0x5056, new ScriptCallTarget("setCalcNorm", "unknown", null, p("bool")));
        putCtWithIdx(0x5057, new ScriptCallTarget("setCalcNorm", "unknown", null, p("worker"), p("bool")));
        putUnknownCt(0x5058, 1);
        putUnknownCt(0x5059, 2);
        putCtWithIdx(0x505A, new ScriptCallTarget("textureSetImageBase", "unknown", null, p(1), p(2)));
        putCtWithIdx(0x505B, new ScriptCallTarget("textureSetClutBase", "unknown", null, p(1), p(2)));
        putCtWithIdx(0x505C, new ScriptCallTarget("?setMotionNextHokan", "unknown", null, p(1))); // animation transition duration
        putUnknownCt(0x505D, null, "motion", 0); // returns cur_actor->__0x72c
        putCtWithIdx(0x505E, new ScriptCallTarget("getChrSpeed", "float", null, true));
        putCtWithIdx(0x505F, new ScriptCallTarget("motionSetLoop", "unknown", null, p(1)));
        putCtWithIdx(0x5060, new ScriptCallTarget("?idEnableShade", "unknown", null, p("bool")));
        putCtWithIdx(0x5061, new ScriptCallTarget("?loadMotionGroups", "unknown", null, p("model"), p(2)));
        putCtWithIdx(0x5062, new ScriptCallTarget(p("model"), p(2), p("bool")));
        putCtWithIdx(0x5063, new ScriptCallTarget(p("model"), p(2)));
        putCtWithIdx(0x5064, new ScriptCallTarget(p(1), p("bool")));
        putCtWithIdx(0x5065, new ScriptCallTarget("?startReadMotionGroup", "unknown", null, p(1)));
        putCtWithIdx(0x5066, new ScriptCallTarget("?syncReadMotionGroup", "unknown", null, p(1)));
        putCtWithIdx(0x5067, new ScriptCallTarget("?setDrawCull", "unknown", null, p("bool")));
        putUnknownCt(0x5068, 0); // doesn't exist
        putCtWithIdx(0x5069, new ScriptCallTarget("setKeepFps", "unknown", null, p("bool")));
        putCtWithIdx(0x506A, new ScriptCallTarget("setPLight", "unknown", null, p("enabled", "bool")));
        putCtWithIdx(0x506B, new ScriptCallTarget("getPLight", "bool", null, true));
        putCtWithIdx(0x506C, new ScriptCallTarget("setPLightMask", "unknown", null, p("enabled", "bool"), p("mask", "unknown"))); // never used by the game
        putCtWithIdx(0x506D, new ScriptCallTarget("?setPitch", "unknown", null, p(1), p(2)));
        putCtWithIdx(0x506E, new ScriptCallTarget("bindSetScale", "unknown", null, p("scale", "float")));
        putCtWithIdx(0x506F, new ScriptCallTarget("setHideCalc", "unknown", null, p(1)));
        putUnknownCt(0x5070, 0);
        putCtWithIdx(0x5071, new ScriptCallTarget("?textureLoadImage", "unknown", null, p(1)));
        putUnknownCt(0x5072, 0); // returns cur_actor->__0x824
        putCtWithIdx(0x5073, new ScriptCallTarget("setOverlapHit", "unknown", null, p("enabled", "bool")));
        putCtWithIdx(0x5074, new ScriptCallTarget("getOverlapHit", "bool", null));
        putCtWithIdx(0x5075, new ScriptCallTarget("getActorMotionFrame", "int", null, p("worker")));
        putUnknownCt(0x5076, 1); // cur_actor->__0x4d4 = p1
        putUnknownCt(0x5077, 0); // returns cur_actor->__0x4d4
        putCtWithIdx(0x5078, new ScriptCallTarget("?textureSetAnim", "unknown", null, p(1), p(2)));
        putCtWithIdx(0x5079, new ScriptCallTarget("setNeck", "unknown", null, p("enabled", "bool")));
        putCtWithIdx(0x507A, new ScriptCallTarget("setNeckFaceActor", "unknown", null, p("worker")));
        putCtWithIdx(0x507B, new ScriptCallTarget("setNeckFacePoint", "unknown", null, p("x", "float"), p("y", "float"), p("z", "float")));
        putUnknownCt(0x507C, 0); // resets facing? looks forward? //TODO: test
        putCtWithIdx(0x507D, new ScriptCallTarget("setNeckRotation", "unknown", null, p(1), p(2)));
        putUnknownCt(0x507E, 2); // tgt_actor->__0x4d4 = p1
        putCtWithIdx(0x507F, new ScriptCallTarget("setModelAlpha", "unknown", null, p(1), p(2)));
        putUnknownCt(0x5080, 1); // cur_actor->__0x51c = p1
        putUnknownCt(0x5081, 1); // cur_actor->__0x198 bit 3 = p1
        putCtWithIdx(0x5082, new ScriptCallTarget("setCloth", "unknown", null, p("enabled", "bool")));
        putCtWithIdx(0x5083, new ScriptCallTarget("vulSetClipMode", "unknown", null, p(1)));
        putCtWithIdx(0x5084, new ScriptCallTarget("setNeckSpeed", "unknown", null, p("speed", "float")));
        putCtWithIdx(0x5085, new ScriptCallTarget("setShadowHeight", "unknown", null, p("height", "float")));
        putUnknownCt(0x5086, 0);
        putUnknownCt(0x5087, 0); // doesn't exist
        putCtWithIdx(0x5088, new ScriptCallTarget("setShadeId", "unknown", null, p(1), p("float")));
        putCtWithIdx(0x5089, new ScriptCallTarget(p("motion"), p(2), p(3), p(4), p(5)));
        putCtWithIdx(0x508A, new ScriptCallTarget(p("worker"), p("motion"), p(2), p(3), p(4), p(5)));
        putCtWithIdx(0x508B, new ScriptCallTarget(p("motion"), p(2), p(3), p(4), p(5)));
        putCtWithIdx(0x508C, new ScriptCallTarget(p("worker"), p("motion"), p(2), p(3), p(4), p(5)));
        putCtWithIdx(0x508D, new ScriptCallTarget("?textureSetAnimTimer", "unknown", null, p(1)));
        putCtWithIdx(0x508E, new ScriptCallTarget("setClothGravity", "unknown", null, p("enabled", "bool")));
        putUnknownCt(0x508F, 1); // cur_actor->__0x870 = p1
        putUnknownCt(0x5090, 1); // cur_actor->__0x871 = p1
        putUnknownCt(0x6000, "camSleep", 1);
        putUnknownCt(0x6001, "camWakeUp", 1);
        putUnknownCt(0x6002, "camSetPos", 3);
        putCtWithIdx(0x6003, new ScriptCallTarget(null, "unknown", "camGetPos", p("xDest", "pointer"), p("yDest", "pointer"), p("zDest", "pointer")));
        putUnknownCt(0x6004, "camSetPolar", 3);
        putUnknownCt(0x6005, "camSetPolarOffset", 3);
        putUnknownCt(0x6006, "camSetHypot", 6);
        putUnknownCt(0x6007, "camSetHypot2", 6);
        putUnknownCt(0x6008, "camSetHypot3", 6);
        putUnknownCt(0x6009, "camSetAct", 3);
        putUnknownCt(0x600A, "camSetFilter", 5);
        putUnknownCt(0x600B, "camSetFilter2", 5);
        putUnknownCt(0x600C, "camSetFilterY", 3);
        putUnknownCt(0x600D, "camSetFilterY2", 3);
        putUnknownCt(0x600E, "camSleepFilter", 1);
        putUnknownCt(0x600F, "camResetFilter", 0);
        putUnknownCt(0x6010, "camMove", 1);
        putUnknownCt(0x6011, "camMovePolar", 1);
        putUnknownCt(0x6012, "camMoveCos", 1);
        putUnknownCt(0x6013, "camMovePolarCos", 1);
        putUnknownCt(0x6014, "camMoveAcc", 4);
        putUnknownCt(0x6015, "camMovePolarAcc", 4);
        putUnknownCt(0x6016, "camResetMove", 0);
        putUnknownCt(0x6017, "camSetInertia", 4);
        putUnknownCt(0x6018, "camSetDirVector", 3);
        putUnknownCt(0x6019, "camResetDirVector", 0);
        putUnknownCt(0x601A, "camWait", 0);
        putUnknownCt(0x601B, "camCheck", 0);
        putUnknownCt(0x601C, "camSetDataPoint", 2);
        putUnknownCt(0x601D, "camSetDataPointHypot", 4);
        putUnknownCt(0x601E, "camSetDataPoint2", 2);
        putUnknownCt(0x601F, "camSetDataPointHypot2", 4);
        putUnknownCt(0x6020, "refSetPos", 3);
        putCtWithIdx(0x6021, new ScriptCallTarget(null, "unknown", "refGetPos", p("xDest", "pointer"), p("yDest", "pointer"), p("zDest", "pointer")));
        putUnknownCt(0x6022, "refSetPolar", 3);
        putUnknownCt(0x6023, "refSetPolarOffset", 3);
        putUnknownCt(0x6024, "refSetHypot", 6);
        putUnknownCt(0x6025, "refSetHypot2", 6);
        putUnknownCt(0x6026, "refSetHypot3", 6);
        putUnknownCt(0x6027, "refSetAct", 3);
        putUnknownCt(0x6028, "refSetFilter", 5);
        putUnknownCt(0x6029, "refSetFilter2", 5);
        putUnknownCt(0x602A, "refSetFilterY", 3);
        putUnknownCt(0x602B, "refSetFilterY2", 3);
        putUnknownCt(0x602C, "refSleepFilter", 1);
        putUnknownCt(0x602D, "refResetFilter", 0);
        putUnknownCt(0x602E, "refMove", 1);
        putUnknownCt(0x602F, "refMovePolar", 1);
        putUnknownCt(0x6030, "refMoveCos", 1);
        putUnknownCt(0x6031, "refMovePolarCos", 1);
        putUnknownCt(0x6032, "refMoveAcc", 4);
        putUnknownCt(0x6033, "refMovePolarAcc", 4);
        putUnknownCt(0x6034, "refResetMove", 0);
        putUnknownCt(0x6035, "refSetInertia", 4);
        putUnknownCt(0x6036, "refSetDirVector", 4);
        putUnknownCt(0x6037, "refResetDirVector", 0);
        putUnknownCt(0x6038, "refWait", 0);
        putUnknownCt(0x6039, "refCheck", 0);
        putUnknownCt(0x603A, "camSetRoll", 1);
        putUnknownCt(0x603B, "camSetScrDpt", 1);
        putUnknownCt(0x603C, "camSetAct2", 4);
        putUnknownCt(0x603D, "refSetAct2", 4);
        putUnknownCt(0x603E, "camSetBtl", 3);
        putUnknownCt(0x603F, "refSetBtl", 3);
        putUnknownCt(0x6040, "camSetBtlPolar", 6);
        putUnknownCt(0x6041, "refSetBtlPolar", 6);
        putUnknownCt(0x6042, "refMoveStat", 1);
        putUnknownCt(0x6043, "camMoveStat", 1);
        putUnknownCt(0x6044, "camSetBtlPolar2", 6);
        putUnknownCt(0x6045, "refSetBtlPolar2", 6);
        putUnknownCt(0x6046, "camSetSpline", 4);
        putUnknownCt(0x6047, "refSetSpline", 4);
        putUnknownCt(0x6048, "camStartSpline", 0);
        putUnknownCt(0x6049, "camRegSpline", 0);
        putUnknownCt(0x604A, "refStartSpline", 0);
        putUnknownCt(0x604B, "refRegSpline", 0);
        putUnknownCt(0x604C, "camSetChrPolar", 6);
        putUnknownCt(0x604D, "camSetChrPolar2", 6);
        putUnknownCt(0x604E, "camScrSet", 6);
        putUnknownCt(0x604F, "camScrOff", 1);
        putUnknownCt(0x6050, "camDrawSet", 5);
        putUnknownCt(0x6051, "camDrawLink", 2);
        putUnknownCt(0x6052, "camScrLink", 2);
        putUnknownCt(0x6053, "camScrMove", 2);
        putUnknownCt(0x6054, "camScrMoveCos", 2);
        putUnknownCt(0x6055, "camScrMoveAcc", 4);
        putUnknownCt(0x6056, "camDrawMove", 2);
        putUnknownCt(0x6057, "camDrawMoveCos", 2);
        putUnknownCt(0x6058, "camDrawMoveAcc", 4);
        putUnknownCt(0x6059, "refSetSplineFilter", 2);
        putUnknownCt(0x605A, "refSetSplineFilter2", 3);
        putUnknownCt(0x605B, "camSetSpline2", 2);
        putUnknownCt(0x605C, "refSetShake", 5);
        putUnknownCt(0x605D, "camSetShake", 5);
        putUnknownCt(0x605E, "camSetScreenShake", 6);
        putUnknownCt(0x605F, "refResetShake", 0);
        putUnknownCt(0x6060, "camResetShake", 0);
        putUnknownCt(0x6061, "camResetScreenShake", 1);
        putUnknownCt(0x6062, "refWaitShake", 0);
        putUnknownCt(0x6063, "camWaitShake", 0);
        putUnknownCt(0x6064, "camWaitScreenShake", 1);
        putUnknownCt(0x6065, "camPriority", 1);
        putUnknownCt(0x6066, "refSetShakeB", 5);
        putUnknownCt(0x6067, "camSetShakeB", 5);
        putUnknownCt(0x6068, "camSetScreenShakeB", 6);
        putUnknownCt(0x6069, "refSetShake2", 5);
        putUnknownCt(0x606A, "camSetShake2", 5);
        putUnknownCt(0x606B, "camSetScreenShake2", 6);
        putUnknownCt(0x606C, "refSetShake2B", 5);
        putUnknownCt(0x606D, "camSetShake2B", 5);
        putUnknownCt(0x606E, "camSetScreenShake2B", 6);
        putUnknownCt(0x606F, "refSetShake3", 5);
        putUnknownCt(0x6070, "camSetShake3", 5);
        putUnknownCt(0x6071, "camSetScreenShake3", 6);
        putUnknownCt(0x6072, "refSetShake3B", 5);
        putUnknownCt(0x6073, "camSetShake3B", 5);
        putUnknownCt(0x6074, "camSetScreenShake3B", 6);
        putUnknownCt(0x6075, "camScrSetCam", 2);
        putUnknownCt(0x6076, "camFreeBattle", 0);
        putUnknownCt(0x6077, "camGetRoll", "float", 0);
        putUnknownCt(0x6078, "camGetScrDpt", "float", 0);
        putUnknownCt(0x6079, "camScrResetMove", 1);
        putUnknownCt(0x607A, "camDrawResetMove", 1);
        putUnknownCt(0x607B, "camScrWait", 1);
        putUnknownCt(0x607C, "camDrawWait", 1);
        putUnknownCt(0x607D, "camBlur", 1);
        putUnknownCt(0x607E, "camFocus", 1);
        putUnknownCt(0x607F, "camSetFocus", 2);
        putUnknownCt(0x6080, "camRand", 1);
        putUnknownCt(0x6081, "refSetShake4", 5);
        putUnknownCt(0x6082, "camSetShake4", 5);
        putUnknownCt(0x6083, "refSetShake5", 5);
        putUnknownCt(0x6084, "camSetShake5", 5);
        putVoidWithIdx(0x6085, new ScriptCallTarget(null, "unknown", "camGetRealPos", p("xDest", "pointer"), p("yDest", "pointer"), p("zDest", "pointer")));
        putVoidWithIdx(0x6086, new ScriptCallTarget(null, "unknown", "refGetRealPos", p("xDest", "pointer"), p("yDest", "pointer"), p("zDest", "pointer")));
        putUnknownCt(0x6087, "refReset", 0);
        putUnknownCt(0x6088, "camReset", 0);
        putUnknownCt(0x7000, "btlTerminateAction", 0);
        putUnknownCt(0x7001, "btlSetRandPosFlag", 1);
        putVoidWithIdx(0x7002, new ScriptCallTarget("launchBattle", "unknown", "btlExe", p("battle"), p("transition", "battleTransition")));
        putUnknownCt(0x7003, "btlDirTarget", 2);
        putUnknownCt(0x7004, "btlSetDirRate", 1);
        putCtWithIdx(0x7005, new ScriptCallTarget("isWater?", "int", "btlGetWater", false));
        putUnknownCt(0x7006, "btlDirBasic", 2);
        putCtWithIdx(0x7007, new ScriptCallTarget("startMotion?", "unknown", "btlSetMotion", p("stdmotion")));
        putCtWithIdx(0x7008, new ScriptCallTarget("awaitMotion?", "unknown", "btlWaitMotion", true));
        putCtWithIdx(0x7009, new ScriptCallTargetAccessor("setSelfGravity", null, "btlSetGravity", "=", p("AffectedByGravity", "bool")));
        putCtWithIdx(0x700A, new ScriptCallTarget("setHeight?", "unknown", "btlSetHeight", p("int"), p("float")));
        putCtWithIdx(0x700B, new ScriptCallTarget("performCommand", "bool", "btlSetDirectCommand", p("target", "btlChr"), p("command")));
        putUnknownCt(0x700C, "btlMove", 8);
        putUnknownCt(0x700D, "btlDirPos", 2);
        putUnknownCt(0x700E, "btlSetDamage", 1);
        putCtWithIdx(0x700F, new ScriptCallTargetAccessor("readBtlChrProperty", "btlChr", "btlGetStat", null, "btlChrProperty"));
        putCtWithIdx(0x7010, new ScriptCallTarget("findMatchingChr", "btlChr", "btlSearchChr", p("group", "btlChr"), p("property", "btlChrProperty"), p("unused", "unknown"), p("selector")));
        putUnknownCt(0x7011, "btlCameraMode", 1);
        putUnknownCt(0x7012, "btlTerminateEffect", 0);
        putUnknownCt(0x7013, "btlChrSp", 1);
        putCtWithIdx(0x7014, new ScriptCallTarget("chosenCommand", "command", "btlGetComNum", false));
        putCtWithIdx(0x7015, new ScriptCallTarget("print?", "unknown", "btlPrint", p("int")));
        putCtWithIdx(0x7016, new ScriptCallTarget("stopMotion?", "unknown", "btlTerminateMotion", p("stdmotion")));
        putUnknownCt(0x7017, "btlSetNormalEffect", 2);
        putCtWithIdx(0x7018, new ScriptCallTargetAccessor("writeBtlChrProperty", "btlChr", "btlSetStat", "=", "btlChrProperty"));
        putCtWithIdx(0x7019, new ScriptCallTarget("usedCommand", "command", "btlGetReCom", false));
        putCtWithIdx(0x701A, new ScriptCallTargetAccessor("readCommandProperty", "command", "btlGetComInfo", null, "commandProperty"));
        putCtWithIdx(0x701B, new ScriptCallTarget("overrideAttemptedCommand", "unknown", "btlChangeReCom", p("target", "btlChr"), p("command")));
        putUnknownCt(0x701C, "btlSetMotionLevel", 1);
        putUnknownCt(0x701D, "btlGetMotionLevel", 0);
        putCtWithIdx(0x701E, new ScriptCallTarget("countChrOverlap", "int", "btlCountChr", p("group", "btlChr"), p("btlChr")));
        putUnknownCt(0x701F, "btlChgWaitMotion", 1);
        putUnknownCt(0x7020, "btlCheckStartEffect", 0);
        putCtWithIdx(0x7021, new ScriptCallTarget("dereferenceCharacter", "btlChr", "btlGetChrNum", p("btlChr")));
        putCtWithIdx(0x7022, new ScriptCallTarget("SetAmbushState", "unknown", "btlSetFirstAttack", p("ambushState")));
        putUnknownCt(0x7023, "btlDistTarget", 1);
        putCtWithIdx(0x7024, new ScriptCallTarget("CurrentBattle", "battle", "btlGetBtlScene", false));
        putCtWithIdx(0x7025, new ScriptCallTarget("findMatchingChrIncludingUntargetable?", "btlChr", "btlSearchChr2", p("group", "btlChr"), p("property", "btlChrProperty"), p("unknown"), p("selector")));
        putCtWithIdx(0x7026, new ScriptCallTarget(null, "unknown", "btlSetWeak", p("weakState")));
        putUnknownCt(0x7027, "btlGetWeak", "weakState", 0);
        putCtWithIdx(0x7028, new ScriptCallTarget("scaleOwnSize", "unknown", "btlSetScale", p("x?", "float"), p("y?", "float"), p("z?", "float")));
        putCtWithIdx(0x7029, new ScriptCallTargetAccessor("setSelfFloating", null, "btlSetFly", "=", p("floating", "bool")));
        putUnknownCt(0x702A, "btlCheckBtlPos", 0);
        putUnknownCt(0x702B, "btlCheckMotion", 0);
        putUnknownCt(0x702C, "btlSetHoming", 9);
        putUnknownCt(0x702D, "btlResetMove", 0);
        putUnknownCt(0x702E, "btlMoveTargetDist", "float", 1);
        putUnknownCt(0x702F, "btlOut", 1);
        putUnknownCt(0x7030, "btlGetMoveFlag", 0);
        putUnknownCt(0x7031, "btlStartMotion", 0);
        putCtWithIdx(0x7032, new ScriptCallTargetAccessor("setActorFacingAngle", "btlChr", "btlSetBtlPosDir", "=", p("facingAngle", "float")));
        putUnknownCt(0x7033, "btlSetEnMapID", 1);
        putCtWithIdx(0x7034, new ScriptCallTarget("endBattle", "unknown", "btlComplete", p("battleEndType")));
        putCtWithIdx(0x7035, new ScriptCallTarget("BattleEndType", "battleEndType", "btlGetCompInfo", false));
        putUnknownCt(0x7036, "btlSetTrans", 3);
        putCtWithIdx(0x7037, new ScriptCallTarget("addCommand", "unknown", "btlAddCom", p("btlChr"), p("command")));
        putCtWithIdx(0x7038, new ScriptCallTarget("removeCommand", "unknown", "btlDelCom", p("btlChr"), p("command")));
        putUnknownCt(0x7039, "btlTerminateDeath", 0);
        putUnknownCt(0x703A, "btlSetSpeed", 1);
        putCtWithIdx(0x703B, new ScriptCallTarget("setCommandDisabled", "unknown", "btlSetCommandUse", p("btlChr"), p("command"), p("disabled", "bool")));
        putCtWithIdx(0x703C, new ScriptCallTarget("runBtlSceneA", "unknown", "btlOff", p("btlScene", "int")));
        putUnknownCt(0x703D, "btlOn", 0);
        putUnknownCt(0x703E, "btlWait", 0);
        putUnknownCt(0x703F, "camReq", 2);
        putUnknownCt(0x7040, "btlMagicStart", 1);
        putUnknownCt(0x7041, "btlMagicEnd", 0);
        putCtWithIdx(0x7042, new ScriptCallTarget("displayBattleString", "unknown", "btlMes", p("msgWindow", "int"), p("string", "localString"), p("x?", "int"), p("y?", "int"), p("align", "textAlignment")));
        putCtWithIdx(0x7043, new ScriptCallTarget("closeTextOnConfirm", "unknown", "btlMesWait", p("msgWindow", "int")));
        putCtWithIdx(0x7044, new ScriptCallTarget("closeTextImmediately", "unknown", "btlMesClose", p("msgWindow", "int")));
        putUnknownCt(0x7045, "btlDistTargetFrame", 1);
        putUnknownCt(0x7046, "btlSplineStart", 1);
        putUnknownCt(0x7047, "btlSplineRegist", 2);
        putUnknownCt(0x7048, "btlSplineRegistPos", 4);
        putUnknownCt(0x7049, "btlSplineMove", 4);
        putCtWithIdx(0x704A, new ScriptCallTarget(null, "unknown", "btlCheckMove", p("btlChr")));
        putCtWithIdx(0x704B, new ScriptCallTarget(null, "unknown", "btlReqMotion", p("btlChr"), p("motionIndex", "int"), p("?await", "bool")));
        putCtWithIdx(0x704C, new ScriptCallTarget(null, "unknown", "btlWaitReqMotion", p("btlChr")));
        putUnknownCt(0x704D, "btlSetDeathLevel", 1);
        putUnknownCt(0x704E, "btlSetDeathPattern", 1);
        putUnknownCt(0x704F, "btlSetEventChrFlag", 2);
        putCtWithIdx(0x7050, new ScriptCallTarget("revive/reinitialize", "unknown", "btlResetParam", p("btlChr")));
        putUnknownCt(0x7051, "btlWaitNormalEffect", 0);
        putCtWithIdx(0x7052, new ScriptCallTarget("attachActor", "unknown", "btlChrLink", p( "btlChr"), p( "host","btlChr"), p( "attachmentPoint","int")));
        putUnknownCt(0x7053, "btlMoveJump", 9);
        putCtWithIdx(0x7054, new ScriptCallTarget(null, "unknown", "btlSetChrPosElem", p("btlChr"), p(2), p(3)));
        putUnknownCt(0x7055, "btlSetBodyHit", 1);
        putCtWithIdx(0x7056, new ScriptCallTarget(null, "unknown", "btlSetSpecialBattle", p("specialBattleSetting")));
        putUnknownCt(0x7057, "btlDirMove", 4);
        putCtWithIdx(0x7058, new ScriptCallTarget(null, "unknown", "btlCheckMotionNum", p("btlChr"), p("stdmotion")));
        putUnknownCt(0x7059, "btlMoveTargetDist2D", "float", 1);
        putCtWithIdx(0x705A, new ScriptCallTarget("forcePerformCommand", "bool", "btlSetAbsCommand", p("target", "btlChr"), p("command")));
        putUnknownCt(0x705B, "btlGetCamWidth", "float", 1);
        putUnknownCt(0x705C, "btlGetCamHeight", "float", 1);
        putUnknownCt(0x705D, "btlSetBindEffect", 2);
        putUnknownCt(0x705E, "btlResetBindEffect", 0);
        putUnknownCt(0x705F, "btlPrintF", 1);
        putUnknownCt(0x7060, "btlSetStatEff", 0);
        putUnknownCt(0x7061, "btlClearStatEff", 0);
        putUnknownCt(0x7062, "btlSetHitEffect", 2);
        putUnknownCt(0x7063, "btlWaitHitEffect", 0);
        putCtWithIdx(0x7064, new ScriptCallTarget("loadBattleVoiceLine", "unknown", "btlVoiceStandby", p("voiceFile")));
        putUnknownCt(0x7065, "btlVoiceStart", 0);
        putUnknownCt(0x7066, "btlVoiceStop", 0);
        putUnknownCt(0x7067, "btlGetVoiceStatus", 0);
        putUnknownCt(0x7068, "btlVoiceSync", 0);
        putUnknownCt(0x7069, "btlSearchChrCamera", "btlChr", 4);
        putUnknownCt(0x706A, "btlCheckTargetOwn", 1);
        putCtWithIdx(0x706B, new ScriptCallTarget(null, "unknown", "btlSetModelHide", p("btlChr"), p("part", "int"), p("show?", "bool")));
        putUnknownCt(0x706C, "btlSoundEffectNormal", 2);
        putCtWithIdx(0x706D, new ScriptCallTarget(null, "unknown", "btlSoundStreamNormal", p("btlChr"), p("btlCharVoice")));
        putCtWithIdx(0x706E, new ScriptCallTarget(null, "unknown", "btlReqVoice", p("btlChr"), p(2)));
        putUnknownCt(0x706F, "btlSetMotion2", 1);
        putUnknownCt(0x7070, "btlStatusOn", 0);
        putUnknownCt(0x7071, "btlStatusOff", 0);
        putCtWithIdx(0x7072, new ScriptCallTarget("displayBattleDialogString", "unknown", "btlmes2", p("msgWindow", "int"), p("string", "localString")));
        putUnknownCt(0x7073, "btlAttachWeapon", 1);
        putUnknownCt(0x7074, "btlDetachWeapon", 1);
        putCtWithIdx(0x7075, new ScriptCallTarget(null, "unknown", "btlReqWeaponMotion", p("btlChr"), p(2), p(3)));
        putUnknownCt(0x7076, "btlBallSplineMove", 3);
        putUnknownCt(0x7077, "btlDistTargetFrameBall", 2);
        putCtWithIdx(0x7078, new ScriptCallTargetAccessor("readCommandPropertyForActor", "command", "btlGetComInfo2", null, "commandProperty", p("btlChr")));
        putUnknownCt(0x7079, "btlResetWeapon", 0);
        putCtWithIdx(0x707A, new ScriptCallTarget(null, "unknown", "btlGetCalcResult", p("btlChr")));
        putCtWithIdx(0x707B, new ScriptCallTarget(null, "unknown", "btlSoundEffect", p("btlChr"), p("sfx", "btlSoundEffect")));
        putUnknownCt(0x707C, "btlWaitSound", 0);
        putCtWithIdx(0x707D, new ScriptCallTarget("setDebugFlag", "unknown", "btlSetDebug", p("battleDebugFlag"), p("active", "bool")));
        putCtWithIdx(0x707E, new ScriptCallTarget("?checkDebugFlagEnabled", "bool", "btlGetDebug", p("battleDebugFlag")));
        putCtWithIdx(0x707F, new ScriptCallTarget(null, "unknown", "btlSetBtlPos", p("btlChr")));
        putUnknownCt(0x7080, "btlChangeAuron", 1);
        putUnknownCt(0x7081, "btlWaitExe", 0);
        putUnknownCt(0x7082, "btlSetFreeEffect", 2);
        putUnknownCt(0x7083, "btlSetAfterImage", 2);
        putUnknownCt(0x7084, "btlResetAfterImage", 0);
        putUnknownCt(0x7085, "btlMoveAttack", 8);
        putUnknownCt(0x7086, "btlUseChrMpLimit", 0);
        putCtWithIdx(0x7087, new ScriptCallTarget(null, "unknown", "btlSoundEffectFade", p("btlChr"), p("sfx", "btlSoundEffect"), p(3)));
        putCtWithIdx(0x7088, new ScriptCallTarget(null, "unknown", "btlRegSoundEffect", p("btlChr"), p(2)));
        putUnknownCt(0x7089, "btlRegSoundEffectFade", 3);
        putCtWithIdx(0x708A, new ScriptCallTarget(null, "unknown", "btlInitEncount", p("battle")));
        putCtWithIdx(0x708B, new ScriptCallTarget(null, "unknown", "btlGetEncount", p("battle")));
        putCtWithIdx(0x708C, new ScriptCallTarget("setEncounterEnabled", "unknown", "btlSetEncount", p("battle"), p("active", "bool")));
        putUnknownCt(0x708D, "btlGetLastActionChr", "btlChr", 0);
        putUnknownCt(0x708E, "btlCheckBtlPos2", 0);
        putUnknownCt(0x708F, "btlDirPosBasic", 1);
        putUnknownCt(0x7090, "btlSetCriticalEffect", 1);
        putCtWithIdx(0x7091, new ScriptCallTarget("changeActorNameToCharName", "unknown", "btlChangeChrName", p("btlChr"), p("newName", "playerChar")));
        putCtWithIdx(0x7092, new ScriptCallTarget(null, "unknown", "btlGetGroundDist", p("btlChr")));
        putUnknownCt(0x7093, "btlCheckDirFlag", 0);
        putUnknownCt(0x7094, "btlSetTransVisible", 3);
        putUnknownCt(0x7095, "btlGetMoveFrameRest", 0);
        putUnknownCt(0x7096, "btlGetReflect", 0);
        putCtWithIdx(0x7097, new ScriptCallTarget("runBtlSceneB", "unknown", "btlOff2", p("btlScene", "int")));
        putUnknownCt(0x7098, "btlCheckDefenseMotion", "bool", 0);
        putUnknownCt(0x7099, "btlSetCursorType", 1);
        putUnknownCt(0x709A, "btlCheckPoison", 0);
        putUnknownCt(0x709B, "btlGetChrPosY", "float", 1);
        putUnknownCt(0x709C, "btlGetTargetDir", "float", 2);
        putUnknownCt(0x709D, "btlWaitMotion_avoid", 0);
        putCtWithIdx(0x709E, new ScriptCallTarget(null, "unknown", "btlSetMotionSignal", p("btlChr"), p(2), p(3)));
        putUnknownCt(0x709F, "btlGetChrTargetDir", "float", 1);
        putUnknownCt(0x70A0, "btlSetUpVectorFlag", 1);
        putCtWithIdx(0x70A1, new ScriptCallTarget("dereferenceEnemy", "btlChr", "btlGetChrNum2", p("btlChr")));
        putUnknownCt(0x70A2, "btlMotionRead", 1);
        putUnknownCt(0x70A3, "btlSetMotionAbs", 1);
        putUnknownCt(0x70A4, "btlMotionDispose", 0);
        putCtWithIdx(0x70A5, new ScriptCallTarget(null, "unknown", "btlSetMapCenter", p("x", "float"), p("y", "float"), p("z", "float")));
        putUnknownCt(0x70A6, "btlSetEscape", 1);
        putUnknownCt(0x70A7, "btlGetMotionData", "float", 2);
        putCtWithIdx(0x70A8, new ScriptCallTargetAccessor("setMotionValue", "btlChr", "btlSetMotionData", "=", "motionProperty"));
        putUnknownCt(0x70A9, "btlmeswait_voice", 1);
        putCtWithIdx(0x70AA, new ScriptCallTargetAccessor("readBtlChrProperty2", null, "btlGetStat2", null, "btlChrProperty"));
        putCtWithIdx(0x70AB, new ScriptCallTargetAccessor("setBtlChrProperty2", null, "btlSetStat2", "=", "btlChrProperty"));
        putCtWithIdx(0x70AC, new ScriptCallTargetAccessor("readMotionProperty2", null, "btlGetMotionData2", null, "motionProperty"));
        putUnknownCt(0x70AD, "btlCheckWakkaWeapon", 0);
        putUnknownCt(0x70AE, "btlGetLastDeathChr", "btlChr", 0);
        putUnknownCt(0x70AF, "btlGetVoiceFlag", 0);
        putUnknownCt(0x70B0, "btlDistTargetFrame2", 1);
        putUnknownCt(0x70B1, "btlPrintSp", 1);
        putCtWithIdx(0x70B2, new ScriptCallTargetAccessor("setMotionValue", null, "btlSetMotionData2", "=", "motionProperty"));
        putCtWithIdx(0x70B3, new ScriptCallTarget("setCommandDialogVoiceLine", "unknown", "btlVoiceSet", p("voiceFile")));
        putUnknownCt(0x70B4, "btlFadeOutWeapon", 0);
        putUnknownCt(0x70B5, "btlResetMotionSpeed", 0);
        putCtWithIdx(0x70B6, new ScriptCallTarget(null, "unknown", "btlDistTargetFrameSpd", p("btlChr")));
        putUnknownCt(0x70B7, "btlmesa", 2);
        putCtWithIdx(0x70B8, new ScriptCallTarget("setDefendingEnabled", "unknown", "btlSetSkipMode", p("enabled", "bool")));
        putUnknownCt(0x70B9, "btlGetCamWidth2", "float", 1);
        putUnknownCt(0x70BA, "btlGetCamHeight2", "float", 1);
        putUnknownCt(0x70BB, "btlMoveLeave", 1);
        putUnknownCt(0x70BC, "btlWaitNomEff", 1);
        putUnknownCt(0x70BD, "btlWaitHitEff", 1);
        putUnknownCt(0x70BE, "btlGetChrDir", "float", 1);
        putUnknownCt(0x70BF, "btlSetBindScale", 1);
        putUnknownCt(0x70C0, "btlGetHeight", "float", 1);
        putUnknownCt(0x70C1, "btlDistTarget2", 2);
        putUnknownCt(0x70C2, "btlGetTargetDirH", "float", 2);
        putUnknownCt(0x70C3, "btlGetChrTargetDir2", "float", 1);
        putUnknownCt(0x70C4, "btlEquipWakkaWeapon", 1);
        putUnknownCt(0x70C5, "btlCheckRetBtlPos", 0);
        putUnknownCt(0x70C6, "btlGetCameraBuffer", 1);
        putUnknownCt(0x70C7, "btlGetCameraBufferFloat", "float", 1);
        putUnknownCt(0x70C8, "btlSoundEffect2", 2);
        putCtWithIdx(0x70C9, new ScriptCallTarget(null, "unknown", "btlSoundEffect3", p("btlChr"), p("sfx", "btlSoundEffect")));
        putUnknownCt(0x70CA, "btlRegSoundEffect2", 2);
        putUnknownCt(0x70CB, "btlRegSoundEffect3", 2);
        putCtWithIdx(0x70CC, new ScriptCallTarget("initializeMatchingGroupTo", "unknown", "btlSetOwnTarget", p("btlChr")));
        putCtWithIdx(0x70CD, new ScriptCallTarget("addToMatchingGroup", "unknown", "btlAddOwnTarget", p("btlChr")));
        putCtWithIdx(0x70CE, new ScriptCallTarget("removeFromMatchingGroup", "unknown", "btlSubOwnTarget", p("btlChr")));
        putCtWithIdx(0x70CF, new ScriptCallTarget(null, "reverbeStatus", "btlGetReverbe", true));
        putUnknownCt(0x70D0, "btlSetCameraSelectMode", 1);
        putCtWithIdx(0x70D1, new ScriptCallTarget(null, "unknown", "btlGetNomEff", p("btlChr")));
        putUnknownCt(0x70D2, "btlGetHitEff", 1);
        putUnknownCt(0x70D3, "btlSetNomEff", 3);
        putUnknownCt(0x70D4, "btlSetHitEff", 3);
        putCtWithIdx(0x70D5, new ScriptCallTarget("setSummoner", "unknown", "btlSetSummoner", p("btlChr")));
        putCtWithIdx(0x70D6, new ScriptCallTarget("calculateAverageDamage", "int", "btlGetAssumeDamage", p("user", "btlChr"), p("target", "btlChr"), p("command")));
        putCtWithIdx(0x70D7, new ScriptCallTarget(null, "unknown", "btlSetDamageMotion", p("btlChr"), p("stdmotion")));
        putUnknownCt(0x70D8, "btlSetAnimaChainOff", 1);
        putUnknownCt(0x70D9, "btlExeAnimaChainOff", 0);
        putUnknownCt(0x70DA, "btlGetFirstAttack", "ambushState", 0);
        putUnknownCt(0x70DB, "btlGetAnimaChainOff", 0);
        putCtWithIdx(0x70DC, new ScriptCallTarget("changeChrName", "unknown", "btlChangeChrNameID", p("btlChr"), p("string", "localString")));
        putUnknownCt(0x70DD, "btlSetDebugCount", 1);
        putCtWithIdx(0x70DE, new ScriptCallTarget("SubtitlesEnabled?", "bool", "btlGetSubTitle", false));
        putCtWithIdx(0x70DF, new ScriptCallTarget("IsBattleInField", "unknown", "btlCheckBtlScene", p("battle")));
        putCtWithIdx(0x70E0, new ScriptCallTarget("isCounterattackAllowed", "bool", "btlGetReaction", true));
        putUnknownCt(0x70E1, "btlGetNormalAttack", 0);
        putCtWithIdx(0x70E2, new ScriptCallTarget(null, "unknown", "btlSetTexAnime", p("btlChr"), p(2)));
        putUnknownCt(0x70E3, "btlGetEffectMemory", 1);
        putUnknownCt(0x70E4, "btlGetCalcResultLimit", 2);
        putUnknownCt(0x70E5, "btlSetNomEffReg", 3);
        putCtWithIdx(0x70E6, new ScriptCallTarget(null, "unknown", "btlSetHitEffReg", p("btlChr"), p(2), p(3)));
        putUnknownCt(0x70E7, "btlSetRandomTarget", 1);
        putCtWithIdx(0x70E8, new ScriptCallTarget("PlayerTotalGil", "int", "btlGetGold", false));
        putCtWithIdx(0x70E9, new ScriptCallTarget("YojimboHireAnswer", "int", "btlGetYoujinboType", false));
        putCtWithIdx(0x70EA, new ScriptCallTarget("setYojimboHireAnswer", "unknown", "btlSetYoujinboType", p("int")));
        putUnknownCt(0x70EB, "btlGetYoujinboRandom", 0);
        putUnknownCt(0x70EC, "btlGetItemNum", 1);
        putCtWithIdx(0x70ED, new ScriptCallTarget("giveItem", "unknown", "btlGetItem", p("item", "command"), p("amount", "int")));
        putCtWithIdx(0x70EE, new ScriptCallTarget("RollYojimboCommand", "command", "btlGetYoujinboCommand", p("motivation", "int"), p("unknown")));
        putCtWithIdx(0x70EF, new ScriptCallTarget(null, "unknown", "btlSetEffSignal", p("btlChr"), p(2)));
        putUnknownCt(0x70F0, "btlGetCameraCount", "float", 0);
        putCtWithIdx(0x70F1, new ScriptCallTarget("clearOwnCommands", "unknown", "btlCommandClear", true));
        putCtWithIdx(0x70F2, new ScriptCallTarget("addCommandToSelf", "unknown", "btlCommandSet", p("command")));
        putCtWithIdx(0x70F3, new ScriptCallTarget("RollMagusRandom", "bool", "btlCheckMegasRandom", p("unknown"), p("chance", "int")));
        putUnknownCt(0x70F4, "btlGetCommandTarget", 2);
        putUnknownCt(0x70F5, "btlCheckUseCommand", 2);
        putUnknownCt(0x70F6, "btlInitCommandBuffer", 0);
        putCtWithIdx(0x70F7, new ScriptCallTarget(null, "unknown", "btlSetCommandBuffer", p("command")));
        putUnknownCt(0x70F8, "btlGetCommandBuffer", "command", 0);
        putUnknownCt(0x70F9, "btlSearchChr3", 5);
        putUnknownCt(0x70FA, "btlSetMegasRandomCommand", 1);
        putCtWithIdx(0x70FB, new ScriptCallTarget(null, "btlChr", "btlGetCommandTargetSearch", p("command"), p("targeting", "magusTarget"), p("property", "btlChrProperty"), p("selector"), p("chance", "int")));
        putUnknownCt(0x70FC, "btlGetMegasRandomCommand", 0);
        putCtWithIdx(0x70FD, new ScriptCallTarget("increaseMagusMotivationAndOverdrive", "unknown", "btlSetUpLimit", p("overdrive", "int"), p("motivation", "int")));
        putCtWithIdx(0x70FE, new ScriptCallTarget("setMagusMotivationAndOverdriveChangeInPositiveOrNegativeCase", "unknown", "btlSetUpLimit2", p("overdrivePos", "int"), p("motivationPos", "int"), p("overdriveNeg", "int"), p("motivationNeg", "int")));
        putUnknownCt(0x70FF, "btlSetDeltaTarget", 0);
        putUnknownCt(0x7100, "btlCheckReqMotion", 1);
        putCtWithIdx(0x7101, new ScriptCallTarget("isDebugBattleStart", "unknown", "btlGetFullCommand", true));
        putCtWithIdx(0x7102, new ScriptCallTarget("makeChrHeadFaceChr?", "unknown", "btlFaseTarget", p("btlChr"), p("target","btlChr")));
        putCtWithIdx(0x7103, new ScriptCallTarget("makeChrHeadFacePoint?", "unknown", "btlFaseTargetXYZ", p("btlChr"), p("x", "float"), p("y", "float"), p("z", "float")));
        putCtWithIdx(0x7104, new ScriptCallTarget("changeCommandAnimation", "unknown", "btlSetCommandEffect", p("command"), p("anim1", "magicFile"), p("anim2", "magicFile")));
        putUnknownCt(0x7105, "btlWaitStone", 0);
        putCtWithIdx(0x7106, new ScriptCallTarget("doesChrKnowCommand", "bool", "btlCheckGetCommand", p("btlChr"), p("command")));
        putUnknownCt(0x7107, "btlDirPosBasic2", 1);
        putUnknownCt(0x7108, "btlDirBasic2", 2);
        putCtWithIdx(0x7109, new ScriptCallTarget(null, "unknown", "btlSetAppear", p("btlChr"), p(2), p(3)));
        putUnknownCt(0x710A, "btlSetSummonTiming", 0);
        putUnknownCt(0x710B, "btlWaitSummonTiming", 0);
        putUnknownCt(0x710C, "btlTerminateStone", 0);
        putUnknownCt(0x710D, "btlDefensePosOff", 0);
        putUnknownCt(0x710E, "btlGetWakkaLimitSkill", 0);
        putUnknownCt(0x710F, "btlGetWakkaLimitNum", 0);
        putCtWithIdx(0x7110, new ScriptCallTarget("activateMouthMovement", "unknown", "btlMouseOn", p("btlChr")));
        putCtWithIdx(0x7111, new ScriptCallTarget("deactivateMouthMovement", "unknown", "btlMouseOff", p("btlChr")));
        putUnknownCt(0x7112, "btlDirMove2", 4);
        putUnknownCt(0x7113, "btlMonsterFarm", 0);
        putUnknownCt(0x7114, "btlSphereMonitor", 0);
        putUnknownCt(0x7115, "btlDirResetLeave", 0);
        putUnknownCt(0x7116, "btlSetSummonDefenseEffect", 0);
        putCtWithIdx(0x7117, new ScriptCallTarget("overrideDeathAnimationWithCommand", "unknown", "btlSetDeathCommand", p("target", "btlChr"), p("command")));
        putUnknownCt(0x7118, "btlSetSummonGameOver", 1);
        putUnknownCt(0x7119, "btlSetCounterFlag", 1);
        putUnknownCt(0x711A, "btlSetWind", 4);
        putUnknownCt(0x711B, "btlSetCameraStandard", 0);
        putUnknownCt(0x711C, "btlSetGameOverEffNum", 1);
        putUnknownCt(0x711D, "btlSetShadowHeight", 1);
        putCtWithIdx(0x7120, new ScriptCallTarget("displayBattleSystem01String?", "unknown", null, p("msgWindow", "int"), p("string", "system01String")));
        putUnknownCt(0x7123, 1);
        putCtWithIdx(0x7124, new ScriptCallTarget("setCommandDialogLineString", "unknown", null, p("string", "localString")));
        putUnknownCt(0x7125, 0);
        putUnknownCt(0x7126, 1);
        putUnknownCt(0x7127, 2);

        putCtWithIdx(0x8000, new ScriptCallTarget("setMapLayerVisibility", "unknown", null, p("layerIndex", "int"), p("visible", "bool")));
        putCtWithIdx(0x8001, new ScriptCallTarget("setSkyboxVisibility", "unknown", null, p("visible", "bool")));
        putCtWithIdx(0x8002, new ScriptCallTarget("setGfxActive?", "unknown", null, p("gfxIndex", "int"), p("active", "bool"))); // GFX are either lights or particle effects
        putCtWithIdx(0x8003, new ScriptCallTarget("startGfxTimer", "unknown", null, p("gfxIndex", "int")));
        putCtWithIdx(0x8004, new ScriptCallTarget("waitForGfxStopped", "unknown", null, p("gfxIndex", "int"))); // equivalent to 8005 with true
        putCtWithIdx(0x8005, new ScriptCallTarget("waitForGfxEnding", "unknown", null, p("gfxIndex", "int"), p("waitForChildren", "bool")));
        putUnknownCt(0x8006, 1);
        putCtWithIdx(0x8007, new ScriptCallTarget("setAllGfxActive", "unknown", null, true));
        putCtWithIdx(0x8008, new ScriptCallTarget("stopAllGfx", "unknown", null, true));
        putCtWithIdx(0x8009, new ScriptCallTarget("bindGfxToTarget", "unknown", null, p("gfxIndex", "int"), p("attachmentPoint", "int"))); // for actors, target is a bone index
        putCtWithIdx(0x800A, new ScriptCallTarget("bindGfxPosition", "unknown", "mpfpbindpos", p("gfxIndex", "int"), p("x", "float"), p("y", "float"), p("z", "float")));
        putCtWithIdx(0x800B, new ScriptCallTarget("unbindGfx", "unknown", null, p("gfxIndex", "int")));
        putCtWithIdx(0x800C, new ScriptCallTarget("setGfxEnabledGlobal", "unknown", null, p("enabled", "bool"))); // controls all gfx rendering/updates
        putCtWithIdx(0x800D, new ScriptCallTarget("setGfxVisibility", "unknown", null, p("gfxIndex", "int"), p("visible", "bool")));
        // next several are about the image planes used for forced-perspective areas
        putCtWithIdx(0x800F, new ScriptCallTarget("?show2DLayer", "unknown", null, p("layerIndex", "int"))); // clear image hidden flag
        putCtWithIdx(0x8010, new ScriptCallTarget("?hide2DLayer", "unknown", null, p("layerIndex", "int"))); // set image hidden flag
        putCtWithIdx(0x8011, new ScriptCallTarget("?set2DLayerPos", "unknown", null, p("layerIndex", "int"), p("?x", "int"), p("?y", "int")));
        putUnknownCt(0x8014, 2); // set image depth
        putCtWithIdx(0x801D, new ScriptCallTarget("?set2DLayerTexture", "unknown", null, p("layerIndex", "int"), p("textureIndex", "int")));
        putCtWithIdx(0x801E, new ScriptCallTarget(p("layerIndex", "int")));
        putCtWithIdx(0x801F, new ScriptCallTarget(p("layerIndex", "int")));
        putCtWithIdx(0x8020, new ScriptCallTarget(p("layerIndex", "int")));
        putCtWithIdx(0x8021, new ScriptCallTarget(p("layerIndex", "int")));
        putCtWithIdx(0x8022, new ScriptCallTarget(p("layerIndex", "int"), p(2)));
        putUnknownCt(0x8026, 5);
        putUnknownCt(0x802C, 0);
        putUnknownCt(0x802D, 0);
        putCtWithIdx(0x802E, new ScriptCallTarget(p("layerIndex", "int"), p(2)));
        putCtWithIdx(0x802F, new ScriptCallTarget(p("layerIndex", "int")));
        putCtWithIdx(0x8030, new ScriptCallTarget("?set2DLayerOpacity", "unknown", null, p("layerIndex", "int"), p("opacity", "int"))); // set image opacity
        putUnknownCt(0x8032, 1); // minimap related?
        putCtWithIdx(0x8035, new ScriptCallTarget("setGfxGroupActive", "unknown", null, p("group", "int"), p("active", "bool")));
        putCtWithIdx(0x8036, new ScriptCallTarget("setGfxGroupVisibility", "unknown", null, p("group", "int"), p("visible", "bool")));
        putCtWithIdx(0x8037, new ScriptCallTarget("setFogParams", "unknown", null, p("near", "float"), p("far", "float"), p("maxAlpha", "float")));
        putUnknownCt(0x8038, 1); // set/get fog components
        putUnknownCt(0x8039, 0);
        putUnknownCt(0x803A, 1);
        putUnknownCt(0x803B, 0);
        putUnknownCt(0x803C, 1);
        putUnknownCt(0x803D, 0);
        putCtWithIdx(0x803E, new ScriptCallTarget("setFogColor", "unknown", null, p("red", "int"), p("green", "int"), p("blue", "int")));
        putCtWithIdx(0x803F, new ScriptCallTarget("setFogRed", "unknown", null, p("red", "int")));
        putCtWithIdx(0x8040, new ScriptCallTarget("setFogGreen", "unknown", null, p("green", "int")));
        putCtWithIdx(0x8041, new ScriptCallTarget("setFogBlue", "unknown", null, p("blue", "int")));
        putCtWithIdx(0x8042, new ScriptCallTarget("getFogRed", "int", null, true));
        putCtWithIdx(0x8043, new ScriptCallTarget("getFogGreen", "int", null, true));
        putCtWithIdx(0x8044, new ScriptCallTarget("getFogBlue", "int", null, true));
        putCtWithIdx(0x8045, new ScriptCallTarget("setClearColor", "unknown", null, p("red", "int"), p("green", "int"), p("blue", "int")));
        putUnknownCt(0x8049, 0);
        putUnknownCt(0x804A, 0);
        putUnknownCt(0x804B, 0);
        putUnknownCt(0x804F, 3);
        putCtWithIdx(0x8059, new ScriptCallTarget("bindGfxToMapGroup", "unknown", null, p("gfxIndex", "int"), p("groupIndex", "int")));
        putCtWithIdx(0x805B, new ScriptCallTarget("setGfxPosition", "unknown", null, p("gfxIndex", "int"), p("x", "float"), p("y", "float"), p("z", "float")));
        putUnknownCt(0x805C, 1);
        putUnknownCt(0x805D, 0);
        putUnknownCt(0x805E, 1);
        putCtWithIdx(0x805F, new ScriptCallTarget("stopGfx", "unknown", null, p("gfxIndex", "int")));
        putCtWithIdx(0x8060, new ScriptCallTarget("stopGfxGroup", "unknown", null, p("groupIndex", "int")));
        putCtWithIdx(0x8066, new ScriptCallTarget("setGfxPausedGlobal", "unknown", null, p("paused", "bool")));
        putUnknownCt(0x8067, 4);
        putUnknownCt(0x806A, 1); // set an image plane's map group
        putUnknownCt(0x806B, 1); // totally empty?!
        putCtWithIdx(0xB000, new ScriptCallTarget("loadFmv", "unknown", null, p("fmv"), p("flags", "bitfield")));
        putCtWithIdx(0xB001, new ScriptCallTarget("?unloadFmv", "unknown", null, true));
        putUnknownCt(0xB002, 0);
        putCtWithIdx(0xB003, new ScriptCallTarget("CurrentFmvPlaybackProgress", "int", null, false));
        putCtWithIdx(0xB004, new ScriptCallTarget("?awaitFmv", "unknown", null, true));
        putCtWithIdx(0xB009, new ScriptCallTarget("?playLoadedFmv", "unknown", null, true));
        putUnknownCt(0xB00A, 2);
        putUnknownCt(0xB00B, 0);
        putUnknownCt(0xB00C, 0);
        putUnknownCt(0xB00D, 0);
        putUnknownCt(0xC002, 1);
        putUnknownCt(0xC003, 1);
        putUnknownCt(0xC007, 2);
        putUnknownCt(0xC009, 1);
        putUnknownCt(0xC00B, 1);
        putUnknownCt(0xC00C, 1);
        putUnknownCt(0xC00D, 1);
        putUnknownCt(0xC013, 0);
        putUnknownCt(0xC014, 0);
        putUnknownCt(0xC018, 1);
        putUnknownCt(0xC022, 1);
        putCtWithIdx(0xC024, new ScriptCallTarget("launchBattleAlwaysWin?", "unknown", null, p("battle"), p("transition", "battleTransition")));
        putUnknownCt(0xC025, 1);
        putCtWithIdx(0xC027, new ScriptCallTarget("RemoveAllKeyItemsAndPrimersRequireDebug", "unknown", null, true)); // Never called
        putUnknownCt(0xC028, 0);
        putUnknownCt(0xC02A, 1);
        putUnknownCt(0xC02C, 1);
        putUnknownCt(0xC02F, 2);
        putUnknownCt(0xC030, 0);
        putUnknownCt(0xC031, 1);
        putUnknownCt(0xC036, 1);
        putUnknownCt(0xC03B, 0);
        putUnknownCt(0xC03C, 0);
        putUnknownCt(0xC051, 1);
        putUnknownCt(0xC052, 0);
        putUnknownCt(0xC053, 4);
        putUnknownCt(0xC054, 1);
        putUnknownCt(0xC055, 1);
        putUnknownCt(0xC056, 3);
        putUnknownCt(0xC057, 1);
        putUnknownCt(0xC058, 1);
        putUnknownCt(0xC05B, 1);
    }

    public MenuButton getCallChoices(FuncChoiceAction action, String type) {
        Map<Integer, List<ScriptFuncChoice>> map = new HashMap<>();
        List<String> allowedTypes = List.of("unknown", "void", "bool", "int", "uint");
        boolean checkType = type != null && !allowedTypes.contains(type);
        for (ScriptCallTarget func : CALL_TARGETS) {
            if (func != null && (!checkType || type.equals(func.type) || allowedTypes.contains(func.type))) {
                List<ScriptFuncChoice> list = map.computeIfAbsent(func.funcspace, k -> new ArrayList<>());
                list.add(new ScriptFuncChoice(func));
            }
        }
        MenuButton menuButton = new MenuButton();
        for (Map.Entry<Integer, List<ScriptFuncChoice>> subEntry : map.entrySet()) {
            Menu menu = new Menu(ScriptConstants.FFX.FUNCSPACES[subEntry.getKey()]);
            for (ScriptFuncChoice choice : subEntry.getValue()) {
                MenuItem menuItem = new MenuItem(choice.func.getOptionLabel());
                menuItem.setOnAction(actionEvent -> action.act(choice, actionEvent));
                menuItem.setUserData(choice);
                menu.getItems().add(menuItem);
            }
            menuButton.getItems().add(menu);
        }
        return menuButton;
    }

    public static record ScriptFuncChoice(ScriptCallTarget func) {
        @Override
        public String toString() {
            return String.format("%04X: %s", func.idx, func.getLabel());
        }
    }

    public interface FuncChoiceAction {
        void act(ScriptFuncChoice choice, ActionEvent event);
    }
}
