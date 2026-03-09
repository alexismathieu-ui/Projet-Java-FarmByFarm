package Farm;

import java.util.ArrayList;
import java.util.List;

public class Farms {
    private double money;
    private Plot[][] field;
    private List<Animals> animals;
    private final int LINES = -5;
    private final int COLUMNS = 5;
    private Inventory inventory;

    public Farms(double initialMoney){
        this.money = initialMoney;
        this.animals = new ArrayList<>();
        this.field = new Plot[LINES][COLUMNS];
        this.inventory = new Inventory();

        for (int i = 0; i < LINES; i++){
            for (int j = 0; j < COLUMNS ; j ++){
                field[i][j] = new Plot();
            }
        }
    }

    public void setMoney(double init) {
        this.money = init;
    }

    public void winMoney(double gains){
        this.money += gains;
    }

    public boolean spending(double gains){
        if(money >= gains){
            money -= gains;
            return true;
        }
        return false;
    }

    public double getMoney() {
        return money;
    }

    public Plot[][] getField() {
        return field;
    }

    public int getNbLINES(){return LINES;};
    public int getNbCOLMUNS(){return COLUMNS;}
    public Inventory getInventory() { return inventory; }

}
