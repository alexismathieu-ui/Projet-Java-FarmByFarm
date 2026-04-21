package FarmController;

import Farm.Crops.*;
import Farm.Culture;
import Farm.Farms;
import Farm.Plot;
import Farm.Quest;
import FarmEngine.GameTimer;
import FarmEngine.GameBalance;
import FarmEngine.GameSettings;
import FarmEngine.I18n;
import FarmEngine.AudioPaths;
import FarmEngine.SaveSystem;
import FarmEngine.SoundManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import java.io.IOException;
import java.util.Random;
import javafx.util.Duration;

public class MainController {
    @FXML private GridPane farmGrid;
    @FXML private Label moneyLabel;
    @FXML private Label labelStatus;
    @FXML private Button barnButton;
    @FXML private ProgressBar xpBar;
    @FXML private Label levelLabel;
    @FXML private Label xpLabel;
    @FXML private Label weatherLabel;
    @FXML private Button adminBtn;
    @FXML private ListView<String> eventLogList;

    private Farms farms;
    private GameTimer gameTimer;
    private String selectedActions = "NONE";
    private String selectedSeed = "Wheat_Seed";
    private InventoryController currentInventoryCtrl;
    private Stage inventoryStage;
    private final int requiredLevel = GameBalance.BARN_UNLOCK_LEVEL;
    private Timeline autosaveTimer;
    private final ObservableList<String> eventLogItems = FXCollections.observableArrayList();
    private int lastDisplayedLevel = 1;

    @FXML
    public void initialize() {
        if (this.farms == null){
            this.farms = new Farms(20);
        }
        if (eventLogList != null) {
            eventLogList.setItems(eventLogItems);
        }
        refreshGrid();
    }

    private void updateUI(){
        moneyLabel.setText(I18n.tr("main.money", (int) farms.getMoney()));
        refreshGrid();

        double progress = farms.getCurrentXP() / farms.getNextLevelXP();
        xpBar.setProgress(progress);
        levelLabel.setText(I18n.tr("main.level", farms.getLevel()));
        xpLabel.setText((int)farms.getCurrentXP() + " / " + (int)farms.getNextLevelXP() + " XP");

        barnButton.setDisable(farms.getLevel() < 5);
        if (farms.getLevel() >= 5) {
            barnButton.setDisable(false);
            barnButton.setText(I18n.tr("main.barn.open") + " 🏠");
        } else {
            barnButton.setDisable(true);
            barnButton.setText(I18n.tr("main.barn.locked"));
        }

        String weatherText = switch (farms.getCurrentWeather()) {
            case SUNNY -> "☀️ Soleil (x1)";
            case RAINY -> "🌧️ Pluie (x1.5)";
            case THUNDERSTORM -> "⚡ Orage (x2)";
            case DROUGHT -> "🔥Sécheresse (x0.5)";
        };
        weatherLabel.setText(I18n.tr("main.weather", weatherText));

        if (farms.getLevel() > lastDisplayedLevel) {
            logEvent("Niveau " + farms.getLevel() + " atteint.");
            lastDisplayedLevel = farms.getLevel();
        } else if (farms.getLevel() < lastDisplayedLevel) {
            lastDisplayedLevel = farms.getLevel();
        }
    }

    private Image textureSol  = null;
    private Image textureGrass = null;

    private Image getTextureSol() {
        if (textureSol == null) {
            java.io.InputStream is = getClass().getResourceAsStream("/Sprite/images/Fonds + Graphics/Sol.png");
            if (is != null) textureSol = new Image(is);
        }
        return textureSol;
    }

    private Image getTextureGrass() {
        if (textureGrass == null) {
            java.io.InputStream is = getClass().getResourceAsStream("/Sprite/images/Fonds + Graphics/Grass.png");
            if (is != null) textureGrass = new Image(is);
        }
        return textureGrass;
    }

    private ImageView makeCell(Image texture, int size) {
        ImageView iv = new ImageView(texture);
        iv.setFitWidth(size);
        iv.setFitHeight(size);
        iv.setPreserveRatio(false);
        return iv;
    }

