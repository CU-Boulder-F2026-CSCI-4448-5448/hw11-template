package polymorphia.characters;

import polymorphia.artifacts.Artifact;

import static java.lang.Math.max;

public class ArmoredCharacter extends DecoratedCharacter {
    ArmoredCharacter(Character myself, Artifact armor) {
        super(myself, armor);
    }

    @Override
    public String getName() {
        return artifact.getName() + "-armored " + super.getName();
    }

    @Override
    protected void loseFightDamage(double fightDamage) {
        // Limit the loss to zero loss. The character can't gain health in losing a fight.
        double actualLoss = max(0.0, fightDamage - artifact.getStrength());
        myself.loseFightDamage(actualLoss);
    }

}
