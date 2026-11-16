package polymorphia.artifacts;

public class TreasureMap extends Artifact {
    public TreasureMap(String name) {
        super(name);
    }

    @Override
    public boolean isTreasureMap() {
        return true;
    }

    @Override
    public String toString() {
        return getName() + " treasure map";
    }
}
