package polymorphia.stepdefs;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import polymorphia.Die;
import polymorphia.FixedDie;
import polymorphia.RandomDie;
import polymorphia.characters.Character;
import polymorphia.characters.CharacterFactory;
import polymorphia.strategy.StrategyFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.round;
import static org.junit.jupiter.api.Assertions.*;

public class CharacterStepDefs {
    CharacterFactory characterFactory = new CharacterFactory(new StrategyFactory());
    World world;

    public CharacterStepDefs(World world) {
        this.world = world;
    }

    Character createCharacter(String characterType, String characterName) {
        Character character = null;
        switch (characterType) {
            case "knight":
                character = characterFactory.createKnight(characterName);
                break;
            case "glutton":
                character = characterFactory.createGlutton(characterName);
                break;
            case "coward":
                character = characterFactory.createCoward(characterName);
                break;
            case "creature":
                character = characterFactory.createCreature(characterName);
                break;
            case "demon":
                character = characterFactory.createDemon(characterName);
                break;
            case "seeker":
                character = characterFactory.createSeeker(characterName);
                break;
            case "guard":
                character = characterFactory.createGuard(characterName);
                break;
            default:
                System.out.println("Error -- shouldn't get here. Character type: " + characterType);
        }
        assert character != null;
        world.putCharacter(character);
        return character;
    }

    @Then("^(\\w+) has health (.+)$")
    public void hasHealth(String name, double expectedHealth) {
        Character character = world.getCharacter(name);
        System.out.println("Expected health: " + expectedHealth);
        assertEquals(expectedHealth, character.getHealth());
    }

    @Given("^a (knight|glutton|coward|creature|demon|seeker|guard) (\\w+) with health (\\d{1,2}\\.?\\d{0,2})$")
    public Character createCharacterWithHealth(String characterType, String characterName, double health) {
        Character character = createCharacter(characterType, characterName, health, 0);
        world.putCharacter(character);
        return character;
    }

    @Given("^a (knight|glutton|coward|creature|demon|seeker|guard) (\\w+) with health (\\d{1,2}\\.?\\d{0,2}) and a fixed die roll of (\\d{1,2})$")
    public Character createCharacter(String characterType, String characterName, double health, int rollValue) {
        Die die = new FixedDie(rollValue);
        if (rollValue < 1) {
            die = RandomDie.sixSided();
        }
        Character character = null;
        switch (characterType) {
            case "knight":
                character = characterFactory.createKnight(characterName, health, die);
                break;
            case "glutton":
                character = characterFactory.createGlutton(characterName, health, die);
                break;
            case "coward":
                character = characterFactory.createCoward(characterName, health, die);
                break;
            case "creature":
            case "demon":
                character = characterFactory.createCreature(characterName, health, die);
                break;
            case "seeker":
                character = characterFactory.createSeeker(characterName, health, die);
                break;
            case "guard":
                character = characterFactory.createGuard(characterName, health, die);
                break;
            default:
                System.out.println("Error -- shouldn't get here");
        }
        assert character != null;
        world.putCharacter(character);
        return character;
    }

    @And("^a (knight|glutton|coward|creature|demon|seeker|guard) named (^\\w+)$")
    public void aNamedCharacter(String characterType, String characterName) {
        createCharacter(characterType, characterName);
    }

    public List<Character> createCharacters(String characterType, int numberOfItems) {
        List<Character> charactersList = new ArrayList<>();
        for (int i = 0; i < numberOfItems; i++) {
            String characterName = characterType + (i + 1);
            Character character = createCharacter(characterType, characterName);
            charactersList.add(character);
        }
        return charactersList;
    }

    @And("{word} has gained {double} in health")
    public void characterHasGainedInHealth(String characterName, Double lostHealth) {
        Character character = world.getCharacter(characterName);
        assertEquals(world.getInitialCharacterHealth(characterName) + lostHealth, character.getHealth());
    }

    @And("{word} did not lose some health")
    public void characterDidNotLoseSomeHealth(String characterName) {
        Character character = world.getCharacter(characterName);
        assertEquals(world.getInitialCharacterHealth(characterName), character.getHealth());
    }

    @And("{word} lost some health")
    public void characterLostSomeHealth(String characterName) {
        Character character = world.getCharacter(characterName);
        assertTrue(character.getHealth() < world.getInitialCharacterHealth(characterName));
    }

    @And("^a (knight|glutton|coward|creature|demon) named (\\w+)$")
    public void createNamedCharacter(String characterType, String characterName) {
        createCharacter(characterType, characterName);
    }

    @And("{word} is still alive")
    public void characterIsStillAlive(String characterName) {
        Character character = world.getCharacter(characterName);
        assertTrue(character.isAlive());
    }

    @And("{word} has found the treasure")
    public void characterHasFoundTheTreasure(String characterName) {
        Character character = world.getCharacter(characterName);
        assertTrue(character.hasTreasure());
    }

    @Then("{word} is now dead")
    public void characterIsDead(String characterName) {
        Character character = world.getCharacter(characterName);
        assertFalse(character.isAlive());
    }

    @And("the Coward Threshold is {int}")
    public void theCowardThresholdIs(double threshold) {
        Character.COWARD_THRESHOLD = threshold;
    }

    @Then("{word} now has a random die")
    public void characterNowHasARandomDie(String characterName) {
        Character character = world.getCharacter(characterName);
        Map<Integer, Integer> rollCounts = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            int currentRoll = character.getRoll();
            rollCounts.put(currentRoll, rollCounts.getOrDefault(currentRoll, 0) + 1);
        }
        assert rollCounts.size() > 1 : "Die is fixed";
    }

    @And("{word} has been in a fight")
    public void characterHasBeenInAFight(String characterName) {
        assertTrue(world.getObserver().aCharacterHadAFight(characterName));
    }

    @And("{word} has lost the default fight cost in health plus the aging cost of {int} turns")
    public void characterHasLostTheDefaultFightCostInHealth(String characterName, int numTurns) {
        Character character = world.getCharacter(characterName);
        Double initialHealth = world.getInitialCharacterHealth(characterName);

        double expectedHealth = initialHealth - Character.HEALTH_LOST_IN_FIGHT_REGARDLESS_OF_OUTCOME - Character.HEALTH_LOST_IN_AGING_EACH_TURN * numTurns;
        // Round health because of floating point precision issues
        double actualRoundedHealth = round(character.getHealth() * 100)/100.0;
        assertEquals(expectedHealth, actualRoundedHealth);
    }

    @And("{word} lost the default-moving cost plus {double} in health")
    public void numberOfDefaultFightCostLosses(String characterName, double additionalCost) {
        Character character = world.getCharacter(characterName);
        Double initialHealth = world.getInitialCharacterHealth(characterName);
        double expectedHealth = initialHealth - Character.HEALTH_LOST_IN_MOVING_ROOMS - additionalCost;
        assertTrue(expectedHealth >= character.getHealth());
    }

    @And("{word} has lost {int} times the default fight cost in health")
    public void characterHasLostNTimesTheDefaultFightCostInHealth(String characterName, int numberOfDefaultFightCostLosses) {
        Character character = world.getCharacter(characterName);
        double initialHealth = world.getInitialCharacterHealth(characterName);
        double expectedHealth = initialHealth - Character.HEALTH_LOST_IN_FIGHT_REGARDLESS_OF_OUTCOME * numberOfDefaultFightCostLosses;
        assertTrue(expectedHealth >= character.getHealth());

    }
}
