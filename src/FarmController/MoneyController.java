package FarmController;

import Farm.Farms;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MoneyController {
    @FXML
    private Label totalMoneyLabel;

    public void updateData(Farms farms) {
        totalMoneyLabel.setText("Money " + farms.getMoney() + " $");
    }
}
