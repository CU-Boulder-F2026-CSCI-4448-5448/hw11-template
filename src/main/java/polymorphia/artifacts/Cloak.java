package polymorphia.artifacts;

public class Cloak extends Artifact {
    public Cloak(String name, double strength) {
        super(name, strength, 0.0);
    }

    public void showWear() {
        strength--;
    }
}
