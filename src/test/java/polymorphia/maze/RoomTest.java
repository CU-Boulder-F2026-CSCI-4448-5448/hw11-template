package polymorphia.maze;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import polymorphia.characters.Character;
import polymorphia.characters.CharacterFactory;
import polymorphia.commands.ICommand;
import polymorphia.strategy.StrategyFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static polymorphia.characters.Character.HEALTH_LOST_IN_AGING_EACH_TURN;
import static polymorphia.characters.Character.HEALTH_LOST_IN_MOVING_ROOMS;
import static polymorphia.characters.CharacterFactory.DEFAULT_KNIGHT_HEALTH;

public class RoomTest {
    static Logger logger = org.slf4j.LoggerFactory.getLogger(RoomTest.class);

    CharacterFactory characterFactory = new CharacterFactory(new StrategyFactory());

    IRoom getRandomNeighbor(Room room) {
        return room.getNeighbors().stream()
                .findAny()
                .orElse(null);
    }

    @Test
    void getRandomNeighbor() {
        Room room = new Room("onlyRoom");
        Room neighbor = new Room("neighbor");
        room.addNeighbor(neighbor);

        assertEquals(getRandomNeighbor(room), neighbor);
    }

    @Test
    void testToString() {
        Room room = new Room("onlyRoom");
        room.addCharacter(characterFactory.createKnight("Bilbo"));
        room.addCharacter(characterFactory.createCreature("Ogre"));

        assertTrue(room.toString().contains("onlyRoom"));
        assertTrue(room.toString().contains("Bilbo"));
        assertTrue(room.toString().contains("Ogre"));
    }

    @Test
    void testMovingRoomLossOfQuarterPoint() {
        Character joe = characterFactory.createKnight("Joe");
        Room currentRoom = new Room("currentRoom");
        Room newRoom = new Room("newRoom");
        currentRoom.addNeighbor(newRoom);
        currentRoom.addCharacter(joe);

        // Since nothing is in the current room with Joe, he should move to the new room
        ICommand command = joe.getAction(currentRoom);
        command.executeCommand();

        logger.info("After moving rooms, Joe's health is: " + joe.getHealth());
        assertEquals(DEFAULT_KNIGHT_HEALTH - HEALTH_LOST_IN_MOVING_ROOMS - HEALTH_LOST_IN_AGING_EACH_TURN, joe.getHealth());
        assertTrue(newRoom.containsCharacter(joe));
    }
}