    private void refreshGrid(){
        farmGrid.getChildren().clear();
        boolean pricedisplay = false;

        final int CELL = 82;

        for (int i = 0 ; i < farms.getNbLINES(); i++) {
            for (int j = 0; j < farms.getNbCOLMUNS(); j++) {
                Plot plotting = farms.getField()[i][j];

                StackPane visualCell = new StackPane();
                visualCell.setPrefSize(CELL, CELL);
                visualCell.setMaxSize(CELL, CELL);

                if (plotting.isLocked()) {
                    Image grass = getTextureGrass();
                    if (grass != null) {
                        ImageView bg = makeCell(grass, CELL);
                        bg.setOpacity(0.35);
                        visualCell.getChildren().add(bg);
                    } else {
                        Rectangle r = new Rectangle(CELL, CELL, Color.web("#3d3d3d"));
                        visualCell.getChildren().add(r);
                    }

                    if (!pricedisplay) {
                        double currentCost = farms.getNextPlotCost();
                        Label priceLabel = new Label("🛒\n" + (int)currentCost + "$");
                        priceLabel.setTextFill(Color.GOLD);
                        priceLabel.setStyle("-fx-font-weight: bold; -fx-text-alignment: center; -fx-font-family: 'Courier New'; -fx-font-size: 12px;");
                        visualCell.setStyle("-fx-border-color: gold; -fx-border-width: 2;");
                        visualCell.getChildren().add(priceLabel);
                        visualCell.setOnMouseClicked(event -> handlePurchasePlot(plotting));
                        pricedisplay = true;
                    } else {
                        Label lockIcon = new Label("🔒");
                        lockIcon.setStyle("-fx-font-size: 20px;");
                        visualCell.getChildren().add(lockIcon);
                        visualCell.setOnMouseClicked(null);
                    }

                } else {
                    Image sol = getTextureSol();
                    if (sol != null) {
                        visualCell.getChildren().add(makeCell(sol, CELL));
                    } else {
                        Rectangle r = new Rectangle(CELL, CELL, Color.SADDLEBROWN);
                        visualCell.getChildren().add(r);
                    }
                    visualCell.setStyle("-fx-border-color: #5c3010; -fx-border-width: 1;");

                    if (plotting.getActualCulture() != null) {
                        Culture culture = plotting.getActualCulture();
                        double progRatio = culture.getProgression();

                        int stage = (progRatio >= 0.66) ? 3 : (progRatio >= 0.33) ? 2 : 1;
                        String cropName = culture.getName();
                        String spritePath = "/Sprite/images/Grow/" + cropName + "/" + cropName + " " + stage + ".png";

                        try {
                            java.io.InputStream is = getClass().getResourceAsStream(spritePath);
                            if (is != null) {
                                ImageView cropView = new ImageView(new Image(is));
                                cropView.setFitWidth(CELL - 40);
                                cropView.setFitHeight(CELL - 40);
                                cropView.setPreserveRatio(true);
                                visualCell.getChildren().add(cropView);
                            }
                        } catch (Exception ignored) {}

                        if (culture.isReady()) {
                            Label readyLabel = new Label("✅");
                            readyLabel.setStyle("-fx-font-size: 15px;");
                            StackPane.setAlignment(readyLabel, javafx.geometry.Pos.BOTTOM_CENTER);
                            visualCell.getChildren().add(readyLabel);
                            visualCell.setStyle("-fx-border-color: gold; -fx-border-width: 2;");
                        }
                    }
                    visualCell.setOnMouseClicked(event -> handleCellClick(plotting));
                }
                farmGrid.add(visualCell, j, i);
            }
        }
    }
    private Culture createCulture(String seedType) {
        return switch (seedType) {
            case "Wheat_Seed" -> new Wheat();
            case "Potato_Seed" -> new Potato();
            case "Carrot_Seed" -> new Carrot();
            case "Tomato_Seed" -> new Tomato();
            case "Strawberry_Seed" -> new Strawberry();
            case "Lemon_Seed" -> new Lemon();
            case "Corn_Seed" -> new Corn();
            case "Pineapple_Seed" -> new Pineapple();
            default -> null;
        };
    }
    @FXML
    private void handleselectSeed(ActionEvent event){
        Button btnSeed = (Button) event.getSource();

        this.selectedSeed = btnSeed.getId();
        labelStatus.setText("Seed Selected : " + selectedSeed);
    }

