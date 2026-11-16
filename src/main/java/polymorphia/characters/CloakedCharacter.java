package polymorphia.characters;

import polymorphia.artifacts.Cloak;

public class CloakedCharacter extends DecoratedCharacter {
    public CloakedCharacter(Character myself, Cloak cloak) {
        super(myself, cloak);
    }

    @Override
    public boolean isVisible() {
        if (artifact.getStrength() > 0) {
            logger.info("{} wasn't seen! Cloak has value of {}", getName(), artifact.getStrength());
            return false;
        }
        return true;
    }

    @Override
    public String getName() {
        if (artifact.getStrength() > 0) {
            return artifact.getName() + "-wearing " + super.getName();
        }
        return super.getName();
    }

    @Override
    public void turnTaken() {
        super.turnTaken();
        ((Cloak) artifact).showWear();
    }
}
