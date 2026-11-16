package polymorphia.artifacts;

public class Armor extends Artifact {

    public Armor(String name, double strength) {
        super(name, strength);
    }

    public Armor(String name, double strength, double movingCost) {
        super(name, strength, movingCost);
    }
}
