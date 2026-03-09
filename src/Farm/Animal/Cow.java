package Farm.Animal;

import Farm.Animals;

public class Cow extends Animals {
    public Cow() {
        super("Cow", 50000.0, 15);
    }

    @Override
    public String getProduct() {
        return "Milk";
    }
}