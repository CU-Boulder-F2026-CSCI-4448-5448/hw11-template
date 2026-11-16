package polymorphia;

import org.slf4j.Logger;
import polymorphia.characters.Character;
import polymorphia.maze.CharacterLocationPair;
import polymorphia.maze.IRoom;
import polymorphia.maze.Maze;

import java.util.Set;

public class Polymorphia {
    static Logger logger = org.slf4j.LoggerFactory.getLogger(Polymorphia.class);

    Maze maze;
    Integer turnCount = 0;
    boolean mustFindTreasureToWin = false;

    public Polymorphia(Maze maze) {
        if (!maze.hasLivingAdventurers()) {
            String msg = "No adventurers created. Terminating game.";
            logger.error(msg);
            throw new IllegalStateException(msg);
        }
        this.maze = maze;
    }

    public void mustFindTreasureToWin() {
        this.mustFindTreasureToWin = true;
    }

    public String toString() {
        return "Polymorphia MAZE: turn " + turnCount + "\n" + maze.toString();
    }

    public Boolean isOver() {
        if (mustFindTreasureToWin) {
            return treasureFound();
        }
        return !hasLivingCreatures() || !hasLivingAdventurers() || treasureFound();
    }

    private boolean treasureFound() {
        return getLivingCharacters().stream().anyMatch(Character::hasTreasure);
    }

    public Boolean hasLivingCreatures() {
        return maze.hasLivingCreatures();
    }

    public Boolean hasLivingAdventurers() {
        return maze.hasLivingAdventurers();
    }

    protected void playTurn() {
        for (CharacterLocationPair pair : maze) {
            Character character = pair.character();
            IRoom room = pair.room();
            character.doAction(room);
        }
    }

    private Set<Character> getLivingCharacters() {
        return maze.getLivingCharacters();
    }

    public void play() {
        logger.info("Starting play...\n");
        while (!isOver()) {
            logger.info(this.toString());
            playTurn();
            turnCount++;
        }
        logger.info("The game ended after {} turns.\n", turnCount);
        String results = getFinalResults();
        logger.info(results);
    }

    private String getFinalResults() {
        String eventDescription;
        if (hasLivingAdventurers()) {
            eventDescription = "The adventurers won! Left standing are: " + getAdventurerNames() + "\n";
            if (treasureFound()) {
                eventDescription += "The adventurers found the treasure!\n";
            }
        } else if (hasLivingCreatures()) {
            eventDescription = "The creatures won. Boo! Left standing are: " + getCreatureNames() + "\n";
        } else {
            eventDescription = "No team won! Everyone died!\n";
        }
        return eventDescription;
    }

    private String getAdventurerNames() {
        return String.join(", ", getLivingCharacters().stream().map(Object::toString).toList());
    }

    private String getCreatureNames() {
        return String.join(", ", getLivingCreatures().stream().map(Object::toString).toList());
    }

    private Set<Character> getLivingCreatures() {
        return maze.getLivingCreatures();
    }

    public Set<Character> getWinners() {
        if (!isOver() || !hasLivingCharacters()) {
            // No one has won yet, or no one won -- all died
            return Set.of();
        }
        return getLivingCharacters();
    }

    private boolean hasLivingCharacters() {
        return maze.hasLivingCharacters();
    }

    protected int getTurnCount() {
        return turnCount;
    }
}
