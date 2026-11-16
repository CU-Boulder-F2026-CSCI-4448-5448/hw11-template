package polymorphia.maze;

import polymorphia.artifacts.Artifact;
import polymorphia.characters.Character;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface IRoom {
    String getName();
    Set<IRoom> getNeighbors();
    default int numberOfNeighbors() {
        return getNeighbors().size();
    }
    default boolean hasNeighbor(IRoom neighbor) {
        return getNeighbors().contains(neighbor);
    }
    Set<Character> getLivingCharacters();
    default Map<IRoom, Set<Character>> getLivingCharactersByRoom() {
        return Map.of(this, getLivingCharacters());
    }
    default boolean containsCharacter(Character character) {
        return getLivingCharacters().contains(character);
    }
    default boolean hasLivingCharacters() {
        return !getLivingCharacters().isEmpty();
    }
    default boolean hasLivingAdventurers() {
        return !getLivingAdventurers().isEmpty();
    }
    default boolean hasLivingCreatures() { return !getLivingCreatures().isEmpty(); }
    default Set<Character> getLivingAdventurers() {
        return getLivingCharacters().stream()
                .filter(Character::isAdventurer)
                .collect(Collectors.toUnmodifiableSet());
    }
    default Set<Character> getLivingCreatures() {
        return getLivingCharacters().stream()
                .filter(Character::isCreature)
                .collect(Collectors.toUnmodifiableSet());
    }

    Optional<Character> getCharacterWithName(String baseName);
    default boolean containsCharacterWithName(String baseName) {
        return getCharacterWithName(baseName).isPresent();
    }

    <T> Set<T> getItemsOf(Class<T> type);
    default Set<Artifact> getAllArtifacts() {
        return getItemsOf(Artifact.class);
    }
    default <T> boolean hasArtifact(Class<T> type) {
        return !getItemsOf(type).isEmpty();
    }
    default boolean hasArtifact(Artifact artifact) {
        return getItemsOf(Artifact.class).contains(artifact);
    }

    void enter(Character character, IRoom previousRoom);
    void replace(Character myself, Character newMe);
}
