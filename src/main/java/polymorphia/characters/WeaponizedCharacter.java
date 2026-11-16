package polymorphia.characters;

import polymorphia.artifacts.Artifact;

import static java.lang.Math.floor;

public class WeaponizedCharacter extends DecoratedCharacter {

    public WeaponizedCharacter(Character myself, Artifact weapon) {
        super(myself, weapon);
    }

    @Override
    public int getRoll() {
        String msg = String.format("%s wields %s and increases roll by %d",
                super.getName(), artifact.getName(), (int)floor(artifact.getStrength()));
        logger.info(msg);
        return (int) floor(myself.getRoll() + artifact.getStrength());
    }

    @Override
    public String getName() {
        return artifact.getName() + "-wielding " + myself.getName();
    }
}
