package polymorphia;

import polymorphia.artifacts.Artifact;

public class FixedDie extends Artifact implements Die {
    private final int value;

    public FixedDie(int roll) {
        super("Fixed Die with roll of " + roll);
        this.value = roll;
    }

    @Override
    public int roll() {
        return value;
    }

    @Override
    public String toString() {
        return "FixedDie{" + value + '}';
    }

    @Override
    public boolean isDie() {
        return true;
    }

}
