package gui;

import atel.model.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;

import static gui.GuiMain.mainLocalization;

public class GuiTableSetup {

    public static void setUpTables(GuiMainController controller) {
        controller.tableWorkersColumnIndex.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getIndexLabel()));
        controller.tableWorkersColumnLabel.setCellValueFactory(cdf -> new SimpleObjectProperty<>(cdf.getValue()));
        controller.tableWorkersColumnLabel.setCellFactory(col -> new TableCell<>() {
            final TextField input = new TextField();
            @Override
            protected void updateItem(ScriptWorker w, boolean empty) {
                super.updateItem(w, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(input);
                    if (w != null) {
                        input.setDisable(false);
                        input.setPromptText(w.getDefaultLabel(mainLocalization));
                        input.setText(w.declaredLabel != null ? w.declaredLabel : "");
                        input.setOnKeyTyped(ev -> controller.sanitizeLabelInput(input));
                        input.setOnAction(actionEvent -> controller.onRenameWorker(w, input));
                    } else {
                        input.setDisable(true);
                        input.setText("");
                    }
                }
            }
        });
        controller.tableWorkersColumnType.setCellValueFactory(cdf -> new SimpleObjectProperty<>(cdf.getValue()));
        controller.tableWorkersColumnType.setCellFactory(col -> new TableCell<>() {
            final MenuButton workerTypeChoiceBox = new MenuButton();
            @Override
            protected void updateItem(ScriptWorker w, boolean empty) {
                super.updateItem(w, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(workerTypeChoiceBox);
                    workerTypeChoiceBox.getItems().clear();
                    if (w != null) {
                        if (controller.selectedEvent == null) {
                            String str = w.purposeSlot != null ? StackObject.enumToString("battleWorkerSlot", w.purposeSlot) : "";
                            workerTypeChoiceBox.setText(str);
                            addBattleWorkerTypeChoices(controller, workerTypeChoiceBox.getItems(), w, controller.selectedMonster != null);
                        } else {
                            workerTypeChoiceBox.setText(StackObject.enumToString("eventWorkerType", w.eventWorkerType));
                            addEventWorkerTypeChoices(controller, workerTypeChoiceBox.getItems(), w);
                        }
                    } else {
                        workerTypeChoiceBox.setText("");
                    }
                }
            }
        });
        controller.tableWorkersColumnDelete.setCellValueFactory(cdf -> new SimpleObjectProperty<>(cdf.getValue()));
        controller.tableWorkersColumnDelete.setCellFactory(col -> new TableCell<>() {
            final Button delButton = new Button("Delete");
            @Override
            protected void updateItem(ScriptWorker w, boolean empty) {
                super.updateItem(w, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(delButton);
                    if (w != null) {
                        delButton.setOnAction(actionEvent -> controller.onDeleteWorker(w));
                    }
                }
            }
        });
        controller.tableVariablesColumnIndex.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getIndexLabel()));
        controller.tableVariablesColumnLabel.setCellValueFactory(cdf -> new SimpleObjectProperty<>(cdf.getValue()));
        controller.tableVariablesColumnLabel.setCellFactory(col -> new TableCell<>() {
            final TextField input = new TextField();
            @Override
            protected void updateItem(ScriptVariable vr, boolean empty) {
                super.updateItem(vr, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(input);
                    if (vr != null) {
                        boolean canRename = vr.canRename();
                        input.setDisable(!canRename);
                        input.setPromptText(vr.getLabel(null));
                        if (canRename) {
                            input.setText(vr.declaredLabel);
                            input.setOnKeyTyped(ev -> controller.sanitizeLabelInput(input));
                            input.setOnAction(actionEvent -> controller.onRenameVariable(vr, input));
                        } else {
                            input.setText("");
                        }
                    } else {
                        input.setDisable(true);
                        input.setText("");
                    }
                }
            }
        });
        controller.tableVariablesColumnType.setCellValueFactory(cdf -> new SimpleObjectProperty<>(cdf.getValue()));
        controller.tableVariablesColumnType.setCellFactory(col -> new TableCell<>() {
            final TextField input = new TextField();
            @Override
            protected void updateItem(ScriptVariable vr, boolean empty) {
                super.updateItem(vr, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(input);
                    if (vr != null) {
                        input.setText(vr.getType());
                        input.setOnAction(actionEvent -> controller.onChangeVariableType(vr, input.getText()));
                    } else {
                        input.setText("");
                    }
                }
            }
        });
        controller.tableVariablesColumnLocation.setCellValueFactory(cdf -> new SimpleObjectProperty<>(cdf.getValue()));
        controller.tableVariablesColumnLocation.setCellFactory(col -> new TableCell<>() {
            final MenuButton variableLocationChoiceBox = new MenuButton();
            @Override
            protected void updateItem(ScriptVariable vr, boolean empty) {
                super.updateItem(vr, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(variableLocationChoiceBox);
                    ObservableList<MenuItem> items = variableLocationChoiceBox.getItems();
                    items.clear();
                    for (int i = 0; i <= 6; i++) {
                        final int loc = i;
                        MenuItem item = new MenuItem(ScriptVariable.locationToString(loc));
                        item.setOnAction(actionEvent -> controller.onChangeVariableLocation(vr, loc));
                        items.add(item);
                    }
                    if (vr != null) {
                        variableLocationChoiceBox.setText(ScriptVariable.locationToString(vr.location));
                    } else {
                        variableLocationChoiceBox.setText("");
                    }
                }
            }
        });
        controller.tableVariablesColumnOffset.setCellValueFactory(cdf -> new SimpleObjectProperty<>(cdf.getValue()));
        controller.tableVariablesColumnOffset.setCellFactory(col -> new TableCell<>() {
            final TextField input = new TextField();
            @Override
            protected void updateItem(ScriptVariable vr, boolean empty) {
                super.updateItem(vr, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(input);
                    if (vr != null) {
                        input.setText(String.format("%d", vr.offset));
                        input.setOnAction(actionEvent -> controller.onChangeVariableOffset(vr, input.getText()));
                    } else {
                        input.setText("");
                    }
                }
            }
        });
        controller.tableVariablesColumnArray.setCellValueFactory(cdf -> new SimpleObjectProperty<>(cdf.getValue()));
        controller.tableVariablesColumnArray.setCellFactory(col -> new TableCell<>() {
            final HBox hbox = new HBox();
            final CheckBox checkBox = new CheckBox();
            final TextField elementCountInput = new TextField();
            final TextField elementSizeInput = new TextField();
            @Override
            protected void updateItem(ScriptVariable vr, boolean empty) {
                super.updateItem(vr, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(hbox);
                    if (hbox.getChildren().isEmpty()) {
                        hbox.getChildren().add(checkBox);
                        hbox.getChildren().add(elementCountInput);
                        hbox.getChildren().add(elementSizeInput);
                        hbox.setSpacing(10);
                        elementCountInput.setPrefWidth(30);
                        elementSizeInput.setPrefWidth(30);
                    }
                    if (vr != null) {
                        boolean isArray = vr.elementCount > 1;
                        checkBox.setSelected(isArray);
                        elementCountInput.setText(String.format("%d", vr.elementCount));
                        elementSizeInput.setText(vr.elementSize > 0 ? String.format("%d", vr.elementSize) : "");
                        elementCountInput.setVisible(isArray);
                        elementSizeInput.setVisible(isArray);
                        checkBox.setOnAction(actionEvent -> controller.onChangeVariableArrayValues(vr, checkBox.isSelected(), vr.elementCount <= 1 ? 2 : vr.elementCount, vr.elementSize));
                        elementCountInput.setOnAction(actionEvent -> controller.onChangeVariableArrayValues(vr, true, Integer.parseInt(elementCountInput.getText(), 10), vr.elementSize));
                        elementSizeInput.setOnAction(actionEvent -> controller.onChangeVariableArrayValues(vr, true, vr.elementCount, Integer.parseInt(elementSizeInput.getText(), 10)));
                    } else {
                        checkBox.setSelected(false);
                        elementCountInput.setText("");
                        elementSizeInput.setText("");
                        elementCountInput.setVisible(false);
                        elementSizeInput.setVisible(false);
                    }
                }
            }
        });
        controller.tableVariablesColumnFormat.setCellValueFactory(cdf -> new SimpleObjectProperty<>(cdf.getValue()));
        controller.tableVariablesColumnFormat.setCellFactory(col -> new TableCell<>() {
            final MenuButton variableFormatChoiceBox = new MenuButton();
            @Override
            protected void updateItem(ScriptVariable vr, boolean empty) {
                super.updateItem(vr, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(variableFormatChoiceBox);
                    ObservableList<MenuItem> items = variableFormatChoiceBox.getItems();
                    items.clear();
                    for (int i = 0; i <= 6; i++) {
                        final int format = i;
                        MenuItem item = new MenuItem(ScriptVariable.formatToType(format));
                        item.setOnAction(actionEvent -> controller.onChangeVariableFormat(vr, format));
                        items.add(item);
                    }
                    if (vr != null) {
                        variableFormatChoiceBox.setText(ScriptVariable.formatToType(vr.format));
                    } else {
                        variableFormatChoiceBox.setText("");
                    }
                }
            }
        });
        controller.tableVariablesColumnDelete.setCellValueFactory(cdf -> new SimpleObjectProperty<>(cdf.getValue()));
        controller.tableVariablesColumnDelete.setCellFactory(col -> new TableCell<>() {
            final Button delButton = new Button("Delete");
            @Override
            protected void updateItem(ScriptVariable vr, boolean empty) {
                super.updateItem(vr, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(delButton);
                    if (vr != null) {
                        delButton.setOnAction(actionEvent -> controller.onDeleteVariable(vr));
                    }
                }
            }
        });
        controller.tableEntryPointsColumnIndex.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getIndexLabel()));
        controller.tableEntryPointsColumnLabel.setCellValueFactory(cdf -> new SimpleObjectProperty<>(cdf.getValue()));
        controller.tableEntryPointsColumnLabel.setCellFactory(col -> new TableCell<>() {
            final TextField input = new TextField();
            @Override
            protected void updateItem(ScriptJump ep, boolean empty) {
                super.updateItem(ep, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(input);
                    if (ep != null) {
                        input.setDisable(false);
                        input.setPromptText(ep.getDefaultLabel());
                        input.setText(ep.declaredLabel != null ? ep.declaredLabel : "");
                        input.setOnKeyTyped(ev -> controller.sanitizeLabelInput(input));
                        input.setOnAction(actionEvent -> controller.onRenameJump(ep, input));
                    } else {
                        input.setDisable(true);
                        input.setText("");
                    }
                }
            }
        });
        controller.tableEntryPointsColumnType.setCellValueFactory(cdf -> new SimpleObjectProperty<>(cdf.getValue()));
        controller.tableEntryPointsColumnType.setCellFactory(col -> new TableCell<>() {
            final MenuButton entryPointTypeChoiceBox = new MenuButton();
            @Override
            protected void updateItem(ScriptJump ep, boolean empty) {
                super.updateItem(ep, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    if (ep != null) {
                        if (ep.parentWorker.battleWorkerType == null || ep.jumpIndex <= 1) {
                            Text text = new Text(ScriptJump.eventWorkerEntryPointToString(ep.parentWorker.eventWorkerType, ep.jumpIndex));
                            text.setDisable(true);
                            setGraphic(text);
                        } else {
                            setGraphic(entryPointTypeChoiceBox);
                            entryPointTypeChoiceBox.getItems().clear();
                            addBattleWorkerEntryPointTypeChoices(controller, entryPointTypeChoiceBox, ep);
                        }
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
        controller.tableEntryPointsColumnDelete.setCellValueFactory(cdf -> new SimpleObjectProperty<>(cdf.getValue()));
        controller.tableEntryPointsColumnDelete.setCellFactory(col -> new TableCell<>() {
            final Button delButton = new Button("Delete");
            @Override
            protected void updateItem(ScriptJump ep, boolean empty) {
                super.updateItem(ep, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(delButton);
                    if (ep != null && ep.isEntryPoint) {
                        if (!ep.canDelete()) {
                            delButton.setDisable(true);
                        } else {
                            delButton.setDisable(false);
                            delButton.setOnAction(actionEvent -> controller.onDeleteEntryPoint(ep));
                        }
                    }
                }
            }
        });
    }

    private static void addEventWorkerTypeChoices(GuiMainController controller, ObservableList<MenuItem> list, ScriptWorker w) {
        for (Map.Entry<Integer, ScriptField> entry : ScriptConstants.FFX.ENUMERATIONS.get("eventWorkerType").entrySet()) {
            final Integer val = entry.getKey();
            MenuItem item = new MenuItem(entry.getValue().toString());
            list.add(item);
            item.setOnAction(actionEvent -> controller.setWorkerEventType(w, val));
        }
    }

    private static void addBattleWorkerTypeChoices(GuiMainController controller, ObservableList<MenuItem> list, ScriptWorker w, boolean isMonster) {
        Map<Integer, Menu> menus = new HashMap<>();
        for (Map.Entry<Integer, ScriptField> entry : ScriptConstants.FFX.ENUMERATIONS.get("battleWorkerSlot").entrySet()) {
            final Integer val = entry.getKey();
            if (isMonster && val != 0x00 && val != 0x04 && val != 0x3D && val != 0x40) {
                continue;
            }
            MenuItem item = new MenuItem(entry.getValue().toString());
            item.setOnAction(actionEvent -> controller.setWorkerBattleSlot(w, val));
            if (!isMonster) {
                Menu parentForSlot = menus.computeIfAbsent(ScriptWorker.getTypeForSlot(val), k -> {
                    if (k == null) {
                        return null;
                    }
                    Menu submenu = new Menu(StackObject.asString(mainLocalization, "battleWorkerType", k));
                    list.add(submenu);
                    return submenu;
                });
                if (parentForSlot != null) {
                    parentForSlot.getItems().add(item);
                } else {
                    list.add(item);
                }
            } else {
                list.add(item);
            }
        }
    }

    private static void addBattleWorkerEntryPointTypeChoices(GuiMainController controller, MenuButton menu, ScriptJump ep) {
        ScriptWorker w = ep.parentWorker;
        String tagType = switch (w.battleWorkerType) {
            case 0 -> "cameraHandlerTag";
            case 1 -> "motionHandlerTag";
            case 2 -> "combatHandlerTag";
            case 3 -> "battleGruntHandlerTag";
            case 4 -> "btlScene";
            case 5 -> "voiceHandlerTag";
            case 6 -> "battleStartEndHookTag";
            case 7, 8, 9, 10 -> "command";
            default -> null;
        };
        int bonus = switch (w.battleWorkerType) {
            case 7 -> 0x3000;
            case 8 -> 0x2000;
            case 9 -> 0x4000;
            case 10 -> 0x6000;
            default -> 0;
        };
        ObservableList<MenuItem> list = menu.getItems();
        int selected = ep.battleWorkerEntryPointSlot != null ? ep.battleWorkerEntryPointSlot : -1;
        for (int i = 0; i < w.battleWorkerTypeSlotCount; i++) {
            final int val = i;
            String s = StackObject.asString(mainLocalization, tagType, val + bonus);
            MenuItem item = new MenuItem(s);
            if (selected == i) {
                menu.setText(s);
            }
            list.add(item);
            item.setOnAction(actionEvent -> controller.setBattleWorkerEntryPointType(ep, val));
        }
    }
}
