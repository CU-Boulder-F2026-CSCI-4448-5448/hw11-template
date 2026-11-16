package polymorphia.characters;

import org.junit.jupiter.api.Test;
import polymorphia.artifacts.ArtifactFactory;
import polymorphia.artifacts.Cloak;
import polymorphia.commands.ICommand;
import polymorphia.maze.Room;
import polymorphia.strategy.StrategyFactory;

import static org.junit.jupiter.api.Assertions.*;

class CloakedCharacterTest {
    private final CharacterFactory characterFactory = new CharacterFactory(new StrategyFactory());
    private final ArtifactFactory artifactFactory = new ArtifactFactory();

    @Test
    void cloakHidesCowardFromCreaturesUntilItWearsOut() {
        Room room = new Room("Starting");
        Character coward = characterFactory.createCoward("Galahad");
        Character creature = characterFactory.createCreature("Satan");
        Cloak cloak = (Cloak) artifactFactory.createCloak("invisibility", 1.0);
        room.addCharacter(coward);
        room.addCharacter(creature);
        room.addArtifact(cloak);

        ICommand command = coward.getAction(room);
        command.executeCommand();

        Character cloakedCoward = room.getCharacterWithName("Galahad").orElseThrow();
        assertInstanceOf(CloakedCharacter.class, cloakedCoward);
        assertFalse(cloakedCoward.isVisible());
        assertFalse(room.hasArtifact(cloak));

        double healthBeforeCreatureTurn = cloakedCoward.getHealth();
        ICommand creatureCommand = creature.getAction(room);
        creatureCommand.executeCommand();
        assertEquals(healthBeforeCreatureTurn, cloakedCoward.getHealth());

        ICommand cloakedCowardCommand = cloakedCoward.getAction(room);
        cloakedCowardCommand.executeCommand();
        assertTrue(cloakedCoward.isVisible());
    }

    @Test
    void artifactFactoryCreatesRequestedNumberOfCloaks() {
        assertEquals(3, artifactFactory.createCloaks(3).size());
    }
}
