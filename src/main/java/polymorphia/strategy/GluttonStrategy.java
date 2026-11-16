package polymorphia.strategy;

import polymorphia.artifacts.Food;
import polymorphia.artifacts.Potion;
import polymorphia.characters.Character;
import polymorphia.maze.IRoom;
import polymorphia.maze.Room;

import java.util.Optional;

public class GluttonStrategy extends Strategy {

    @Override
    public void doAction(Character myself, IRoom room) {
        Optional<Potion> potion = getArtifact(room, Potion.class);
        if (potion.isPresent()) {
            Character reincarnatableMe = characterFactory.newReincarnatableMeCharacter(myself, potion.get());
            myself.takeArtifact(room, potion.get());
            room.replace(myself, reincarnatableMe);
            return;
        }
        Optional<Food> food = getArtifact(room, Food.class);
        if (food.isPresent()) {
            myself.eat(room, food.get());
            return;
        }
        move(myself, room);
    }

    @Override
    public boolean isGlutton() {
        return true;
    }

}
