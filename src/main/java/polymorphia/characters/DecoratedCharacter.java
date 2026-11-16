package polymorphia.characters;

import polymorphia.Die;
import polymorphia.artifacts.Artifact;
import polymorphia.strategy.IStrategy;

public abstract class DecoratedCharacter extends Character {
    Character myself;
    Artifact artifact;

    public DecoratedCharacter(Character self, Artifact artifact) {
        myself = self;
        this.artifact = artifact;
    }

    @Override
    public boolean isVisible() { return myself.isVisible(); };

    @Override
    public String getName() {
        return myself.getName();
    }

    @Override
    protected Die getDie() {
        return myself.getDie();
    }

    @Override
    public double getHealth() {
        return myself.getHealth();
    }

    @Override
    public String getBaseName() {
        return myself.getBaseName();
    }

    @Override
    public void setDie(Die die) {
        myself.setDie(die);
    }

    @Override
    protected IStrategy getStrategy() {
        return myself.getStrategy();
    }

    @Override
    public void setStrategy(IStrategy strategy) {
        myself.setStrategy(strategy);
    }

    @Override
    public Boolean isAdventurer() {
        return myself.isAdventurer();
    }

    @Override
    protected double getMovingCost() {
        return artifact.getMovingCost() + myself.getMovingCost();
    }

    @Override
    protected void loseHealth(double healthToLose) {
        myself.loseHealth(healthToLose);
    }

    @Override
    protected void gainHealth(double healthPoints) {
        myself.gainHealth(healthPoints);
    }
}