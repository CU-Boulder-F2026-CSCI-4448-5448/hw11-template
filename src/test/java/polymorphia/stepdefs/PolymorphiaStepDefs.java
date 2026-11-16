package polymorphia.stepdefs;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ooad.audibleobserver.AudibleObserver;
import ooad.audibleobserver.AudiblePlayer;
import ooad.gameobserver.intf.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import polymorphia.ObservablePolymorphia;
import polymorphia.characters.Character;
import polymorphia.commands.ICommand;
import polymorphia.maze.IMutableRoom;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PolymorphiaStepDefs {
    private static final Logger logger = LoggerFactory.getLogger(PolymorphiaStepDefs.class);

    World world;

    public PolymorphiaStepDefs(World world) {
        this.world = world;
    }

    @When("the game is played in the {word} maze")
    public void theGameIsPlayedInTheCreatedMaze(String mazeName) {
        if (Objects.equals(mazeName, "created")) {
            mazeName = "default";
        }
        world.getGame(mazeName, false).play();
    }

    @When("the game is played in the {word} maze where the treasure must be found to win")
    public void theGameIsPlayedInTheCreatedMazeWithTreasureFound(String mazeName) {
        if (Objects.equals(mazeName, "created")) {
            mazeName = null;
        }
        world.getGame(mazeName, true).play();
    }

    @When("the game is played in the created maze with the following observers:")
    public void theGameIsPlayedWithTheFollowingObservers(List<String> observerTypes) {
        ObservablePolymorphia observableGame = world.getGame();
        for (String observerType : observerTypes) {
            switch (observerType) {
                case "Audible":
                    observableGame.attach(new AudibleObserver(new AudiblePlayer()),
                            List.of(EventType.CHARACTER_DIED, EventType.TREASURE_MAP_ACQUIRED, EventType.TREASURE_ACQUIRED));
                    break;
                case "None":
                    break;
                default:
                    throw new IllegalArgumentException("Unknown observer type: " + observerType);
            }
        }
        observableGame.play();
    }

    @Then("all the adventurers or all of the creatures have died")
    public void allTheAdventurersOrAllOfTheCreaturesHaveDied() {
        assertTrue(!world.getGame().hasLivingAdventurers() || !world.getGame().hasLivingCreatures());
    }

    @Then("the game is over")
    public void theGameIsOver() {
        assertTrue(world.getGame().isOver());
    }

    @When("{word} plays a turn")
    public void characterPlaysATurn(String characterName) {
        // Make sure the maze is built and game created before the character plays a turn
        world.getGame();
        Character character = world.getCharacter(characterName);
        IMutableRoom currentRoom = world.getRoomWithCharacter(character);
        logger.info("Character {} is doing their turn in {} room", character.getName(), currentRoom.getName());
        ICommand command = character.getAction(currentRoom);
        command.executeCommand();
    }

    @And("{word} has found the treasure or all creatures or all the adventurers have died")
    public void seekerHasFoundTheTreasureOrAllCreaturesDied(String characterName) {
        Character character = world.getCharacter(characterName);
        assertTrue(character.hasTreasure() ||
                !world.getGame().hasLivingCreatures() ||
                !world.getGame().hasLivingAdventurers());
    }

    @Then("a fight occurred")
    public void aFightOccurred() {
        assertTrue(world.getObserver().aFightOccurred());
    }

    @Then("a death occurred")
    public void aDeathOccurred() {
        assertTrue(world.getObserver().aDeathOccurred());
    }

    @Then("{word} picked up the Armor")
    public void characterPickedUpTheArmor(String characterName) {
        Optional<Character> armoredCharacter = world.getMaze().getCharacterWithName(characterName);
        assertTrue(armoredCharacter.isPresent());
        assertTrue(armoredCharacter.get().getName().toLowerCase().contains("armor"));
    }
}
