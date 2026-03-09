package FarmController;

import Farm.Farms;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MenuController {

    @FXML
    public void handleNewGame(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/MainView.fxml"));
            Parent mainRoot = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(mainRoot);

            scene.getStylesheets().add(getClass().getResource("/FarmView/style.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Farm My Farm - Ma Ferme");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la ferme.");
        }
    }

    @FXML
    public void handleLoadGame(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/MainView.fxml"));
            Parent root = loader.load();


            MainController mainCtrl = loader.getController();
            Farms currentFarm = mainCtrl.getFarms();

            FarmEngine.SaveSystem.load(currentFarm);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

            System.out.println("Partie chargée avec succès !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleQuit(ActionEvent event) {
        Platform.exit();
    }
}