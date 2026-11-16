package polymorphia.maze;

import polymorphia.artifacts.Artifact;
import polymorphia.characters.Character;

public interface IMutableRoom extends IRoom {
    void addArtifact(Artifact artifact);
    void removeArtifact(Artifact artifact);
    void addCharacter(Character character);
    void removeCharacter(Character character);
    void addNeighbor(IMutableRoom neighbor, boolean bidirectional);
    default void addNeighbor(IMutableRoom neighbor) {
        addNeighbor(neighbor, false);
    }
}
