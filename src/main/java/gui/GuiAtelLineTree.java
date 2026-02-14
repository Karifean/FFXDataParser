package gui;

import atel.AtelScriptObject;
import atel.EncounterFile;
import atel.EventFile;
import atel.MonsterFile;
import atel.model.*;
import atel.model.ScriptOpcode.OpcodeChoice;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import main.DataAccess;
import model.MonsterStatDataObject;
import model.strings.FieldString;
import model.strings.LocalizedFieldStringObject;

import java.util.*;

import static gui.GuiMain.mainLocalization;

public class GuiAtelLineTree {

    private GuiMainController controller;
    private ScriptLine scriptLine;
    private ScriptState state;

    private static final Map<ScriptInstruction, List<Double>> dividers = new HashMap<>();

    public GuiAtelLineTree(GuiMainController controller, ScriptLine line, ScriptState state) {
        this.controller = controller;
        this.scriptLine = line;
        this.state = state;
    }

    public static void clearDividersCache() {
        dividers.clear();
    }

    public VBox create() {
        VBox root = new VBox();
        appendInput(root, "void", scriptLine.lineEnder);
        return root;
    }

    private void appendInput(VBox vBox, String inputType, ScriptInstruction input) {
        HBox hBox = new HBox();
        vBox.getChildren().add(hBox);
        MenuButton opcodeSelect = makeOpcodeSelect(inputType, input);
        hBox.getChildren().add(opcodeSelect);
        Node opcodeArgInput = makeOpcodeArgInput(inputType, input);
        if (opcodeArgInput != null) {
            hBox.getChildren().add(opcodeArgInput);
        }
        appendSubInputSection(vBox, input);
    }

    private MenuButton makeOpcodeSelect(String inputType, ScriptInstruction input) {
        MenuButton opcodeSelect = ScriptOpcode.getOpcodeChoices((choice, event) -> onOpcodeChoiceMade(input, choice, event), inputType);
        MenuItem selected = findSelectedOpcodeItem(opcodeSelect, input);
        if (selected != null) {
            opcodeSelect.setText(selected.getText());
            opcodeSelect.setUserData(selected.getUserData());
        }
        return opcodeSelect;
    }

    private MenuItem findSelectedOpcodeItem(MenuButton opcodeSelect, ScriptInstruction input) {
        int searchKey = input.opcode;
        if (searchKey == 0xAD) {
            searchKey = 0xAE;
        }
        List<MenuItem> toCheck = new ArrayList<>(opcodeSelect.getItems());
        while (!toCheck.isEmpty()) {
            MenuItem first = toCheck.removeFirst();
            OpcodeChoice choice = (OpcodeChoice) first.getUserData();
            if (choice != null && choice.opcode() == searchKey) {
                return first;
            }
            if (first instanceof Menu) {
                toCheck.addAll(((Menu) first).getItems());
            }
        }
        return null;
    }

