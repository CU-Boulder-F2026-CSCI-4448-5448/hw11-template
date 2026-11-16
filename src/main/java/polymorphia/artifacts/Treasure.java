package polymorphia.artifacts;

public class Treasure extends Artifact {

    public Treasure(String name) {
        super(name);
    }

    @Override
    public boolean isTreasure() {
        return true;
    }
}
