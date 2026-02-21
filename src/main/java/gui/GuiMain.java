package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(fxmlUrl));
        Parent root = fxmlLoader.load();
        GuiMainController controller = fxmlLoader.getController();
        controller.stage = primaryStage;
        primaryStage.setTitle("FFX Atelier");
        primaryStage.setScene(new Scene(root, 1366, 768));
        primaryStage.show();
    }
}
