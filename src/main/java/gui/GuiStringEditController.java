package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import main.DataReadingManager;
import main.StringHelper;
import model.strings.FieldString;
import model.strings.LocalizedFieldStringObject;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class GuiStringEditController implements Initializable {

    @FXML
    ListView<Lang> langList;
    @FXML
    TextArea textArea;
    @FXML
    TextArea textAreaSimplified;
    @FXML
    Text warnHint;

    LocalizedFieldStringObject fieldString;
    String selectedLang = GuiMain.mainLocalization;
    String selectedCharset = StringHelper.localizationToCharset(selectedLang);
    FieldString selectedString;

    List<Integer> invalidIndicesRegular = List.of();
    List<Integer> invalidIndicesSimplified = List.of();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        langList.getSelectionModel().selectedItemProperty().addListener((o, s, newValue) -> onLangSelected(newValue));
    }

    public void setUpLangList() {
        langList.getItems().clear();
        if (fieldString == null) {
            return;
        }
        Lang selected = null;
        for (String locale : DataReadingManager.LOCALIZATIONS_LIST) {
            final String label = DataReadingManager.LOCALIZATIONS.get(locale);
            Lang obj = new Lang(locale, label, fieldString);
            if (locale.equals(selectedLang)) {
                selected = obj;
            }
            langList.getItems().add(obj);
        }
        if (selected != null) {
            langList.getSelectionModel().select(selected);
        }
    }

    public void onLangSelected(Lang lang) {
        selectedLang = lang.key;
        selectedCharset = StringHelper.localizationToCharset(selectedLang);
        if (fieldString == null) {
            return;
        }
        selectedString = fieldString.getLocalizedContent(selectedLang);
        textArea.setText(selectedString.getRegularStringMultiline());
        if (selectedString.hasDistinctSimplified()) {
            textAreaSimplified.setText(selectedString.getSimplifiedStringMultiline());
        } else {
            textAreaSimplified.setText("");
        }
        checkInvalidCharacters();
    }

    @FXML
    public void onTypeRegular() {
        checkInvalidCharacters();
        if (invalidIndicesRegular.isEmpty()) {
            String text = textArea.getText();
            selectedString.setRegularString(text);
            langList.refresh();
        }
    }

    @FXML
    public void onTypeSimplified() {
        checkInvalidCharacters();
        if (invalidIndicesSimplified.isEmpty()) {
            String text = textAreaSimplified.getText();
            if (text.isEmpty()) {
                selectedString.setSimplifiedUndistinct();
            } else {
                selectedString.setSimplifiedString(text);
            }
            langList.refresh();
        }
    }

    private void checkInvalidCharacters() {
        warnHint.setText("");
        String regular = textArea.getText();
        StringHelper.InvalidChars invalidCharactersRegular = StringHelper.getInvalidCharacters(regular, selectedCharset);
        if (invalidCharactersRegular != null) {
            warnHint.setText("Invalid characters: " + invalidCharactersRegular.chars());
            invalidIndicesRegular = invalidCharactersRegular.indices();
        } else {
            invalidIndicesRegular = List.of();
        }
        String simplified = textAreaSimplified.getText();
        StringHelper.InvalidChars invalidCharactersSimplified = StringHelper.getInvalidCharacters(simplified, selectedCharset);
        if (invalidCharactersSimplified != null) {
            String full = invalidCharactersRegular != null ? invalidCharactersRegular.chars() + " " + invalidCharactersSimplified.chars() : invalidCharactersSimplified.chars();
            warnHint.setText("Invalid characters: " + full);
            invalidIndicesSimplified = invalidCharactersSimplified.indices();
        } else {
            invalidIndicesSimplified = List.of();
        }
    }

    private record Lang(String key, String label, LocalizedFieldStringObject obj) {
        @Override
        public String toString() {
            String str = obj.getLocalizedString(key);
            String val = str != null && !str.isEmpty() ? str : "<Missing>";
            return key + ": " + val;
        }
    }
}
