package FarmController;

import FarmEngine.GameSettings;
import FarmEngine.SoundManager;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

public class SettingsController {
    @FXML private Slider autosaveSlider;
    @FXML private Slider volumeSlider;
    @FXML private ComboBox<String> languageCombo;
    @FXML private Label autosaveValueLabel;
    @FXML private Label volumeValueLabel;

    private Runnable onApply;

    @FXML
    public void initialize() {
        languageCombo.getItems().setAll("FR", "EN");
        autosaveSlider.setValue(GameSettings.getAutosaveIntervalSeconds());
        volumeSlider.setValue(GameSettings.getVolume() * 100.0);
        languageCombo.setValue(GameSettings.getLanguage());
        refreshLabels();

        autosaveSlider.valueProperty().addListener((obs, oldVal, newVal) -> refreshLabels());
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> refreshLabels());
    }

    public void setOnApply(Runnable onApply) {
        this.onApply = onApply;
    }

    @FXML
    private void onApplySettings() {
        GameSettings.setAutosaveIntervalSeconds((int) autosaveSlider.getValue());
        GameSettings.setVolume(volumeSlider.getValue() / 100.0);
        GameSettings.setLanguage(languageCombo.getValue());
        GameSettings.save();
        SoundManager.updateVolume();
        if (onApply != null) onApply.run();
        close();
    }

    @FXML
    private void onCancel() {
        close();
    }

    private void refreshLabels() {
        autosaveValueLabel.setText((int) autosaveSlider.getValue() + " sec");
        volumeValueLabel.setText((int) volumeSlider.getValue() + "%");
    }

    private void close() {
        Stage stage = (Stage) autosaveSlider.getScene().getWindow();
        stage.close();
    }
}
