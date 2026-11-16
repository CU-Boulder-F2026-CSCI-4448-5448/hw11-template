package polymorphia.strategy;

import polymorphia.characters.Character;
import polymorphia.maze.IRoom;
import polymorphia.maze.Room;

public class SleepStrategy extends Strategy {

    @Override
    public void doAction(Character myself, IRoom room) {
        // Sleep strategy does nothing, the character sleeps;
    }
}
