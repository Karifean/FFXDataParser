package atel.model;

import atel.AtelScriptObject;

import java.util.HashMap;
import java.util.Map;

public class ScriptState {
    public ScriptJump entryPoint;
    public ScriptWorker parentWorker;
    public AtelScriptObject parentScript;
    public String rAType;
    public String rXType;
    public String rYType;
    public Map<Integer, String> tempITypes;

    public ScriptState(ScriptJump entryPoint) {
        this.entryPoint = entryPoint;
        this.parentWorker = entryPoint.parentWorker;
        this.parentScript = entryPoint.parentScript;
        this.rAType = entryPoint.rAType;
        this.rXType = entryPoint.rXType;
        this.rYType = entryPoint.rYType;
        this.tempITypes = entryPoint.tempITypes;
    }

    public ScriptState(ScriptState state) {
        this.entryPoint = state.entryPoint;
        this.parentWorker = state.parentWorker;
        this.parentScript = state.parentScript;
        this.rAType = state.rAType;
        this.rXType = state.rXType;
        this.rYType = state.rYType;
        this.tempITypes = new HashMap<>(state.tempITypes);
    }
}
