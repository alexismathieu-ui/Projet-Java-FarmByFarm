package FarmController;

import Farm.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.Timeline;

public class QuestController {
    @FXML private VBox questContainer;
    @FXML private Label timerLabel;
    private Farms farms;
    private Runnable onUpdate;
    private Timeline liveTimer;

    private void refreshUI() {
        questContainer.getChildren().clear();

        long now = System.currentTimeMillis();
        if (now < farms.getNextQuestTime()) {
            timerLabel.setText("Prochaines quêtes dans : " + ((farms.getNextQuestTime() - now) / 1000) + "s");
            return;
        } else if (farms.getActiveQuests().isEmpty()) {
            farms.generalQuests();
        }

        for (Quest q : farms.getActiveQuests()) {
            HBox row = new HBox(10);
            row.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-padding: 10;");

            Label desc = new Label(q.toString());
            desc.setTextFill(javafx.scene.paint.Color.WHITE);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button btnValider = new Button("Livrer");
            btnValider.setOnAction(e -> deliverQuest(q));

            Button btnRefuser = new Button("X");
            btnRefuser.setOnAction(e -> removeQuest(q));

            row.getChildren().addAll(desc, spacer, btnValider, btnRefuser);
            questContainer.getChildren().add(row);
        }
    }

    private void deliverQuest(Quest q) {
        if (farms.getInventory().getQuantity(q.getTargetItem()) >= q.getAmountNeeded()) {
            farms.getInventory().add(q.getTargetItem(), -q.getAmountNeeded());
            farms.winMoney(q.getRewardMoney());
            farms.addXP(q.getRewardXP());
            removeQuest(q);
        } else {
            // Optionnel : Alerte "Pas assez de ressources"
        }
    }

    private void removeQuest(Quest q) {
        farms.getActiveQuests().remove(q);
        if (farms.getActiveQuests().isEmpty()) {
            farms.setNextQuestTime(System.currentTimeMillis() + (5 * 60 * 1000));
        }
        refreshUI();
        onUpdate.run();
    }

    public void init(Farms farms, Runnable onUpdate) {
        this.farms = farms;
        this.onUpdate = onUpdate;

        liveTimer = new javafx.animation.Timeline(new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), e -> {
            long now = System.currentTimeMillis();
            if (now < farms.getNextQuestTime()) {
                long diff = (farms.getNextQuestTime() - now) / 1000;
                timerLabel.setText("Prochaines quêtes dans : " + diff + "s");
            } else {
                timerLabel.setText("Nouvelles quêtes disponibles !");
                if (farms.getActiveQuests().isEmpty()) refreshUI();
            }
        }));
        liveTimer.setCycleCount(javafx.animation.Timeline.INDEFINITE);
        liveTimer.play();

        refreshUI();
    }

    @FXML protected void close() {
        if (liveTimer != null) liveTimer.stop();
        ((Stage)questContainer.getScene().getWindow()).close();
    }
}