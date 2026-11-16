package polymorphia.artifacts;

public abstract class Artifact {
    private static final double DEFAULT_STRENGTH = 1.0;
    private static final double DEFAULT_MOVING_COST = 0.2;

    protected final String name;
    protected double strength;
    protected double movingCost;

    public Artifact(String name) {
        this(name, DEFAULT_STRENGTH);
    }

    public Artifact(String name, double strength) {
        this(name, strength, DEFAULT_MOVING_COST);
    }
    public Artifact(String name, double strength, double movingCost) {
        this.name = name;
        this.strength = strength;
        this.movingCost = movingCost;
    }

    public String getName() {
        return name;
    }

    public double getMovingCost() {
        return movingCost;
    }

    public double getStrength() {
        return strength;
    }

    @Override
    public String toString() {
        String formattedStrength = String.format("%.2f", getStrength());
        return name + "(" + formattedStrength + ")";
    }

    public boolean isTreasure() {
        return false;
    }

    public boolean isTreasureMap() {
        return false;
    }

    public boolean isDie() {
        return false;
    }
}
