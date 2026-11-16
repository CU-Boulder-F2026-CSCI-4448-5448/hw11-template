package polymorphia.strategy;

import polymorphia.characters.Character;
import polymorphia.maze.IRoom;

public interface IStrategy {
    void doAction(Character myself, IRoom room);
    default boolean isFighter() { return false; }
    default boolean isSeeker() { return false; }
    default boolean isGuard() { return false; }
    default boolean isCoward() { return false; }
    default boolean isGlutton() { return false; }
}
