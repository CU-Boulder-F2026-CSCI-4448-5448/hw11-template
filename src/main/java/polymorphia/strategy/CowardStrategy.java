package polymorphia.strategy;

import polymorphia.artifacts.Food;
import polymorphia.artifacts.TreasureMap;
import polymorphia.characters.Character;
import polymorphia.maze.IRoom;

import java.util.Optional;
import java.util.Set;

public class CowardStrategy extends Strategy {

    @Override
    public void doAction(Character myself, IRoom room) {
        if (!room.getLivingCreatures().isEmpty()) {
            move(myself, room);
            return;
        }
        Optional<TreasureMap> treasureMap = getArtifact(room, TreasureMap.class);
        if (treasureMap.isPresent()) {
            TreasureMap actualMap = treasureMap.get();
            myself.take(room, actualMap);
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
    public boolean isCoward() {
        return true;
    }

}
