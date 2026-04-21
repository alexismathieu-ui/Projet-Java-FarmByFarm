package FarmView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import FarmEngine.GameSettings;
import FarmEngine.AudioPaths;
import FarmEngine.SoundManager;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        GameSettings.load();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/MenuView.fxml"));
        Scene scene = new Scene(loader.load());

        primaryStage.setTitle("Pix'Farm");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> {
            SoundManager.stopMusic();
            Platform.exit();
            System.exit(0);
        });
        SoundManager.playMusic(AudioPaths.MUSIC_MENU);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}