package polymorphia.commands;

import org.slf4j.Logger;
import polymorphia.artifacts.Artifact;
import polymorphia.characters.Character;
import polymorphia.characters.CharacterFactory;
import polymorphia.maze.IMutableRoom;
import polymorphia.maze.IRoom;
import polymorphia.strategy.StrategyFactory;

public abstract class ArtifactCommand extends Command {
    static Logger logger = org.slf4j.LoggerFactory.getLogger(ArtifactCommand.class);
    static final CharacterFactory characterFactory = new CharacterFactory(new StrategyFactory());

    Artifact artifact;

    ArtifactCommand(CommandType type, Character character, IRoom room, Artifact artifact) {
        super(type, character, room);
        this.artifact = artifact;
    }

    Artifact getArtifactFromRoom() {
        if (!room.hasArtifact(artifact)) {
            logger.warn("Artifact not in room");
        }
        ((IMutableRoom) room).removeArtifact(artifact);
        return artifact;
    }

    @Override
    public String optionString() {
        return artifact.getName();
    }

}