    private Node makeOpcodeArgInput(String inputType, ScriptInstruction input) {
        int opcode = input.opcode;
        if (opcode == 0xD8 || opcode == 0xB5) {
            MenuButton funcSelect = ScriptFuncLib.FFX.getCallChoices((choice, event) -> onIntArgvChanged(input, choice.func() != null ? choice.func().idx : null, event), inputType);
            MenuItem selected = findSelectedFuncItem(funcSelect, input);
            if (selected != null) {
                funcSelect.setText(selected.getText());
                funcSelect.setUserData(selected.getUserData());
            }
            return funcSelect;
        }
        if (opcode == 0xAD || opcode == 0xAE || opcode == 0xAF || opcode == 0xF6) {
            int val = opcode == 0xAE ? input.argvSigned : (input.dereferencedArg == null ? 0 : input.dereferencedArg);
            String content = opcode == 0xAF ? String.valueOf(Float.intBitsToFloat(val)) : String.valueOf(val);
            final boolean isFloat = opcode == 0xAF;
            TextField textField = new TextField(content);
            textField.setPrefWidth(100);
            textField.setOnAction(actionEvent -> {
                try {
                    if (isFloat) {
                        int newVal = Float.floatToIntBits(Float.parseFloat(textField.getText()));
                        onFloatArgvChanged(input, newVal, actionEvent);
                    } else {
                        int newVal = Integer.parseInt(textField.getText(), 10);
                        onIntArgvChanged(input, newVal, actionEvent);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            });
            if (opcode == 0xAF || opcode == 0xF6 || inputType.equals("unknown") || inputType.startsWith("int") || inputType.startsWith("uint")) {
                return textField;
            } else {
                return makeExplicitValueInputs(inputType, input, val, textField);
            }
        } else if ((opcode >= 0x9F && opcode <= 0xA4) || opcode == 0xA7) {
            MenuButton variableSelect = new MenuButton();
            if (input.dereferencedVar != null) {
                variableSelect.setText(input.dereferencedVar.getLabel(scriptLine.parentWorker));
            }
            for (int i = 0; i < scriptLine.parentScript.variableDeclarations.size(); i++) {
                final int varIndex = i;
                String variableLabel = scriptLine.parentScript.getVariableLabel(varIndex);
                if (input.dereferencedVar == null && varIndex == input.argv) {
                    variableSelect.setText(variableLabel);
                }
                if (!inputType.equals("float") || scriptLine.parentScript.getVariableType(varIndex).equals("float")) {
                    MenuItem variableMenuItem = new MenuItem(variableLabel);
                    variableMenuItem.setOnAction(actionEvent -> onIntArgvChanged(input, varIndex, actionEvent));
                    variableSelect.getItems().add(variableMenuItem);
                }
            }
            return variableSelect;
        } else if (opcode == 0xB3) {
            MenuButton workerSelect = new MenuButton();
            int workerCount = scriptLine.parentScript.workers.size();
            for (int i = 0; i < workerCount; i++) {
                final int workerIndex = i;
                ScriptWorker worker = scriptLine.parentScript.getWorker(workerIndex);
                String workerLabel = worker.getLabel(mainLocalization);
                if (i == input.argv) {
                    workerSelect.setText(workerLabel);
                }
                if (worker.eventWorkerType == 0) {
                    MenuItem workerMenuItem = new MenuItem(workerLabel);
                    workerMenuItem.setOnAction(actionEvent -> onIntArgvChanged(input, workerIndex, actionEvent));
                    workerSelect.getItems().add(workerMenuItem);
                }
            }
            return workerSelect;
        } else if (ScriptOpcode.OPCODES[opcode].branches) {
            return controller.branchHbox;
        }
        return null;
    }

    private Node makeExplicitValueInputs(String inputType, ScriptInstruction input, int selected, TextField textField) {
        HBox hBox = new HBox();
        MenuButton enumMenu = new MenuButton("_" + StackObject.asString(mainLocalization, inputType, selected));
        enumMenu.setMaxWidth(200);
        if ("room".equals(inputType)) {
            Map<String, Menu> folderMap = new HashMap<>();
            for (Map.Entry<Integer, String> eventEntry : EventFile.EVENT_BY_ROOM_ID.entrySet()) {
                String roomId = eventEntry.getValue();
                int i;
                for (i = 0; i < roomId.length(); i++) {
                    if (Character.isDigit(roomId.charAt(i))) {
                        break;
                    }
                }
                String submenuKey = roomId.substring(0, i);
                Menu folderMenu = folderMap.computeIfAbsent(submenuKey, k -> {
                    Menu menu = new Menu();
                    menu.setGraphic(new Text(submenuKey));
                    enumMenu.getItems().add(menu);
                    return menu;
                });
                addItem(folderMenu, roomId, inputType, input, eventEntry.getKey(), selected);
            }
        } else if ("encounter".equals(inputType)) {
            Map<String, Menu> folderMap = new HashMap<>();
            for (Map.Entry<String, EncounterFile> encounterEntry : DataAccess.ENCOUNTERS.entrySet()) {
                EncounterFile encounter = encounterEntry.getValue();
                int encounterIndex = encounter.getIndex();
                if (encounterIndex >= 0) {
                    String submenuKey = encounter.scriptId.substring(0, 4);
                    String label = encounter.getName(mainLocalization);
                    Menu folderMenu = folderMap.computeIfAbsent(submenuKey, k -> {
                        Menu menu = new Menu();
                        menu.setGraphic(new Text(submenuKey));
                        enumMenu.getItems().add(menu);
                        return menu;
                    });
                    addItem(folderMenu, label, inputType, input, encounterIndex, selected);
                }
            }
        } else if ("charCommand".equals(inputType)) {
            for (int i = 0x0000; i < 0x1000 && DataAccess.getCommand(i + 0x3000) != null; i++) {
                addItem(enumMenu, inputType, input, i, selected);
            }
        } else if ("command".equals(inputType)) {
            if (controller.selectedMonster != null) {
                Menu cmdlist = new Menu("Monster Command List");
                MonsterStatDataObject monsterStatData = controller.selectedMonster.monsterStatData;
                boolean needToAddForcedAction = DataAccess.getCommand(monsterStatData.forcedAction) != null;
                for (int cmd : monsterStatData.commandList) {
                    if (DataAccess.getCommand(cmd) != null) {
                        addItem(cmdlist, "command", input, cmd, selected);
                        if (cmd == monsterStatData.forcedAction) {
                            needToAddForcedAction = false;
                        }
                    }
                }
                if (needToAddForcedAction) {
                    addItem(cmdlist, "command", input, monsterStatData.forcedAction, selected);
                }
                enumMenu.getItems().add(cmdlist);
            }
            Menu commandbin = new Menu("Player Commands");
            for (int i = 0x3000; i < 0x4000 && DataAccess.getCommand(i) != null; i++) {
                addItem(commandbin, "command", input, i, selected);
            }
            enumMenu.getItems().add(commandbin);
            Menu monmagic1bin = new Menu("Monster Attacks (monmagic1)");
            for (int i = 0x4000; i < 0x5000 && DataAccess.getCommand(i) != null; i++) {
                addItem(monmagic1bin, "command", input, i, selected);
            }
            enumMenu.getItems().add(monmagic1bin);
            Menu monmagic2bin = new Menu("Boss Attacks (monmagic2)");
            for (int i = 0x6000; i < 0x7000 && DataAccess.getCommand(i) != null; i++) {
                addItem(monmagic2bin, "command", input, i, selected);
            }
            enumMenu.getItems().add(monmagic2bin);
            Menu itembin = new Menu("Items");
            for (int i = 0x2000; i < 0x3000 && DataAccess.getCommand(i) != null; i++) {
                addItem(itembin, "command", input, i, selected);
            }
            enumMenu.getItems().add(itembin);
        } else if ("localString".equals(inputType) || "system01String".equals(inputType)) {
            List<LocalizedFieldStringObject> strings = "system01String".equals(inputType) ? DataAccess.getEncounter("system_01").strings : controller.selectedAtelObject.strings;
            if (strings != null) {
                if (selected >= 0 && selected < strings.size()) {
                    LocalizedFieldStringObject obj = strings.get(selected);
                    String selectedStr = obj.getLocalizedString(mainLocalization);
                    if (selectedStr != null) {
                        enumMenu.setText(selectedStr);
                    }
                }
                for (int i = 0; i < strings.size(); i++) {
                    LocalizedFieldStringObject str = strings.get(i);
                    FieldString localizedContent = str.getLocalizedContent(mainLocalization);
                    String label = localizedContent != null ? localizedContent.getString() : "<Missing!>";
                    if (label.length() > 100) {
                        label = label.substring(0, 97) + "...";
                    }
                    addItem(enumMenu, label, inputType, input, i, selected);
                }
            }
            if (!"localString".equals(inputType) || controller.selectedEvent != null || controller.selectedEncounter != null) {
                MenuItem newStringItem = new MenuItem();
                newStringItem.setGraphic(new Text("<Add new String>"));
                newStringItem.setOnAction(actionEvent -> onAddString(input, inputType, actionEvent));
                enumMenu.getItems().add(newStringItem);
            }
        } else if (inputType != null && inputType.startsWith("bitfieldFrom_")) {
            String enumType = inputType.substring(13);
            Map<Integer, ScriptField> enumMap = ScriptConstants.FFX.ENUMERATIONS.get(enumType);
            List<ScriptField> valueList = enumMap.values().stream().sorted(Comparator.comparingInt(f -> f.idx)).toList();
            for (ScriptField option : valueList) {
                String label = StackObject.asString(mainLocalization, enumType, option.idx);
                addItem(enumMenu, label, inputType, input, 1 << option.idx, selected);
            }
        } else {
            Map<Integer, ScriptField> enumMap = ScriptConstants.FFX.ENUMERATIONS.get(inputType);
            if (enumMap != null) {
                List<ScriptField> valueList = enumMap.values().stream().sorted(Comparator.comparingInt(f -> f.idx)).toList();
                if ("model".equals(inputType)) {
                    Map<String, Menu> folderMap = new HashMap<>();
                    for (ScriptField option : valueList) {
                        String filePrefix = StackObject.getModelFilePrefix(option.idx);
                        Menu folderMenu = folderMap.computeIfAbsent(filePrefix, k -> {
                            Menu menu = new Menu();
                            menu.setGraphic(new Text(k.split("/")[0]));
                            enumMenu.getItems().add(menu);
                            return menu;
                        });
                        addItem(folderMenu, inputType, input, option.idx, selected);
                    }
                } else if ("motion".equals(inputType)) {
                    Map<String, Menu> folderMap = new HashMap<>();
                    Map<Integer, Menu> map = new HashMap<>();
                    for (ScriptField option : valueList) {
                        Menu modelMenu = map.computeIfAbsent((option.idx & 0xFFFF0000) >> 16, k -> {
                            String filePrefix = StackObject.getModelFilePrefix(k);
                            Menu folderMenu = folderMap.computeIfAbsent(filePrefix, k2 -> {
                                Menu menu = new Menu();
                                menu.setGraphic(new Text(k2.split("/")[0]));
                                enumMenu.getItems().add(menu);
                                return menu;
                            });
                            Menu menu = new Menu();
                            menu.setGraphic(new Text(StackObject.enumToString("model", k)));
                            folderMenu.getItems().add(menu);
                            return menu;
                        });
                        addItem(modelMenu, inputType, input, option.idx, selected);
                    }
                } else if ("btlChr".equals(inputType)) {
                    Menu group = new Menu();
                    group.setGraphic(new Text("Target Group"));
                    for (int i = -26; i < 0; i++) {
                        addItem(group, inputType, input, i, selected);
                    }
                    enumMenu.getItems().add(group);
                    Menu plyChars = new Menu();
                    plyChars.setGraphic(new Text("Player Characters"));
                    for (int i = 0; i < 0x14; i++) {
                        addItem(plyChars, inputType, input, i, selected);
                    }
                    enumMenu.getItems().add(plyChars);
                    Menu monstersByIndex = new Menu();
                    monstersByIndex.setGraphic(new Text("Monsters by index"));
                    for (int i = 0x14; i < 0x1C; i++) {
                        addItem(monstersByIndex, inputType, input, i, selected);
                    }
                    enumMenu.getItems().add(monstersByIndex);
                    Menu monstersByType = new Menu();
                    monstersByType.setGraphic(new Text("Monsters by type"));
                    for (int i = 0; i <= 360; i++) {
                        final int monsterIndex = i | 0x1000;
                        MonsterFile monster = DataAccess.getMonster(i);
                        if (monster != null) {
                            addItem(monstersByType, inputType, input, monsterIndex, selected);
                        }
                    }
                    enumMenu.getItems().add(monstersByType);
                    addItem(enumMenu, inputType, input, 0xFF, selected);
                } else {
                    for (ScriptField option : valueList) {
                        addItem(enumMenu, option.toString(), inputType, input, option.idx, selected);
                    }
                }
            }
        }
        if (!enumMenu.getItems().isEmpty()) {
            hBox.getChildren().add(enumMenu);
        }
        hBox.getChildren().add(textField);
        if ("localString".equals(inputType) || "system01String".equals(inputType)) {
            Button editButton = new Button("Edit String");
            editButton.setOnAction(actionEvent -> controller.editString(selected, inputType));
            hBox.getChildren().add(editButton);
        }
        return hBox;
    }

    private void addItem(MenuButton menu, String inputType, ScriptInstruction input, final int value, int selectedValue) {
        addItem(menu, StackObject.asString(mainLocalization, inputType, value), inputType, input, value, selectedValue);
    }

    private void addItem(MenuButton menu, String label, String inputType, ScriptInstruction input, final int value, int selectedValue) {
        MenuItem item = new MenuItem();
        Text text = new Text(label);
        boolean isBitfield = isBitfieldType(inputType);
        if (isSelected(isBitfield, value, selectedValue)) {
            text.setUnderline(true);
            if (isBitfield) {
                item.setOnAction(actionEvent -> onIntArgvChanged(input, -(value + 1) & selectedValue, actionEvent));
            }
        } else if (isBitfield) {
            item.setOnAction(actionEvent -> onIntArgvChanged(input, value | selectedValue, actionEvent));
        } else {
            item.setOnAction(actionEvent -> onIntArgvChanged(input, value, actionEvent));
        }
        item.setGraphic(text);
        menu.getItems().add(item);
    }

    private void addItem(Menu menu, String inputType, ScriptInstruction input, final int value, int selectedValue) {
        addItem(menu, StackObject.asString(mainLocalization, inputType, value), inputType, input, value, selectedValue);
    }

    private void addItem(Menu menu, String label, String inputType, ScriptInstruction input, final int value, int selectedValue) {
        MenuItem item = new MenuItem();
        Text text = new Text(label);
        boolean isBitfield = isBitfieldType(inputType);
        if (isSelected(isBitfield, value, selectedValue)) {
            text.setUnderline(true);
            if (isBitfield) {
                item.setOnAction(actionEvent -> onIntArgvChanged(input, -(value + 1) & selectedValue, actionEvent));
            }
        } else if (isBitfield) {
            item.setOnAction(actionEvent -> onIntArgvChanged(input, value | selectedValue, actionEvent));
        } else {
            item.setOnAction(actionEvent -> onIntArgvChanged(input, value, actionEvent));
        }
        item.setGraphic(text);
        menu.getItems().add(item);
    }

    private boolean isSelected(boolean isBitfield, int checkValue, int selectedValue) {
        return isBitfield ? (checkValue & selectedValue) != 0 : checkValue == selectedValue;
    }

    private boolean isBitfieldType(String inputType) {
        return inputType.startsWith("bitfield") || inputType.endsWith("Bitfield");
    }

    private MenuItem findSelectedFuncItem(MenuButton funcSelect, ScriptInstruction input) {
        int searchKey = input.argv;
        List<MenuItem> toCheck = new ArrayList<>(funcSelect.getItems());
        while (!toCheck.isEmpty()) {
            MenuItem first = toCheck.removeFirst();
            ScriptFuncLib.ScriptFuncChoice choice = (ScriptFuncLib.ScriptFuncChoice) first.getUserData();
            if (choice != null && choice.func() != null && choice.func().idx == searchKey) {
                return first;
            }
            if (first instanceof Menu) {
                toCheck.addAll(((Menu) first).getItems());
            }
        }
        return null;
    }

    private void appendSubInputSection(VBox vBox, ScriptInstruction instruction) {
        int args = instruction.getStackPops();
        if (args <= 0) {
            return;
        }
        if (args == 1) {
            appendSingleInputSection(vBox.getChildren(), instruction, 0);
        } else {
            SplitPane splitPane = new SplitPane();
            splitPane.setOrientation(Orientation.HORIZONTAL);
            vBox.getChildren().add(splitPane);
            for (int i = 0; i < args; i++) {
                appendSingleInputSection(splitPane.getItems(), instruction, i);
            }
            int dividerCount = splitPane.getItems().size() - 1;
            if (dividerCount > 0) {
                double standardFraction = 1 / ((double) dividerCount + 1.0);
                List<Double> dividerPositions = dividers.computeIfAbsent(instruction, k -> new ArrayList<>(dividerCount));
                for (int i = 0; i < dividerCount; i++) {
                    if (dividerPositions.size() > i) {
                        splitPane.setDividerPosition(i, dividerPositions.get(i));
                    } else {
                        double pos = (i + 1) * standardFraction;
                        dividerPositions.add(pos);
                        splitPane.setDividerPosition(i, pos);
                    }
                    final int idx = i;
                    splitPane.getDividers().get(idx).positionProperty().addListener((a, b, c) -> dividers.computeIfAbsent(instruction, k -> new ArrayList<>(dividerCount)).set(idx, c.doubleValue()));
                }
            }
        }
    }

    private void appendSingleInputSection(ObservableList<Node> list, ScriptInstruction instruction, int index) {
        String inputLabel = instruction.getInputLabel(index);
        if (inputLabel.startsWith("unused")) {
            return;
        }
        ScriptInstruction input = instruction.getInput(index);
        String inputType = instruction.getInputType(state, index);
        VBox vBox = new VBox();
        list.add(vBox);
        Text labelNode = new Text(inputLabel);
        vBox.getChildren().add(labelNode);
        appendInput(vBox, inputType, input);
    }

    private void onOpcodeChoiceMade(ScriptInstruction instruction, OpcodeChoice choice, ActionEvent event) {
        int opcode = choice.opcode();
        System.out.println("opcodeChoiceMade: " + opcode);
        if (opcode >= 0x00) {
            controller.changeInstructionOpcode(instruction, opcode, 0);
        }
    }

    private void onFloatArgvChanged(ScriptInstruction instruction, Integer argv, ActionEvent event) {
        if (argv == null) {
            return;
        }
        instruction.dereferencedArg = argv;
        if (scriptLine.parentWorker.refFloats != null) {
            for (int i = 0; i < scriptLine.parentWorker.refFloats.length; i++) {
                if (scriptLine.parentWorker.refFloats[i] == argv) {
                    controller.changeInstructionOpcode(instruction, 0xAF, i);
                    return;
                }
            }
        }
        controller.changeInstructionOpcode(instruction, 0xAF, scriptLine.parentWorker.refFloatCount++);
    }

    private void onIntArgvChanged(ScriptInstruction instruction, Integer argv, ActionEvent event) {
        if (argv == null) {
            return;
        }
        boolean isImmediate = argv <= 32767 && argv >= -32768;
        int opcode = instruction.opcode;
        if (isImmediate) {
            instruction.dereferencedArg = null;
            if (opcode == 0xAD) {
                controller.changeInstructionOpcode(instruction, 0xAE, argv);
            } else {
                if ((opcode >= 0x9F && opcode <= 0xA4) || opcode == 0xA7) {
                    instruction.dereferencedVar = scriptLine.parentWorker.getVariable(argv);
                }
                controller.changeInstructionArgv(instruction, argv);
            }
        } else if (opcode == 0xAD || opcode == 0xAE) {
            instruction.dereferencedArg = argv;
            if (scriptLine.parentWorker.refInts != null) {
                for (int i = 0; i < scriptLine.parentWorker.refInts.length; i++) {
                    if (scriptLine.parentWorker.refInts[i] == argv) {
                        controller.changeInstructionOpcode(instruction, 0xAD, i);
                        return;
                    }
                }
            }
            controller.changeInstructionOpcode(instruction, 0xAD, scriptLine.parentWorker.refIntCount++);
        } else {
            System.out.println("Cannot put argument that's not an int16 on this opcode");
        }
    }

    private void onAddString(ScriptInstruction instruction, String inputType, ActionEvent event) {
        int newIndex = controller.addString(inputType);
        onIntArgvChanged(instruction, newIndex, event);
    }
}
