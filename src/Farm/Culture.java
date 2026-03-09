package Farm;

public abstract class Culture {
    private String name;
    private int growthTime;
    private int timeLeft;
    private double buyPrice;
    private double sellPrice;
    private String imagePath;
    protected double timeSec;

    public Culture(String name, int growthTime, double buyPrice, double sellPrice,String imagePath){
        this.name = name;
        this.growthTime = growthTime;
        this.timeLeft = 0;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.imagePath = imagePath;
    }

    public void growing(){
        if (timeLeft < growthTime){
            timeLeft ++;
        }
    }


    public double getTimeSec() {
        return this.timeSec;
    }

    public void setTimeSec(double time) {
        this.timeSec = time;
    }

    public boolean isReady(){
        return timeLeft >= growthTime;
    }

    public String getName() {return name;}
    public double getBuyPrice() {return buyPrice;}
    public double getSellPrice() {return sellPrice;}
    public String getImagePath() {return imagePath;}
    public double getProgression() {return (double) timeLeft / growthTime ;}
}
