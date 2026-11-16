package polymorphia.artifacts;

public class Weapon extends Artifact {

    public Weapon(String name, double strength) {
        super(name, strength);
    }

    public Weapon(String name, double strength, double movingCost) {
        super(name, strength, movingCost);
    }
}
