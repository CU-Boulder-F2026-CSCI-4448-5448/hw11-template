package polymorphia.characters;

import ooad.gameobserver.intf.EventType;
import org.slf4j.Logger;
import polymorphia.Die;
import polymorphia.RandomDie;
import polymorphia.artifacts.Artifact;
import polymorphia.artifacts.Food;
import polymorphia.maze.IMutableRoom;
import polymorphia.maze.IRoom;
import polymorphia.observers.EventBus;
import polymorphia.strategy.IStrategy;
import polymorphia.strategy.SleepStrategy;
import polymorphia.strategy.StrategyFactory;

import java.util.Objects;

import static ooad.gameobserver.intf.EventType.CHARACTER_SWITCHED_STRATEGY;
import static ooad.gameobserver.intf.EventType.FIGHT_OCCURRED;

abstract public class Character {
    public static Double COWARD_THRESHOLD = 4.0;

    public static final Double HEALTH_LOST_IN_AGING_EACH_TURN = 0.05;
    static Logger logger = org.slf4j.LoggerFactory.getLogger(Character.class);

    public static final Double HEALTH_LOST_IN_FIGHT_REGARDLESS_OF_OUTCOME = 0.5;
    public static final Double HEALTH_LOST_IN_MOVING_ROOMS = 0.25;

    static StrategyFactory strategyFactory = new StrategyFactory();

    protected String name;
    private Double health;
    private Die die;
    private IStrategy strategy;
    private boolean hasTreasure = false;

    protected Character() {
    }

    public Character(String name, Double health) {
        this(name, health, RandomDie.sixSided());
    }

    public Character(String name, Double health, Die die) {
        this(name, health, die, new SleepStrategy());
    }

    public Character(String name, Double initialHealth, Die die, IStrategy strategy) {
        this.name = name;
        this.health = initialHealth;
        this.die = die;
        this.strategy = strategy;
    }

    public String toString() {
        return String.format("%s(health: %.2f, %s)", getName(), getHealth(), getDie());
    }

    protected void loseHealth(double healthPoints) {
        if (health <= 0) {
            return;
        }     // already dead, probably called for mandatory health loss for having a fight

        health -= healthPoints;
        if (health <= 0) {
            String message = name + " just died!";
            EventBus.getInstance().postEvent(EventType.CHARACTER_DIED, message);
            logger.info(message);
        }

        if (isAlive() && isFighter() && health < COWARD_THRESHOLD) {
            setStrategy(strategyFactory.newCowardStrategy());
            logger.info(name + " is now a coward!");
        }
    }

    boolean isFighter() {
        return getStrategy().isFighter();
    }

    protected IStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(IStrategy strategy) {
        this.strategy = strategy;
    }

    public double getHealth() {
        return health;
    }

    protected Die getDie() {
        return die;
    }

    public String getName() {
        return name;
    }

    public String getNameAndHealth() {
        return String.format("%s(%.2f)", getName(), getHealth());
    }

    public Boolean isAlive() {
        return getHealth() > 0;
    }

    void loseFightDamage(double fightDamage) {
        loseHealth(fightDamage);
    }

    public Boolean isAdventurer() {
        return false;
    }

    public Boolean isCreature() {
        return false;
    }

    public boolean fight(Character opponent) {
        if (Objects.equals(getName(), opponent.getName())) {
            logger.error(this + " just fought their self");
        }
        Integer adventurerRoll = getRoll();
        Integer opponentRoll = opponent.getRoll();
        logger.info(this + " is fighting " + opponent);

        logger.info(this + " rolled " + adventurerRoll);
        logger.info(opponent + " rolled " + opponentRoll);

        if (adventurerRoll > opponentRoll) {
            opponent.loseFightDamage(adventurerRoll - opponentRoll);
        } else if (opponentRoll > adventurerRoll) {
            loseFightDamage(opponentRoll - adventurerRoll);
        }

        if (!isDead()) {
            loseHealth(Character.HEALTH_LOST_IN_FIGHT_REGARDLESS_OF_OUTCOME);
        }
        if (!opponent.isDead()) {
            opponent.loseHealth(Character.HEALTH_LOST_IN_FIGHT_REGARDLESS_OF_OUTCOME);
        }

        String message = getName() + " fought " + opponent.getName();
        logger.info(message);
        EventBus.getInstance().postEvent(FIGHT_OCCURRED, message);

        // If I died and my opponent was a coward, change their strategy
        if (isDead() && opponent.isGlutton() && opponent.isAlive()) {
            opponent.setStrategy(strategyFactory.newFighterStrategy());
            message = opponent.getName() + " is no longer a coward, but a fighter";
            logger.info(message);
            EventBus.getInstance().postEvent(CHARACTER_SWITCHED_STRATEGY, message);
        }
        return true;
    }

    private boolean isGlutton() {
        return getStrategy().isGlutton();
    }

    public boolean isDead() {
        return !isAlive();
    }

    public boolean isCoward() {
        return getStrategy().isCoward();
    }

    public int getRoll() {
        return getDie().roll();
    }

    public final void doAction(IRoom currentLocation) {
        if (isAlive()) {
            doLivingAction(currentLocation);
        }
        loseHealth(HEALTH_LOST_IN_AGING_EACH_TURN);
    }

    private void doLivingAction(IRoom currentLocation) {
        this.getStrategy().doAction(this, currentLocation);
    }

    protected double getMovingCost() {
        return HEALTH_LOST_IN_MOVING_ROOMS;
    }

    protected void gainHealth(double healthPoints) {
        health += healthPoints;
    }

    public boolean eat(Food food) {
        gainHealth(food.getHealthValue());
        EventBus.getInstance().postEvent(EventType.CHARACTER_ATE, getName() + " ate " + food);
        return true;
    }

    public boolean hasTreasure() {
        return hasTreasure;
    }

    public void takeTreasure(Artifact treasure) {
        if (treasure.isTreasure()) {
            this.hasTreasure = true;
            String message = String.format("%s has captured the treasure %s!", getName(), treasure);
            EventBus.getInstance().postEvent(EventType.TREASURE_ACQUIRED, message);
            logger.info(message);
        }
    }

    public void takeMap(Artifact treasureMap) {
        if (treasureMap.isTreasureMap()) {
            String message = getName() + " has picked up the " + treasureMap;
            EventBus.getInstance().postEvent(EventType.TREASURE_MAP_ACQUIRED, message);
            if (isCoward()) {
                setStrategy(strategyFactory.newSeekerStrategy());
                EventBus.getInstance().postEvent(CHARACTER_SWITCHED_STRATEGY, getName() + " changed to the Seeker strategy");
            }
        }
    }

    public void setDie(Die die) {
        this.die = die;
        EventBus.getInstance().postEvent(EventType.NEW_DIE_ACQUIRED, getName() + " acquired " + die);
    }

    public String getBaseName() {
        return name;
    }

    public boolean isVisible() {
        return true;
    }

    public void pickUpDie(IRoom room, Die die) {
        setDie(die);
        ((IMutableRoom) room).removeArtifact((Artifact) die);
    }

    public void takeArtifact(IRoom room, Artifact artifact) {
        ((IMutableRoom) room).removeArtifact(artifact);
    }
}
