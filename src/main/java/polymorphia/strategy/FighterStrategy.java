package polymorphia.strategy;

import polymorphia.Die;
import polymorphia.artifacts.Armor;
import polymorphia.artifacts.Food;
import polymorphia.artifacts.Weapon;
import polymorphia.characters.Character;
import polymorphia.maze.IRoom;
import polymorphia.maze.Room;

import java.util.Optional;

public class FighterStrategy extends Strategy {

    @Override
    public void doAction(Character myself, IRoom room) {
        Optional<Armor> armor = getArtifact(room, Armor.class);
        if (armor.isPresent()) {
            Character armoredMe = characterFactory.newArmoredCharacter(myself, armor.get());
            myself.takeArtifact(room, armor.get());
            room.replace(myself, armoredMe);
            return;
        }
        Optional<Weapon> weapon = getArtifact(room, Weapon.class);
        if (weapon.isPresent()) {
            Character armoredMe = characterFactory.newWeaponizedCharacter(myself, weapon.get());
            myself.takeArtifact(room, weapon.get());
            room.replace(myself, armoredMe);
            return;
        }
        Optional<Character> creature = getLivingCreature(room);
        if (creature.isPresent()) {
            myself.fight(creature.get());
            return;
        }
        Optional<Die> die = getArtifact(room, Die.class);
        if (die.isPresent()) {
            myself.pickUpDie(room, die.get());
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
    public boolean isFighter() {
        return true;
    }
}
