package atel.model;

import atel.AtelScriptObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptState {
    public ScriptJump function;
    public ScriptWorker parentWorker;
    public AtelScriptObject parentScript;
    public String rAType;
    public String rXType;
    public String rYType;
    public Map<Integer, String> tempITypes;
    public boolean writeJumpsAsIndexedLines = false;
    public String localization = null;

    public final List<ScriptLine> lines;

    public ScriptState(ScriptJump function) {
        this(function, null);
    }

    public ScriptState(ScriptJump function, List<ScriptLine> lines) {
        this.function = function;
        this.lines = lines != null ? lines : function.getLines();
        this.parentWorker = function.parentWorker;
        this.parentScript = function.parentScript;
        this.rAType = function.rAType;
        this.rXType = function.rXType;
        this.rYType = function.rYType;
        this.tempITypes = function.tempITypes;
    }

    public ScriptState(ScriptState state) {
        this.function = state.function;
        this.lines = state.lines;
        this.parentWorker = state.parentWorker;
        this.parentScript = state.parentScript;
        this.rAType = state.rAType;
        this.rXType = state.rXType;
        this.rYType = state.rYType;
        this.tempITypes = new HashMap<>(state.tempITypes);
        this.writeJumpsAsIndexedLines = state.writeJumpsAsIndexedLines;
        this.localization = state.localization;
    }
}
