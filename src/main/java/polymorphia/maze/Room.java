package polymorphia.maze;

import org.slf4j.Logger;
import polymorphia.artifacts.Artifact;
import polymorphia.artifacts.ArtifactBag;
import polymorphia.characters.Character;

import java.util.*;
import java.util.stream.Collectors;

public class Room implements IRoom, IMutableRoom {
    static Logger logger = org.slf4j.LoggerFactory.getLogger(Room.class);

    private final String name;
    private final Set<Room> neighbors = new HashSet<>();
    private final Set<Character> characters = new HashSet<>();
    private final ArtifactBag artifactBag = new ArtifactBag();

    public Room(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private List<String> getContents() {
        List<String> characterStrings = new ArrayList<>(getLivingCharacters().stream()
                .map(Object::toString)
                .toList());

        for (Artifact artifact : artifactBag.allItems()) {
            characterStrings.add(artifact.toString());
        }

        return characterStrings;
    }

    public void addNeighbor(IMutableRoom neighbor) {
        // Make sure we are never a neighbor of ourselves
        if (this == neighbor) {
            logger.warn("A room cannot be its own neighbor: {}.", name);
            return;
        }
        if (neighbor instanceof Room neighboringRoom) {
            this.neighbors.add(neighboringRoom);
        }
    }

    public void addNeighbor(IMutableRoom neighbor, boolean bidirectional) {
        this.addNeighbor(neighbor);
        if (bidirectional) {
            neighbor.addNeighbor(this);
        }
    }

    public String toString() {
        String representation = "\t" + name + ":\n\t\t";
        representation += String.join("\n\t\t", getContents());
        representation += "\n";
        return representation;
    }

    public void addCharacter(Character character) {
        characters.add(character);
    }

    public void removeCharacter(Character character) {
        characters.remove(character);
    }

    @Override
    public void enter(Character character, IRoom previousRoom) {
        if (previousRoom instanceof IMutableRoom mutablePreviousRoom) {
            mutablePreviousRoom.removeCharacter(character);
            addCharacter(character);
        }
    }

    public boolean contains(Class<? extends Artifact> artifactClass) {
        return artifactBag.contains(artifactClass);
    }

    public void addArtifact(Artifact artifact) {
        artifactBag.add(artifact);
    }

    public Set<IRoom> getNeighbors() {
        // Shuffling this order each time for more randomness in the game
        List<Room> shuffled = new ArrayList<>(neighbors);
        Collections.shuffle(shuffled);
        return new LinkedHashSet<>(shuffled);
    }

    @Override
    public Set<Character> getLivingCharacters() {
        return characters.stream()
                .filter(Character::isAlive)
                .collect(Collectors.toUnmodifiableSet());
    }

    // TODO: Eventually move this method to a Command object
    public void replace(Character myself, Character newMe) {
        if (characters.contains(myself)) {
            characters.remove(myself);
            characters.add(newMe);
        }
    }

    public boolean containsCharacterWithName(String baseName) {
        return characters.stream()
                .anyMatch(character -> character.getBaseName().equals(baseName));
    }

    public Optional<Character> getCharacterWithName(String baseName) {
        return getLivingCharacters().stream()
                .filter(character -> character.getName().contains(baseName))
                .findFirst();
    }

    @Override
    public <T> Set<T> getItemsOf(Class<T> type) {
        return artifactBag.getItemsOf(type);
    }

    public Set<Artifact> getAllArtifacts() {
        return artifactBag.getAllArtifacts();
    }

    public void removeArtifact(Artifact artifact) {
        artifactBag.remove(artifact);
    }

}