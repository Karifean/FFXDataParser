package gui;

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

import java.util.*;

import static gui.GuiMain.mainLocalization;

public class GuiAtelLineTree {

    private GuiMainController controller;
    private ScriptLine scriptLine;
    private ScriptState state;

    public GuiAtelLineTree(GuiMainController controller, ScriptLine line, ScriptState state) {
        this.controller = controller;
        this.scriptLine = line;
        this.state = state;
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
        OpcodeChoice selectedOpcodeItem = (OpcodeChoice) opcodeSelect.getUserData();
        if (selectedOpcodeItem.opcode() == -2) {
            MenuButton typeSelect = makeTypeSelect(inputType, input);
            hBox.getChildren().add(typeSelect);
        }
        Node opcodeArgInput = makeOpcodeArgInput(selectedOpcodeItem, inputType, input);
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

    private MenuButton makeTypeSelect(String inputType, ScriptInstruction input) {
        String selected = input.getOutputType(state);
        MenuButton typeSelect = new MenuButton(selected);
        List<String> types = ScriptConstants.FFX.ENUMERATIONS.keySet().stream().filter(s -> !inputType.equals(s)).toList();
        for (String t : types) {
            typeSelect.getItems().add(new MenuItem(t));
        }
        return typeSelect;
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

    private Node makeOpcodeArgInput(OpcodeChoice selectedOption, String inputType, ScriptInstruction input) {
        int opcode = input.opcode;
        if (opcode == 0xD8 || opcode == 0xB5) {
            MenuButton funcSelect = ScriptFuncLib.FFX.getCallChoices((choice, event) -> onArgvChoiceMade(input, choice.func() != null ? choice.func().idx : null, event), inputType);
            MenuItem selected = findSelectedFuncItem(funcSelect, input);
            if (selected != null) {
                funcSelect.setText(selected.getText());
                funcSelect.setUserData(selected.getUserData());
            }
            return funcSelect;
        }
        if (opcode == 0xAD || opcode == 0xAE || opcode == 0xAF || opcode == 0xF6) {
            int val = opcode == 0xAE ? input.argvSigned : input.dereferencedArg;
            String content = opcode == 0xAF ? String.valueOf(Float.intBitsToFloat(val)) : String.valueOf(val);
            final boolean isFloat = opcode == 0xAF || inputType.equals("float");
            TextField textField = new TextField(content);
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
            for (int i = 0; i < scriptLine.parentScript.variableDeclarations.length; i++) {
                final int varIndex = i;
                String variableLabel = scriptLine.parentScript.getVariableLabel(varIndex);
                if (varIndex == input.argv) {
                    variableSelect.setText(variableLabel);
                }
                if (!inputType.equals("float") || scriptLine.parentScript.getVariableType(varIndex).equals("float")) {
                    MenuItem variableMenuItem = new MenuItem(variableLabel);
                    variableMenuItem.setOnAction(actionEvent -> onArgvChoiceMade(input, varIndex, actionEvent));
                    variableSelect.getItems().add(variableMenuItem);
                }
            }
            return variableSelect;
        } else if (opcode == 0xB3) {
            MenuButton workerSelect = new MenuButton();
            int workerCount = scriptLine.parentScript.getWorkers().size();
            for (int i = 0; i < workerCount; i++) {
                final int workerIndex = i;
                ScriptWorker worker = scriptLine.parentScript.getWorker(workerIndex);
                String workerLabel = worker.getLabel(mainLocalization);
                if (i == input.argv) {
                    workerSelect.setText(workerLabel);
                }
                if (worker.eventWorkerType == 0) {
                    MenuItem workerMenuItem = new MenuItem(workerLabel);
                    workerMenuItem.setOnAction(actionEvent -> onArgvChoiceMade(input, workerIndex, actionEvent));
                    workerSelect.getItems().add(workerMenuItem);
                }
            }
            return workerSelect;
        }
        return null;
    }

    private Node makeExplicitValueInputs(String inputType, ScriptInstruction input, int val, TextField textField) {
        HBox hBox = new HBox();
        Map<Integer, ScriptField> enumMap = ScriptConstants.FFX.ENUMERATIONS.get(inputType);
        if (enumMap != null) {
            ScriptField selected = StackObject.enumToScriptField(inputType, val);
            System.out.printf("Found selection of type %s for argv %04X and it is %s%n", inputType, val, selected);
            MenuButton enumMenu = new MenuButton(selected.toString());
            enumMenu.setUserData(selected);
            List<ScriptField> valueList = enumMap.values().stream().sorted(Comparator.comparingInt(f -> f.idx)).toList();
            for (ScriptField option : valueList) {
                System.out.printf("Found option %s%n", option.toString());
                MenuItem menuItem = new MenuItem(option.toString());
                menuItem.setUserData(option);
                menuItem.setOnAction(actionEvent -> onArgvChoiceMade(input, option.idx, actionEvent));
                enumMenu.getItems().add(menuItem);
            }
            hBox.getChildren().add(enumMenu);
        }
        hBox.getChildren().add(textField);
        return hBox;
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
        }
    }

    private void appendSingleInputSection(ObservableList<Node> list, ScriptInstruction instruction, int index) {
        String inputLabel = instruction.getInputLabel(index);
        if (inputLabel.equals("unused")) {
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

    private void onArgvChoiceMade(ScriptInstruction instruction, Integer argv, ActionEvent event) {
        System.out.println("argvChoiceMade: " + argv);
        if (argv != null) {
            controller.changeInstructionArgv(instruction, argv);
        }
    }

    private void onFloatArgvChanged(ScriptInstruction instruction, Integer argv, ActionEvent event) {
        System.out.println("onFloatArgvChanged: " + argv);
        if (argv != null) {
            instruction.dereferencedArg = argv;
            controller.changeInstructionOpcode(instruction, 0xAF, scriptLine.parentWorker.refFloatCount++);
        }
    }

    private void onIntArgvChanged(ScriptInstruction instruction, Integer argv, ActionEvent event) {
        System.out.println("onIntArgvChanged: " + argv);
        if (argv == null) {
            return;
        }
        boolean isImmediate = argv < 32767 && argv >= -32768;
        boolean wasImmediate = instruction.opcode != 0xAD;
        if (isImmediate) {
            instruction.dereferencedArg = null;
            if (wasImmediate) {
                controller.changeInstructionArgv(instruction, argv);
            } else {
                controller.changeInstructionOpcode(instruction, 0xAE, argv);
            }
        } else {
            instruction.dereferencedArg = argv;
            controller.changeInstructionOpcode(instruction, 0xAD, scriptLine.parentWorker.refIntCount++);
        }
    }
}
