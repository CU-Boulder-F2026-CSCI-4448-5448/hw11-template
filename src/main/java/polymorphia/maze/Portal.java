package polymorphia.maze;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Portal extends Room implements IRoom {
    private static final Random random = new Random();

    public Portal(String name) {
        super(name);
    }

    @Override
    public Set<IRoom> getNeighbors() {
        Set<IRoom> allNeighbors = super.getNeighbors();
        Set<IRoom> currentNeighbors = new HashSet<>();
        for (IRoom room : allNeighbors) {
            if (random.nextBoolean()) {
                currentNeighbors.add(room);
            }
        }
        logger.info("Showing {} neighbors out of a total of {} in {} room", currentNeighbors.size(), allNeighbors.size(), getName());
        return currentNeighbors;
    }
}
