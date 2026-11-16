package polymorphia.artifacts;

import polymorphia.RandomDie;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class ArtifactFactory {
    private static final Random random = new Random();

    private static final double MINIMUM_STRENGTH_VALUE = 1.0;
    private static final double MAXIMUM_STRENGTH_VALUE = 2.0;

    private static final String[] FOOD_NAMES = new String[]{
            "cupcake", "apple", "banana", "steak", "salad", "fries", "burger", "pizza", "eggs",
            "bacon", "muffin", "donut", "chicken", "pasta", "rice", "sushi", "taco", "burrito",
            "nachos", "chips"};

    private static final String[] TREASURE_NAMES = new String[]{
            "gold", "silver", "diamond", "ruby", "emerald", "sapphire", "topaz", "opal",
            "amethyst", "quartz", "agate", "jade", "onyx", "pearl", "coral"};

    private static final String[] ARMOR_NAMES = new String[]{
            "iron", "mithril", "bronze", "copper"};
    private static final String[] WEAPON_NAMES = new String[]{
            "sword", "mace", "axe", "dagger", "spear"};
    private static final String[] POTION_NAMES = new String[]{
            "Elixir of Life", "Potion of Reanimation", "Sorcerer's Serum", "Healing Potion"};
    private static final String[] CLOAK_NAMES = new String[]{
            "invisibility-cloak", "no-see-um-cloak", "Harry-Potter-cloak"};

    private static double getRandomStrengthValue() {
        return random.nextDouble(MINIMUM_STRENGTH_VALUE, MAXIMUM_STRENGTH_VALUE);
    }

    private static String getRandomFoodName() {
        return FOOD_NAMES[random.nextInt(FOOD_NAMES.length)];
    }

    private static String getRandomTreasureName() {
        return TREASURE_NAMES[random.nextInt(TREASURE_NAMES.length)];
    }
    private static String getRandomPotionName() {
        return POTION_NAMES[random.nextInt(POTION_NAMES.length)];
    }
    private static String getRandomWeaponName() {
        return WEAPON_NAMES[random.nextInt(WEAPON_NAMES.length)];
    }
    private static String getRandomArmorName() {
        return ARMOR_NAMES[random.nextInt(ARMOR_NAMES.length)];
    }

    private static String getRandomCloakName() {
        return CLOAK_NAMES[random.nextInt(CLOAK_NAMES.length)];
    }

    public Artifact createFood(String name, double healthValue) {
        return new Food(name, healthValue);
    }

    public Artifact createFood(String name) {
        return createFood(name, getRandomStrengthValue());
    }

    public Artifact createTreasure(String name) {
        return new Treasure(name);
    }

    public Artifact createTreasure() {
        return createTreasure(getRandomTreasureName());
    }

    public Artifact createFood() {
        return createFood(getRandomFoodName(), getRandomStrengthValue());
    }
    public Artifact createPotion() {
        return createPotion(getRandomPotionName(), getRandomStrengthValue());
    }
    public Artifact createPotion(String name, double strength) {
        return new Potion(name, strength);
    }

    public Artifact createWeapon() {
        return createWeapon(getRandomWeaponName(), getRandomStrengthValue());
    }

    public Artifact createWeapon(String name, double strength) {
        return new Weapon(name, strength);
    }

    public Artifact createWeapon(String name, double strength, double movingCost) {
        return new Weapon(name, strength, movingCost);
    }

    public Artifact createArmor() {
        return new Armor(getRandomArmorName(), getRandomStrengthValue());
    }

    public Artifact createArmor(String name, double strength, double movingCost) {
        return new Armor(name, strength, movingCost);
    }

    public Artifact createCloak() {
        return createCloak(getRandomCloakName(), getRandomStrengthValue());
    }

    public Artifact createCloak(String name, double strength) {
        return new Cloak(name, strength);
    }

    public List<Artifact> createFood(int numberOfItems) {
        return IntStream.range(0, numberOfItems)
                .mapToObj(_ -> createFood())
                .toList();
    }

    public List<Artifact> createTreasures(int value) {
        return IntStream.range(0, value)
                .mapToObj(_ -> createTreasure())
                .toList();
    }

    public List<Artifact> createTreasureMaps(int value) {
        return IntStream.range(0, value)
                .mapToObj(count -> createTreasureMap("Map" + count))
                .toList();
    }

    public List<Artifact> createArmoredSuits(int value) {
        return IntStream.range(0, value)
                .mapToObj(_ -> createArmor())
                .toList();
    }

    public List<Artifact> createCloaks(int value) {
        return IntStream.range(0, value)
                .mapToObj(_ -> createCloak())
                .toList();
    }
    public List<Artifact> createWeapons(int value) {
        return IntStream.range(0, value)
                .mapToObj(_ -> createWeapon())
                .toList();
    }
    public List<Artifact> createPotions(int value) {
        return IntStream.range(0, value)
                .mapToObj(_ -> createPotion())
                .toList();
    }

    public Artifact createTreasureMap(String name) {
        return new TreasureMap(name);
    }

    public Artifact createRandomDie(int sides) {
        return new RandomDie(sides);
    }

    public Artifact createRandomDie() {
        int numSides = random.nextInt(10) + 1;
        return createRandomDie(numSides);
    }

    public List<Artifact> createRandomDice(int numDie) {
        return IntStream.range(0, numDie)
                .mapToObj(_ -> createRandomDie())
                .toList();
    }
}
