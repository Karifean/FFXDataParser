package gui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import main.StringHelper;
import reading.FileAccessorWithMods;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import static main.DataReadingManager.DEFAULT_LOCALIZATION;

public class GuiMain extends Application {

    public static String mainLocalization = DEFAULT_LOCALIZATION;

    public GuiMain() {}

    public static void main(String[] args) {
        launch();
    }

    public void start(Stage primaryStage) throws IOException {
        URL fxmlUrl = getClass().getResource("/gui/main.fxml");
        Parent root = FXMLLoader.load(Objects.requireNonNull(fxmlUrl));
        primaryStage.setTitle("FFX ATEL Editor");
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.show();
    }
}
