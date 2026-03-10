package Farm;

public class Quest {
    private String targetItem;
    private int amountNeeded;
    private double rewardMoney;
    private int rewardXP;
    private boolean completed = false;

    public Quest(String targetItem, int amountNeeded, double rewardMoney, int rewardXP){
        this.targetItem = targetItem;
        this.rewardMoney = rewardMoney;
        this.rewardXP = rewardXP;
        this.amountNeeded = amountNeeded;
    }

    public String getTargetItem() { return targetItem; }
    public int getAmountNeeded() { return amountNeeded; }
    public double getRewardMoney() { return rewardMoney; }
    public int getRewardXP() { return rewardXP; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    @Override
    public String toString() {
        return "Besoin de : " + amountNeeded + " " + targetItem.replace("_Crop", "") +
                "\nRécompense : " + (int)rewardMoney + "$ | " + rewardXP + " XP";
    }
}
