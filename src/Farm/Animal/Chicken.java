package Farm.Animal;

import Farm.Animals;

public class Chicken extends Animals {
    public Chicken() {
        super("Chicken", 5000.0, 10);
    }

    @Override
    public String getProduct() {
        return "Egg";
    }
}