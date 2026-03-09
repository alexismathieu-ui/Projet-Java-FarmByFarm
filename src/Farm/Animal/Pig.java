package Farm.Animal;

import Farm.Animals;

public class Pig extends Animals {
    public Pig() {
        super("Pig", 100000.0, 25);
    }

    @Override
    public String getProduct() {
        return "Truff";
    }
}