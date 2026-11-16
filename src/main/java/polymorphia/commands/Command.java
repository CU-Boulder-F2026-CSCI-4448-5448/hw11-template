package polymorphia.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import polymorphia.characters.Character;
import polymorphia.maze.IRoom;
import polymorphia.strategy.StrategyFactory;


public abstract class Command implements ICommand {
    Logger logger = LoggerFactory.getLogger(Command.class);

    static StrategyFactory strategyFactory = new StrategyFactory();

    protected CommandType type;
    protected final Character myself;
    protected final IRoom room;

    public Command(CommandType type, Character myself, IRoom room) {
        this.type = type;
        this.myself = myself;
        this.room = room;
    }

    @Override
    public CommandType getType() {
        return type;
    }

    @Override
    public String optionString() {
        return "";
    }

    @Override
    public final void executeCommand() {
        if (execute()) {
            logger.info("Command {} executed successfully by {}", type, myself.getName());
        } else {
            logger.warn("Command {} failed to execute by {}", type, myself.getName());
        }
        myself.turnTaken();
    }

}
