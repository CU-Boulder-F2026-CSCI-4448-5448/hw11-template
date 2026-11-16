package polymorphia.characters;

import ooad.gameobserver.intf.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import polymorphia.artifacts.Artifact;
import polymorphia.observers.EventBus;
import polymorphia.strategy.StrategyFactory;

public class ReincarnatableCharacter extends DecoratedCharacter {
    private static final Logger logger = LoggerFactory.getLogger(ReincarnatableCharacter.class);
    private static final CharacterFactory characterFactory = new CharacterFactory(new StrategyFactory());

    public ReincarnatableCharacter(Character character, Artifact potion) {
        super(character, potion);
    }

    @Override
    public String getName() {
        return "Reincarnatable " + myself.getName();
    }

    @Override
    protected void loseHealth(double healthToLose) {
        super.loseHealth(healthToLose);
        if (!isAlive()) {
            // If I died, I'll reincarnate (without the ability to do it again)
            Character rebornSelf = characterFactory.createKnight("Reborn " + myself.getBaseName(), artifact.getStrength());
            String msg = String.format("%s just reborn with health %.2f!", myself.getBaseName(), artifact.getStrength());
            logger.info(msg);

            // Polymorphia should receive this event and add the new character to a random room
            // The old character, now dead, stays in the game but is no longer active
            EventBus.getInstance().postEvent(EventType.ADD_CHARACTER, rebornSelf);
        }
    }
}
