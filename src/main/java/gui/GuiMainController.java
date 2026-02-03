package gui;

import atel.AtelScriptObject;
import atel.EncounterFile;
import atel.EventFile;
import atel.MonsterFile;
import atel.model.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import main.DataAccess;

import java.net.URL;
import java.util.*;

import static gui.GuiMain.mainLocalization;

public class GuiMainController implements Initializable {
    private static final String LEGAL_HEX_CHARS = "0123456789ABCDEFabcdef ";
    private static final String LEGAL_INT_CHARS = "0123456789";

    @FXML
    ListView<String> eventList;
    @FXML
    ListView<String> encounterList;
    @FXML
    ListView<String> monsterList;
    @FXML
    ListView<String> miscList;
    @FXML
    TreeView<TreeEntry> middleTree;
    @FXML
    ListView<String> scriptLineList;
    @FXML
    VBox lineGuiVbox;
    @FXML
    TextField branchLineInput;
    @FXML
    Label branchLineLabel;
    @FXML
    Button branchLineButton;
    @FXML
    TextField hexLineInput;

    EventFile selectedEvent;
    EncounterFile selectedEncounter;
    MonsterFile selectedMonster;
    AtelScriptObject selectedMisc;
    AtelScriptObject selectedAtelObject;
    ScriptJump selectedEntryPoint;
    ScriptState scriptState;
    List<ScriptLine> scriptLines;
    int selectedLineIndex = -1;
    ScriptLine selectedLine;

