package polymorphia.strategy;

import polymorphia.characters.Character;
import polymorphia.maze.IRoom;

import java.util.Optional;

public class CreatureStrategy extends Strategy {
    // Creatures fight adventurers or other creatures

    @Override
    public void doAction(Character myself, IRoom currentRoom) {
        Optional<Character> character = getRandomCharacterNotMe(myself, currentRoom);
        if (character.isPresent()) {
            myself.fight(character.get());
        } else {
            move(myself, currentRoom);
        }
    }
}