    private void handleCellClick(Plot plotting) {
        if(plotting.isLocked()){
            double cost = 500;
            if (farms.spending(cost)){
                plotting.setLocked(false);
                labelStatus.setText("Parcelle Achetée");
                updateUI();
            }else {
                labelStatus.setText("Pas assez d'argent ( " + cost + " $ requis)");
            }
        }
        if (plotting.isEmpty()) {
            if (farms.getInventory().getQuantity(selectedSeed) > 0) {
                Culture toplant = createCulture(selectedSeed);
                if (toplant != null){
                    farms.getInventory().add(selectedSeed, -1);
                    refreshInventoryUI();
                    plotting.planting(toplant);
                    labelStatus.setText(toplant.getName() + " planted !");
                }
            } else {
                labelStatus.setText("Not enough " + selectedSeed + " in stock !");
            }
        } else if (plotting.getActualCulture().isReady()) {
            String cropName = plotting.getActualCulture().getName() + "_Crop";
            farms.getInventory().add(cropName, 1);
            farms.addXP(50);
            refreshInventoryUI();
            plotting.collect();
            labelStatus.setText("Collected : " + cropName);
        }
        refreshGrid();
    }

    private void handlePurchasePlot(Plot plotting){
        double cost = farms.getNextPlotCost();

        if (farms.spending(cost)){
            plotting.setLocked(false);
            farms.incrementUnlockedPlots();
            labelStatus.setText("Nouvelle Terre achetée !");

            updateUI();
        } else {
            labelStatus.setText("Il vous manque " + (int)(cost - farms.getMoney()) + "$ !");
        }
    }