    private boolean choosingBranchTarget = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        eventList.getSelectionModel().selectedItemProperty().addListener((o, s, newValue) -> onEventSelected(newValue));
        encounterList.getSelectionModel().selectedItemProperty().addListener((o, s, newValue) -> onEncounterSelected(newValue));
        monsterList.getSelectionModel().selectedItemProperty().addListener((o, s, newValue) -> onMonsterSelected(newValue));
        middleTree.getSelectionModel().selectedItemProperty().addListener((o, s, newValue) -> onTreeEntrySelected(newValue));
        scriptLineList.getSelectionModel().selectedItemProperty().addListener((o, s, newValue) -> onLineSelected(newValue));
        makeLists();
    }

    public void makeLists() {
        eventList.getItems().clear();
        for (Map.Entry<String, EventFile> eventEntry : DataAccess.EVENTS.entrySet()) {
            String label = eventEntry.getValue().getName(mainLocalization);
            eventList.getItems().add(label);
        }
        eventList.getItems().sort(Comparator.naturalOrder());
        encounterList.getItems().clear();
        for (Map.Entry<String, EncounterFile> encounterEntry : DataAccess.ENCOUNTERS.entrySet()) {
            String label = encounterEntry.getValue().getName(mainLocalization);
            encounterList.getItems().add(label);
        }
        encounterList.getItems().sort(Comparator.naturalOrder());
        monsterList.getItems().clear();
        for (int i = 0; i <= 360; i++) {
            MonsterFile monster = DataAccess.getMonster(i);
            if (monster != null) {
                String idStr = String.format("m%03d", i);
                String name = monster.getName(mainLocalization);
                String label = idStr + " - " + name;
                monsterList.getItems().add(label);
            }
        }
        miscList.getItems().clear();
        miscList.getItems().add(DataAccess.MENUMAIN.scriptId);
    }

    public void onEventSelected(String label) {
        System.out.println("event selected: " + label);
        String eventId = label.split(" ")[0];
        clearSelection();
        selectedEvent = DataAccess.getEvent(eventId);
        if (selectedEvent == null) {
            throw new RuntimeException("Monster not found");
        }
        selectedEvent.parseScript();
        selectedAtelObject = selectedEvent.eventScript;
        makeTree();
    }

    public void onEncounterSelected(String label) {
        System.out.println("encounter selected: " + label);
        String encounterId = label.split(" ")[0];
        clearSelection();
        selectedEncounter = DataAccess.getEncounter(encounterId);
        if (selectedEncounter == null) {
            throw new RuntimeException("Monster not found");
        }
        selectedEncounter.parseScript();
        selectedAtelObject = selectedEncounter.encounterScript;
        makeTree();
    }

    public void onMonsterSelected(String label) {
        System.out.println("monster selected: " + label);
        String monsterId = label.split(" ")[0].substring(1);
        clearSelection();
        selectedMonster = DataAccess.getMonster(monsterId);
        if (selectedMonster == null) {
            throw new RuntimeException("Monster not found");
        }
        selectedMonster.parseScript();
        selectedAtelObject = selectedMonster.monsterScript;
        makeTree();
    }

    private void clearSelection() {
        selectedEvent = null;
        selectedEncounter = null;
        selectedMonster = null;
        selectedMisc = null;
        selectedAtelObject = null;
        choosingBranchTarget = false;
        onLineSelected(null);
    }

    public void makeTree() {
        TreeItem<TreeEntry> treeRoot = treeItem("root");
        treeRoot.setExpanded(true);
        middleTree.setRoot(treeRoot);
        if (selectedAtelObject == null) {
            return;
        }
        treeRoot.setGraphic(new Text(selectedAtelObject.scriptId));
        ObservableList<TreeItem<TreeEntry>> rootChildren = treeRoot.getChildren();
        if (selectedEvent != null) {
            treeRoot.setGraphic(new Text(selectedEvent.getName(mainLocalization)));
            TreeItem<TreeEntry> eventGeneralItem = treeItem("eg");
            rootChildren.add(eventGeneralItem);
        }
        if (selectedEncounter != null) {
            treeRoot.setGraphic(new Text(selectedEncounter.getName(mainLocalization)));
            TreeItem<TreeEntry> encounterGeneralItem = treeItem("bg");
            rootChildren.add(encounterGeneralItem);
        }
        if (selectedMonster != null) {
            treeRoot.setGraphic(new Text(selectedMonster.getName(mainLocalization)));
            TreeItem<TreeEntry> monsterGeneralItem = treeItem("mg");
            rootChildren.add(monsterGeneralItem);
        }
        TreeItem<TreeEntry> atelGeneralItem = treeItem("sg");
        rootChildren.add(atelGeneralItem);
        for (ScriptWorker worker : selectedAtelObject.getWorkers()) {
            TreeItem<TreeEntry> workerTreeItem = treeItem("wr", worker, null);
            rootChildren.add(workerTreeItem);
            workerTreeItem.getChildren().add(treeItem("wg", worker, null));
            for (ScriptJump entryPoint : worker.getEntryPoints()) {
                TreeItem<TreeEntry> entryPointItem = treeItem("e", worker, entryPoint);
                workerTreeItem.getChildren().add(entryPointItem);
            }
        }
    }

    private TreeItem<TreeEntry> treeItem(String type) {
        return treeItem(type, null, null);
    }

    private TreeItem<TreeEntry> treeItem(String type, ScriptWorker worker, ScriptJump entryPoint) {
        return new TreeItem<>(new TreeEntry(this, type, worker, entryPoint));
    }

    public void onTreeEntrySelected(TreeItem<TreeEntry> item) {
        System.out.println("tree entry selected: " + item);
        selectedEntryPoint = null;
        choosingBranchTarget = false;
        if (item != null) {
            selectedEntryPoint = item.getValue().entryPoint();
        }
        adaptToSelectedEntryPoint();
    }

    public void adaptToSelectedEntryPoint() {
        if (selectedEntryPoint == null) {
            scriptState = null;
            scriptLines = null;
        } else {
            scriptState = new ScriptState(selectedEntryPoint);
            scriptLines = scriptState.lines;
            scriptState.writeJumpsAsIndexedLines = true;
        }
        setScriptLines();
    }

    public void setScriptLines() {
        scriptLineList.getItems().clear();
        if (scriptLines == null || scriptLines.isEmpty()) {
            return;
        }
        for (int i = 0; i < scriptLines.size(); i++) {
            ScriptLine line = scriptLines.get(i);
            scriptLineList.getItems().add(String.format("#%d: %s", i, line.lineEnder.asString(scriptState)));
        }
    }

    public void onLineSelected(String label) {
        int index = -1;
        if (label != null && label.length() > 2) {
            try {
                index = Integer.parseInt(label.substring(1).split(":")[0], 10);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (choosingBranchTarget) {
            choosingBranchTarget = false;
            setBranchTarget(index);
            scriptLineList.getSelectionModel().select(selectedLineIndex);
            return;
        }
        selectedLineIndex = index;
        selectedLine = index < 0 ? null : scriptLines.get(selectedLineIndex);
        System.out.println("selecting line index " + index + " ergo line: " + selectedLine);
        adaptToSelectedLine();
    }

    public void adaptToSelectedLine() {
        lineGuiVbox.getChildren().clear();
        if (selectedLine == null) {
            hexLineInput.setText("");
            hexLineInput.setDisable(true);
            branchLineInput.setText("");
            branchLineLabel.setText("-");
            branchLineInput.setDisable(true);
            branchLineLabel.setDisable(true);
            branchLineButton.setDisable(true);
            return;
        }
        fillLineGuiVbox();
        hexLineInput.setText(selectedLine.asHexString());
        hexLineInput.setDisable(false);
        if (selectedLine.branch != null) {
            ScriptLine targetLine = selectedLine.branch.targetLine;
            int lineIndex = scriptLines.indexOf(targetLine);
            branchLineInput.setText(lineIndex < 0 ? "" : String.valueOf(lineIndex));
            branchLineLabel.setText(lineIndex < 0 ? "-" : targetLine.lineEnder.asString(scriptState));
        } else {
            branchLineInput.setText("");
            branchLineLabel.setText("-");
        }
        boolean cannotBranch = selectedLine.lineEnder.getBranchIndex() == null;
        branchLineInput.setDisable(cannotBranch);
        branchLineLabel.setDisable(cannotBranch);
        branchLineButton.setDisable(cannotBranch);
    }

    public void fillLineGuiVbox() {
        if (selectedLine == null || selectedLine.lineEnder == null) {
            return;
        }
        lineGuiVbox.getChildren().add(new GuiAtelLineTree(this, selectedLine, scriptState).create());
    }

    public void changeInstructionOpcode(ScriptInstruction instruction, Integer opcode, Integer argv) {
        if (opcode != null && opcode >= 0x00) {
            selectedLine.changeInstructionOpcode(instruction, opcode, argv != null ? argv : 0);
            adaptLineToChangedInstructions();
        }
    }

    public void changeInstructionArgv(ScriptInstruction instruction, Integer argv) {
        if (argv != null) {
            System.out.println("instructionBefore " + instruction + " / line=" + selectedLine);
            selectedLine.changeInstructionArgv(instruction, argv);
            System.out.println("instructionAfter " + instruction + " / line=" + selectedLine);
            adaptLineToChangedInstructions();
        }
    }

    @FXML
    public void onSave() {
        if (selectedEvent != null) {
            selectedEvent.writeToMods(false, true);
            System.out.println("saved selected event");
        }
        if (selectedEncounter != null) {
            selectedEncounter.writeToMods(false, true);
            System.out.println("saved selected encounter");
        }
        if (selectedMonster != null) {
            selectedMonster.writeToMods(false, true);
            System.out.println("saved selected monster");
        }
        makeTree();
    }

    @FXML
    public void onAddWorker() {
        System.out.println("onAddWorker");
        List<ScriptWorker> workers = selectedAtelObject.getWorkers();
        if (workers.isEmpty()) {
            return;
        }
        ScriptWorker worker = new ScriptWorker(selectedAtelObject, workers.size(), workers.getFirst(), selectedEvent == null ? 2 : 0);
        workers.add(worker);
        worker.addBlankEntryPoints();
        makeTree();
        Optional<TreeItem<TreeEntry>> workerTreeItem = middleTree.getRoot().getChildren().stream().filter(w -> w.getValue().worker == worker).findAny();
        workerTreeItem.ifPresent(item -> {
            item.setExpanded(true);
            middleTree.getSelectionModel().select(item.getChildren().getFirst());
        });
    }

    @FXML
    public void onChooseBranchTarget() {
        System.out.println("onChooseBranchTarget");
        choosingBranchTarget = true;
    }

    @FXML
    public void onBranchLineInputAction(ActionEvent event) {
        sanitizeBranchLineInput();
        try {
            int newIndex = Integer.parseInt(branchLineInput.getText(), 10);
            setBranchTarget(newIndex);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void setBranchTarget(int newIndex) {
        if (newIndex < 0 || newIndex >= scriptLines.size()) {
            return;
        }
        ScriptWorker worker = selectedLine.parentWorker;
        ScriptJump branch = new ScriptJump(worker, -1, worker.jumpCount, false);
        selectedLine.branch = branch;
        selectedLine.lineEnder.setArgv(branch.jumpIndex);
        worker.jumps.add(branch);
        worker.jumpCount++;
        ScriptLine newTargetLine = scriptLines.get(newIndex);
        branch.targetLine = newTargetLine;
        newTargetLine.incomingJumps.add(branch);
        branchLineLabel.setText(newTargetLine.lineEnder.asString(scriptState));
        scriptLineList.getItems().set(selectedLineIndex, String.format("#%d: %s", selectedLineIndex, selectedLine.lineEnder.asString(scriptState)));
    }

    @FXML
    public void onHexLineInputAction(ActionEvent event) {
        sanitizeHexLineInput();
        String hexLineString = hexLineInput.getText().replace(" ", "");
        setLineToHexLineString(hexLineString);
    }

    private void setLineToHexLineString(String hexLineString) {
        if (hexLineString.isBlank()) {
            return;
        }
        List<Integer> bytes = new ArrayList<>();
        int max = hexLineString.length() - 1;
        for (int i = 0; i < max; i += 2) {
            bytes.add(Integer.parseInt(hexLineString.substring(i, i + 2), 16));
        }
        setLineToBytesList(bytes);
    }

    private void setLineToBytesList(List<Integer> bytes) {
        List<ScriptInstruction> instructions = new ArrayList<>();
        boolean lastWasLineEnder = false;
        boolean isOnlyNop = true;
        for (int i = 0; i < bytes.size(); i++) {
            if (lastWasLineEnder) {
                return;
            }
            int offset = i;
            int opcode = bytes.get(i);
            lastWasLineEnder = ScriptOpcode.OPCODES[opcode].isLineEnd;
            if (opcode >= 0x80 && opcode < 0xFF) {
                isOnlyNop = false;
                i++;
                int arg1 = bytes.get(i);
                i++;
                int arg2 = bytes.get(i);
                ScriptInstruction instruction = new ScriptInstruction(offset, opcode, arg1, arg2);
                instructions.add(instruction);
                if (opcode == 0xAD) {
                    if (instruction.argv < selectedAtelObject.refInts.length) {
                        instruction.dereferencedArg = selectedAtelObject.refInts[instruction.argv];
                    } else {
                        instruction.dereferencedArg = 0;
                    }
                } else if (opcode == 0xAE) {
                    instruction.dereferencedArg = instruction.argv;
                } else if (opcode == 0xAF) {
                    if (instruction.argv < selectedAtelObject.refFloats.length) {
                        instruction.dereferencedArg = selectedAtelObject.refFloats[instruction.argv];
                    } else {
                        instruction.dereferencedArg = 0;
                    }
                } else if ((opcode >= 0x9F && opcode <= 0xA4) || opcode == 0xA7) {
                    if (instruction.argv < selectedAtelObject.variableDeclarations.length) {
                        instruction.dereferencedVar = selectedAtelObject.variableDeclarations[instruction.argv];
                    }
                }
            } else if (opcode == 0x00) {
                int count = 1;
                while ((i + 1) < bytes.size() && bytes.get(i + 1) == 0x00) {
                    count++;
                    i++;
                }
                instructions.add(new ScriptInstruction(offset, 0x00, count));
            } else {
                isOnlyNop = false;
                instructions.add(new ScriptInstruction(offset, opcode));
            }
        }
        if (!lastWasLineEnder && (!isOnlyNop || instructions.isEmpty())) {
            return;
        }
        setLineToInstructionsList(instructions);
    }

    private void setLineToInstructionsList(List<ScriptInstruction> instructions) {
        boolean isLast = selectedLineIndex == scriptLines.size() - 1;
        selectedLine.setInstructions(instructions, isLast ? null : scriptLines.get(selectedLineIndex + 1));
        adaptLineToChangedInstructions();
    }

    private void adaptLineToChangedInstructions() {
        boolean isLast = selectedLineIndex == scriptLines.size() - 1;
        Integer branchIndex = !selectedLine.instructions.isEmpty() ? selectedLine.instructions.getLast().getBranchIndex() : null;
        if (branchIndex != null) {
            ScriptJump jump = selectedLine.parentWorker.getJump(branchIndex);
            selectedLine.branch = jump;
            int lineIndex = jump != null ? scriptLines.indexOf(jump.targetLine) : -1;
            branchLineInput.setText(lineIndex < 0 ? "" : String.valueOf(lineIndex));
            branchLineLabel.setText(lineIndex < 0 ? "-" : jump.targetLine.lineEnder.asString(scriptState));
        }
        if (isLast && selectedLine.continues()) {
            ScriptInstruction retInstruction = new ScriptInstruction(0, 0x3C);
            ScriptLine finalLine = new ScriptLine(selectedLine.parentWorker, 0, List.of(retInstruction), List.of());
            scriptLineList.getItems().add(String.format("#%d: %s", scriptLines.size(), retInstruction.asString(scriptState)));
            finalLine.predecessor = selectedLine;
            selectedLine.successor = finalLine;
            scriptLines.add(finalLine);
        }
        scriptLineList.getItems().set(selectedLineIndex, String.format("#%d: %s", selectedLineIndex, selectedLine.lineEnder.asString(scriptState)));
        adaptToSelectedLine();
    }

    @FXML
    public void onHexLineInputTyped(KeyEvent event) {
        sanitizeHexLineInput();
    }

    private void sanitizeHexLineInput() {
        StringBuilder sanitizer = new StringBuilder();
        int caretPosition = hexLineInput.getCaretPosition();
        String original = hexLineInput.getText().toUpperCase();
        for (int i = 0; i < original.length(); i++) {
            char ch = original.charAt(i);
            if (LEGAL_HEX_CHARS.indexOf(ch) >= 0) {
                sanitizer.append(ch);
            }
        }
        String sanitized = sanitizer.toString();
        hexLineInput.setText(sanitized);
        hexLineInput.positionCaret(caretPosition);
    }

    @FXML
    public void onBranchLineInputTyped(KeyEvent event) {
        sanitizeBranchLineInput();
    }

    private void sanitizeBranchLineInput() {
        StringBuilder sanitizer = new StringBuilder();
        int caretPosition = branchLineInput.getCaretPosition();
        String original = branchLineInput.getText();
        for (int i = 0; i < original.length(); i++) {
            char ch = original.charAt(i);
            if (LEGAL_INT_CHARS.indexOf(ch) >= 0) {
                sanitizer.append(ch);
            }
        }
        String sanitized = sanitizer.toString();
        branchLineInput.setText(sanitized);
        branchLineInput.positionCaret(caretPosition);
    }

    @FXML
    public void onAddLine() {
        System.out.println("onAddLine selectedLineIndex=" + selectedLineIndex + " and line=" + selectedLine);
        ScriptInstruction blankIns = new ScriptInstruction(0, 0x00);
        ScriptLine newLine = new ScriptLine(selectedEntryPoint.parentWorker, -1, List.of(blankIns), List.of());
        if (selectedLineIndex < 0 || selectedLineIndex >= scriptLines.size() - 1) {
            scriptLines.add(0, newLine);
            ScriptLine previousStartLine = selectedEntryPoint.targetLine;
            selectedEntryPoint.targetLine = newLine;
            newLine.successor = previousStartLine;
            previousStartLine.predecessor = newLine;
            setScriptLines();
            scriptLineList.getSelectionModel().select(0);
        } else {
            int nextLineIndex = selectedLineIndex + 1;
            ScriptLine nextLine = scriptLines.get(nextLineIndex);
            scriptLines.add(nextLineIndex, newLine);
            if (selectedLine.continues()) {
                selectedLine.successor = newLine;
                newLine.predecessor = selectedLine;
            }
            if (newLine.continues()) {
                newLine.successor = nextLine;
                nextLine.predecessor = newLine;
            } else {
                nextLine.predecessor = null;
            }
            setScriptLines();
            scriptLineList.getSelectionModel().select(nextLineIndex);
        }
    }

    @FXML
    public void onRemoveLine() {
        if (selectedLineIndex < 0 || selectedLine == null) {
            return;
        }
        if (selectedLineIndex == scriptLines.size() - 1) {
            List<ScriptInstruction> instructions = new ArrayList<>();
            instructions.add(new ScriptInstruction(0, 0x3C));
            setLineToInstructionsList(instructions);
            return;
        }
        ScriptLine nextLine = scriptLines.get(selectedLineIndex + 1);
        nextLine.predecessor = selectedLine.predecessor;
        nextLine.incomingJumps.addAll(selectedLine.incomingJumps);
        selectedLine.incomingJumps.forEach(j -> j.targetLine = nextLine);
        int previousLineIndex = selectedLineIndex > 0 ? selectedLineIndex - 1 : 0;
        if (selectedLineIndex > 0) {
            ScriptLine previousLine = scriptLines.get(previousLineIndex);
            if (previousLine.continues()) {
                previousLine.successor = nextLine;
                nextLine.predecessor = previousLine;
            }
        }
        scriptLines.remove(selectedLine);
        setScriptLines();
        scriptLineList.getSelectionModel().select(previousLineIndex);
    }

    private static record TreeEntry(GuiMainController ctrl, String type, ScriptWorker worker, ScriptJump entryPoint) {
        @Override
        public String toString() {
            return switch (type) {
                case "eg" -> "Event General";
                case "bg" -> "Encounter General";
                case "mg" -> "Monster General";
                case "sg" -> "Script General";
                case "wr" -> worker.getLabel(mainLocalization);
                case "wg" -> "Worker General";
                case "e" -> entryPoint.getLabel();
                default -> "";
            };
        }
    }
}
