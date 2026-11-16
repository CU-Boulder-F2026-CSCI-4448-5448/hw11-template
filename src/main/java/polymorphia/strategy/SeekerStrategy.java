package polymorphia.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import polymorphia.artifacts.Treasure;
import polymorphia.characters.Character;
import polymorphia.maze.IRoom;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class SeekerStrategy extends Strategy {
    static Logger logger = LoggerFactory.getLogger(SeekerStrategy.class);

    private final Set<IRoom> visitedRooms = new HashSet<>();

    @Override
    public void doAction(Character myself, IRoom room) {
        visitedRooms.add(room);

        logger.info("{} looking for treasure in room {}", myself.getName(), room.getName());
        if (!hasArtifacts(room, Treasure.class)) {
            logger.info("{} found no treasure in room {}. Moving", myself.getName(), room.getName());
            Optional<IRoom> nextRoom = selectNewRoom(room);
            if (nextRoom.isPresent()) {
                myself.move(nextRoom.get(), room);
            } else {
                logger.warn("No neighbors to move to from {}", room);
            }
            return;
        }

        Optional<Character> possibleGuard = getLivingCreature(room);
        if (possibleGuard.isPresent()) {
            logger.info("{} found guard {} in treasure room {}", myself.getName(), possibleGuard.get(), room.getName());
            myself.fight(possibleGuard.get());
            return;
        }

        Optional<Treasure> treasure = getArtifact(room, Treasure.class);
        if (treasure.isEmpty()) {
            logger.info("{} had the treasure snatched (on another thread) before they could take it in room {}", myself.getName(), room.getName());
            return;
        }

        myself.take(room, treasure.get());
    }

    private Optional<IRoom> selectNewRoom(IRoom room) {
        for (IRoom candidate : room.getNeighbors()) {
            if (!visitedRooms.contains(candidate)) {
                return Optional.of(candidate);
            }
        }
        return getRandomNeighbor(room);
    }
}