    @FXML
    private void onOpenStore() {
        SoundManager.playSfx(AudioPaths.SFX_OPEN);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/StoreView.fxml"));
            Parent root = loader.load();

            StoreController storeCtrl = loader.getController();
            storeCtrl.setFarms(this.farms);


            storeCtrl.setOnPurchaseCallback(() -> {
                updateUI();
                refreshInventoryUI();
            });

            Stage storeStage = new Stage();
            storeStage.setTitle(I18n.tr("store.title"));

            storeStage.initOwner(farmGrid.getScene().getWindow());
            storeStage.initModality(javafx.stage.Modality.WINDOW_MODAL);

            storeStage.setScene(new Scene(root));
            storeStage.setWidth(1120);
            storeStage.setHeight(780);
            storeStage.setMinWidth(980);
            storeStage.setMinHeight(680);
            storeStage.centerOnScreen();
            storeStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onOpenQuestBoard() {
        SoundManager.playSfx(AudioPaths.SFX_OPEN);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/QuestView.fxml"));
            Parent root = loader.load();

            QuestController questCtrl = loader.getController();


            questCtrl.init(this.farms, () -> {
                updateUI();
                refreshInventoryUI();
            });

            Stage questStage = new Stage();
            questStage.setTitle(I18n.tr("quest.title"));

            questStage.initOwner(farmGrid.getScene().getWindow());
            questStage.initModality(javafx.stage.Modality.WINDOW_MODAL);

            questStage.setResizable(false);

            questStage.setScene(new Scene(root));

            questStage.setOnCloseRequest(e -> questCtrl.close());

            questStage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de QuestView.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    private void onActionHarvest() {
        SoundManager.playSfx(AudioPaths.SFX_HARVEST);
        int collected = 0;
        for (int i = 0; i < farms.getNbLINES(); i++) {
            for (int j = 0; j < farms.getNbCOLMUNS(); j++) {
                Plot plot = farms.getField()[i][j];
                if (!plot.isEmpty() && plot.getActualCulture().isReady()) {
                    String cropName = plot.getActualCulture().getName() + "_Crop";
                    farms.getInventory().add(cropName, 1);
                    farms.addXP(50);
                    plot.collect();
                    collected++;
                }
            }
        }

        refreshInventoryUI();
        refreshGrid();
        if (collected > 0) {
            labelStatus.setText("Récolte terminée : " + collected + " culture(s) collectée(s).");
            logEvent("Recolte globale: " + collected + " culture(s).");
        } else {
            labelStatus.setText("Aucune culture prête à récolter.");
        }
    }

    public void init(Farms farms) {
        this.farms = farms;

        if (this.gameTimer != null) {
            this.gameTimer.stop();
        }
        this.gameTimer = new GameTimer(this.farms, this::updateUI);
        this.gameTimer.start();
        startAutosave();
        lastDisplayedLevel = farms.getLevel();
        logEvent("Partie chargee (slot " + farms.getCurrentSaveSlot() + ").");

        javafx.application.Platform.runLater(() -> {
            if (farmGrid.getScene() != null) {
                farmGrid.getScene().getAccelerators().put(
                        new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN),
                        this::onOpenAdmin
                );
            }
        });

        updateUI();
        refreshGrid();
        refreshInventoryUI();
    }

    @FXML
    private void onSaveClicked() {
        SaveSystem.saves(this.farms, this.farms.getCurrentSaveSlot());
        System.out.println("Sauvegarde effectuée sur le slot " + farms.getCurrentSaveSlot());
        logEvent("Sauvegarde manuelle effectuee.");
    }

    @FXML
    private void onOpenInventory() {
        if (inventoryStage != null && inventoryStage.isShowing()) {
            inventoryStage.toFront();
            return;
        }
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/InventoryView.fxml"));
            Parent root = loader.load();

            currentInventoryCtrl = loader.getController();
            currentInventoryCtrl.update(this.farms);

            inventoryStage = new Stage();
            inventoryStage.setTitle(I18n.tr("inventory.title"));
            inventoryStage.initOwner(farmGrid.getScene().getWindow());
            inventoryStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            inventoryStage.setScene(new Scene(root));

            inventoryStage.setOnCloseRequest(e -> {
                currentInventoryCtrl = null;
                inventoryStage = null;
            });

            inventoryStage.show();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void refreshInventoryUI(){
        if(currentInventoryCtrl != null){
            currentInventoryCtrl.update(this.farms);
        }
    }

    @FXML
    private void onSave() {
        SaveSystem.saves(this.farms, this.farms.getCurrentSaveSlot());
        labelStatus.setText("Game Saved !");
        logEvent("Sauvegarde manuelle effectuee.");
        SoundManager.playSfx(AudioPaths.SFX_SAVE);
    }

    public Farms getFarms() {
        return this.farms;
    }

    public void setFarms(Farms farms) {
        this.farms = farms;

        if (this.gameTimer != null) {
            this.gameTimer.stop();
        }
        this.gameTimer = new GameTimer(this.farms, this::updateUI);
        this.gameTimer.start();
        startAutosave();
        lastDisplayedLevel = farms.getLevel();

        refreshGrid();
        updateUI();
    }


    @FXML
    private void onOpenAdmin() {
        SoundManager.playSfx(AudioPaths.SFX_OPEN);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/AdminView.fxml"));
            Parent root = loader.load();
            FarmController.AdminController ctrl = loader.getController();
            ctrl.setFarms(this.farms);
            ctrl.setOnCloseCallback(this::updateUI);
            Stage stage = new Stage();
            stage.setTitle(I18n.tr("admin.title"));
            stage.initOwner(farmGrid.getScene().getWindow());
            stage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToBarn() throws IOException{
        if (farms.getLevel() < requiredLevel) {
            System.out.println("Niveau " + requiredLevel + " requis pour cet animal !");
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/BarnView.fxml"));
        Parent root = loader.load();
        FarmController.BarnController barnctrl = loader.getController();
        barnctrl.setFarms(this.farms);

        Stage stage = (Stage) farmGrid.getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    private void startAutosave() {
        if (autosaveTimer != null) {
            autosaveTimer.stop();
        }
        if (!GameBalance.AUTOSAVE_ENABLED) {
            return;
        }
        autosaveTimer = new Timeline(new KeyFrame(Duration.seconds(GameSettings.getAutosaveIntervalSeconds()), event -> {
            if (this.farms != null) {
                SaveSystem.saves(this.farms, this.farms.getCurrentSaveSlot());
                logEvent("Autosave (slot " + this.farms.getCurrentSaveSlot() + ").");
            }
        }));
        autosaveTimer.setCycleCount(Timeline.INDEFINITE);
        autosaveTimer.play();
    }

    @FXML
    private void onOpenSettings() {
        SoundManager.playSfx(AudioPaths.SFX_OPEN);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/SettingsView.fxml"));
            Parent root = loader.load();
            SettingsController ctrl = loader.getController();
            ctrl.setOnApply(() -> {
                startAutosave();
                updateUI();
                SoundManager.updateVolume();
                logEvent("Parametres appliques.");
            });

            Stage settingsStage = new Stage();
            settingsStage.setTitle(I18n.tr("settings.title"));
            settingsStage.initOwner(farmGrid.getScene().getWindow());
            settingsStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            settingsStage.setScene(new Scene(root));
            settingsStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logEvent(String message) {
        if (eventLogList == null) return;
        eventLogItems.add(0, message);
        if (eventLogItems.size() > 30) {
            eventLogItems.remove(30, eventLogItems.size());
        }
    }

}
