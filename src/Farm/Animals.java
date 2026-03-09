package Farm;

public abstract class Animals {
    private String species;
    private double buyPrice;
    private boolean isHungry;
    private int productionTime; // Temps nécessaire pour produire
    private int currentTime = 0; // Temps écoulé

    public Animals(String species, double buyPrice, int productionTime){
        this.species = species;
        this.buyPrice = buyPrice;
        this.isHungry = false;
        this.productionTime = productionTime;
    }

    public void update() {
        if (!isHungry) {
            currentTime++;
        }
    }

    public boolean isReadyToProduce() {
        return currentTime >= productionTime;
    }

    public void resetProduction() {
        this.currentTime = 0;
        this.isHungry = true;
    }

    public abstract String getProduct();

    public void feed() {
        this.isHungry = false;
    }

    public String getSpecies(){return species;}
    public double getBuyPrice() {return buyPrice;}
    public boolean isHungry() { return isHungry; }
}