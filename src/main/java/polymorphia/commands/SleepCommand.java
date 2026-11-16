package polymorphia.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import polymorphia.characters.Character;
import polymorphia.maze.IRoom;

public class SleepCommand extends Command {
    private static final Logger logger = LoggerFactory.getLogger(SleepCommand.class);

    public SleepCommand(Character myself, IRoom room) {
        super(CommandType.SLEEP, myself, room);
    }

    @Override
    public boolean execute() {
        logger.info("Sleeping...");
        return true;
    }
}
