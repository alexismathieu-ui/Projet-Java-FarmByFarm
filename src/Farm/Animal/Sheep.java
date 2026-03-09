package Farm.Animal;

import Farm.Animals;

public class Sheep extends Animals {
    public Sheep() {
        super("Sheep", 10000.0, 10);
    }

    @Override
    public String getProduct() {
        return "Wool";
    }
}