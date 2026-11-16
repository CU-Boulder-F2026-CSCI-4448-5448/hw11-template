package polymorphia.strategy;

import polymorphia.artifacts.Treasure;
import polymorphia.characters.Character;
import polymorphia.maze.IRoom;

import java.util.Optional;

public class GuardStrategy extends Strategy {

    @Override
    public void doAction(Character myself, IRoom currentRoom) {
        if (hasArtifacts(currentRoom, Treasure.class)) {
            return;     // stand guard and do nothing if there is treasure in the room
        }
        // Else, behave like a normal creature
        Optional<Character> character = getRandomCharacterNotMe(myself, currentRoom);
        if (character.isPresent()) {
            myself.fight(character.get());
        } else {
            move(myself, currentRoom);
        }
    }

}
