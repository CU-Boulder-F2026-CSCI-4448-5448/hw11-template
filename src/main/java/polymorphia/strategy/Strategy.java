package polymorphia.strategy;

import polymorphia.characters.Character;
import polymorphia.characters.CharacterFactory;
import polymorphia.maze.IRoom;

import java.util.Optional;
import java.util.Set;

import static polymorphia.strategy.SeekerStrategy.logger;

public abstract class Strategy implements IStrategy {
    protected static final CharacterFactory characterFactory = new CharacterFactory(new StrategyFactory());

    Optional<Character> getLivingCreature(IRoom room) {
        return room.getLivingCreatures().stream()
                .filter(Character::isCreature)
                .findFirst();
    }

    public Optional<Character> getRandomCharacterNotMe(Character myself, IRoom room) {
        return room.getLivingCharacters().stream()
                .filter(character -> !character.equals(myself))
                .findFirst();
    }

    <T> Set<T> getArtifacts(IRoom room, Class<T> type) {
        return room.getItemsOf(type);
    };

    <T> Optional<T> getArtifact(IRoom room, Class<T> type) {
        return getArtifacts(room, type).stream()
                .findAny();
    };

    <T> boolean hasArtifacts(IRoom room, Class<T> type) {
        return !room.getItemsOf(type).isEmpty();
    }

    Optional<IRoom> getRandomNeighbor(IRoom room) {
        return room.getNeighbors().stream()
                .findAny();
    }

    void move(Character myself, IRoom room) {
        Optional<IRoom> randomRoom = getRandomNeighbor(room);
        if (randomRoom.isPresent()) {
            myself.move(randomRoom.get(), room);
            return;
        }
        logger.warn("No neighbors to move to from {}", room);
    }

}
