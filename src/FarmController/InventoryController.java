package FarmController;

import Farm.Farms;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class InventoryController {

    @FXML private Label wheatSeedLabel;
    @FXML private Label wheatCropLabel;

    @FXML private GridPane seedGrid;
    @FXML private GridPane cropGrid;
    @FXML private GridPane animalGrid;
    @FXML private Label    totalItemsLabel;
    @FXML private Label    invStatusLabel;

    private static final String[] CROPS = {"Wheat","Carrot","Potato","Tomato","Lemon","Strawberry","Corn","Pineapple"};
    private static final String[] ANIMALS_PROD = {"Egg","Milk","Wool","Truff"};

    private static final java.util.Map<String,String> EMOJI = new java.util.HashMap<>();
    static {
        EMOJI.put("Wheat","🌾"); EMOJI.put("Carrot","🥕"); EMOJI.put("Potato","🥔");
        EMOJI.put("Tomato","🍅"); EMOJI.put("Lemon","🍋"); EMOJI.put("Strawberry","🍓");
        EMOJI.put("Corn","🌽"); EMOJI.put("Pineapple","🍍");
        EMOJI.put("Egg","🥚"); EMOJI.put("Milk","🥛"); EMOJI.put("Wool","🧶"); EMOJI.put("Truff","🍄");
    }
    private static final java.util.Map<String,String> FR = new java.util.HashMap<>();
    static {
        FR.put("Wheat","Blé"); FR.put("Carrot","Carotte"); FR.put("Potato","Patate");
        FR.put("Tomato","Tomate"); FR.put("Lemon","Citron"); FR.put("Strawberry","Fraise");
        FR.put("Corn","Maïs"); FR.put("Pineapple","Ananas");
        FR.put("Egg","Œuf"); FR.put("Milk","Lait"); FR.put("Wool","Laine"); FR.put("Truff","Truffe");
    }

    public void update(Farms farms) {
        if (seedGrid == null) return;

        seedGrid.getChildren().clear();
        cropGrid.getChildren().clear();
        animalGrid.getChildren().clear();

        int total = 0;
        int col;

        col = 0;
        for (String name : CROPS) {
            int qty = farms.getInventory().getQuantity(name + "_Seed");
            total += qty;
            VBox card = makeCard(EMOJI.getOrDefault(name,"🌱"), FR.getOrDefault(name,name), qty, "seed", name + " Graine");
            seedGrid.add(card, col % 4, col / 4);
            col++;
        }

        col = 0;
        for (String name : CROPS) {
            int qty = farms.getInventory().getQuantity(name + "_Crop");
            total += qty;
            VBox card = makeCard(EMOJI.getOrDefault(name,"🌾"), FR.getOrDefault(name,name), qty, "crop", name + " Récolte");
            cropGrid.add(card, col % 4, col / 4);
            col++;
        }

        col = 0;
        for (String name : ANIMALS_PROD) {
            int qty = farms.getInventory().getQuantity(name + "_Crop");
            total += qty;
            VBox card = makeCard(EMOJI.getOrDefault(name,"📦"), FR.getOrDefault(name,name), qty, "animal", name);
            animalGrid.add(card, col % 4, col / 4);
            col++;
        }

        if (totalItemsLabel != null) totalItemsLabel.setText(total + " objets");
    }

    private VBox makeCard(String emoji, String name, int qty, String type, String fullName) {
        VBox card = new VBox(4);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("inv-card");
        if (qty == 0) card.getStyleClass().add("inv-card-empty");
        else if (type.equals("seed")) card.getStyleClass().add("inv-card-seed");
        else if (type.equals("crop")) card.getStyleClass().add("inv-card-crop");
        else card.getStyleClass().add("inv-card-animal");

        Label emojiLbl = new Label(emoji);
        emojiLbl.getStyleClass().add("inv-emoji");

        Label nameLbl = new Label(name);
        nameLbl.getStyleClass().add("inv-item-name");

        Label qtyLbl = new Label("×" + qty);
        qtyLbl.getStyleClass().add(qty > 0 ? "inv-qty" : "inv-qty-zero");

        card.getChildren().addAll(emojiLbl, nameLbl, qtyLbl);

        card.setOnMouseClicked(e -> {
            if (invStatusLabel != null)
                invStatusLabel.setText(qty > 0 ? "📦 " + fullName + " : " + qty + " en stock" : "🚫 " + fullName + " : aucun en stock");
        });

        return card;
    }

    @FXML
    private void closeInventory(){
        // wheatSeedLabel always present (compat), use it or adminRoot
        try {
            Stage stage;
            if (wheatSeedLabel != null && wheatSeedLabel.getScene() != null)
                stage = (Stage) wheatSeedLabel.getScene().getWindow();
            else if (seedGrid != null)
                stage = (Stage) seedGrid.getScene().getWindow();
            else return;
            stage.close();
        } catch (Exception ignored) {}
    }
}
