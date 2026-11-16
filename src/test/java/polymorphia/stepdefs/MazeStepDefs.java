package polymorphia.stepdefs;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import polymorphia.FixedDie;
import polymorphia.RandomDie;
import polymorphia.artifacts.*;
import polymorphia.characters.Character;
import polymorphia.characters.CharacterFactory;
import polymorphia.maze.IMutableRoom;
import polymorphia.maze.Maze;
import polymorphia.strategy.StrategyFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static polymorphia.stepdefs.World.DEFAULT_MAZE_NAME;


public class MazeStepDefs {
    private static final int DEFAULT_NUMBER_OF_ROOMS = 5;
    private static final int DEFAULT_MIN_CONNECTIONS = 2;
    private static final String BIDIRECTIONAL = "2-way";

    private static final ArtifactFactory artifactFactory = new ArtifactFactory();
    private static final CharacterFactory characterFactory = new CharacterFactory(new StrategyFactory());

    CharacterStepDefs characterStepDefs;
    private final World world;

    public MazeStepDefs(World world, CharacterStepDefs characterStepDefs) {
        this.world = world;
        this.characterStepDefs = characterStepDefs;
    }

    private IMutableRoom getRoom(String roomName) {
        return world.getRoom(roomName);
    }

    @Given("a maze with the following attributes:")
    public void aMazeWithTheFollowingAttributes(Map<String, Integer> mazeArgs) {
        int numRooms = DEFAULT_NUMBER_OF_ROOMS;
        int numConnections = DEFAULT_MIN_CONNECTIONS;
        for (Map.Entry<String, Integer> entry : mazeArgs.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();
            switch (key) {
                case "Rooms":
                    numRooms = value;
                    break;
                case "Connections":
                    numConnections = value;
                    break;
            }
        }

        world.getMazeBuilder().createConnectedRooms(numConnections, numRooms);
        addToRooms(mazeArgs);
    }

    @And("^the maze has the following (?:attribute|artifact) counts:$")
    public void addToRooms(Map<String, Integer> mazeArgs) {
        addToNamedMazeRooms(DEFAULT_MAZE_NAME, mazeArgs);
    }

    @And("^the (.+) maze has the following (?:attribute|artifact) counts:$")
    public void addToNamedMazeRooms(String mazeName, Map<String, Integer> mazeArgs) {
        Maze.Builder mazeBuilder = world.getMazeBuilder(mazeName);
        for (Map.Entry<String, Integer> entry : mazeArgs.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();
            switch (key) {
                case "Food":
                    mazeBuilder.addArtifacts(artifactFactory.createFood(value));
                    break;
                case "Treasure":
                    addTreasuresToMaze(mazeName, value);
                    break;
                case "Treasure Maps":
                    addTreasureMapsToMaze(mazeName, value);
                    break;
                case "Dice":
                    addDiceToMaze(mazeName, value);
                    break;
                case "Knights":
                    mazeBuilder.addCharacters(characterFactory.createKnights(value));
                    break;
                case "Gluttons":
                    mazeBuilder.addCharacters(characterFactory.createGluttons(value));
                    break;
                case "Cowards":
                    mazeBuilder.addCharacters(characterFactory.createCowards(value));
                    break;
                case "Creatures":
                    mazeBuilder.addCharacters(characterFactory.createCreatures(value));
                    break;
                case "Demons":
                    mazeBuilder.addCharacters(characterFactory.createDemons(value));
                    break;
                case "Armor":
                    mazeBuilder.addArtifacts(artifactFactory.createArmoredSuits(value));
                    break;
                case "Weapons":
                    mazeBuilder.addArtifacts(artifactFactory.createWeapons(value));
                    break;
                case "Potions":
                    mazeBuilder.addArtifacts(artifactFactory.createPotions(value));
                    break;
            }
        }
    }

    private void addTreasuresToMaze(String mazeName, int value) {
        List<Artifact> treasures = artifactFactory.createTreasures(value);
        for (Artifact treasure : treasures) {
            IMutableRoom randomRoom = addArtifactToRandomRoomInMaze(mazeName, treasure);
            Character guard = characterStepDefs.createCharacter("guard", "Guard of " + treasure.getName());
            randomRoom.addCharacter(guard);
        }
    }

    private void addTreasureMapsToMaze(String mazeName, int value) {
        List<Artifact> dice = artifactFactory.createTreasureMaps(value);
        for (Artifact die : dice) {
            addArtifactToRandomRoomInMaze(mazeName, die);
        }
    }

    private void addDiceToMaze(String mazeName, int value) {
        List<Artifact> dice = artifactFactory.createRandomDice(value);
        for (Artifact die : dice) {
            addArtifactToRandomRoomInMaze(mazeName, die);
        }
    }

    private IMutableRoom addArtifactToRandomRoomInMaze(String mazeName, Artifact artifact) {
        world.putArtifact(artifact.getName(), artifact);
        IMutableRoom randomRoom = world.getMazeBuilder(mazeName).getRandomRoom();
        randomRoom.addArtifact(artifact);
        return randomRoom;
    }

    @And("^a (knight|glutton|coward|creature|demon|seeker|guard) named (.+) in the (\\w+) room$")
    public void createCharacterAndPutInRoom(String characterType, String characterName, String roomName) {
        Character character = characterStepDefs.createCharacter(characterType, characterName);
        IMutableRoom room = world.getMazeBuilder().getRoom(roomName);
        room.addCharacter(character);
    }

    @Then("{word} room contains {word}")
    public void roomContainsCharacter(String roomName, String characterName) {
        IMutableRoom room = getRoom(roomName);
        Character character = world.getCharacter(characterName);
        assertTrue(room.containsCharacter(character));
    }


    @Given("a fully-connected maze with the following rooms:")
    public void aFullyConnectedMazeWithTheFollowingRooms(List<String> roomNames) {
        world.getMazeBuilder().createFullyConnectedRooms(roomNames);
    }

    @And("the {word} room contains the following food items:")
    public void roomContainsTheFollowingFoodItems(String roomName, List<Map<String, String>> foodDescriptions) {
        IMutableRoom room = world.getMazeBuilder().getRoom(roomName);
        for (Map<String, String> foodDescription : foodDescriptions) {
            Artifact food = artifactFactory.createFood(
                    foodDescription.get("Name"),
                    Double.parseDouble(foodDescription.get("Value")));
            world.putArtifact(food.getName(), food);
            room.addArtifact(food);
        }
    }

    @And("the {word} room contains the following artifacts:")
    public void theStartingRoomContainsTheFollowingArtifacts(String roomName, List<Map<String, String>> artifactDescriptions) {
        IMutableRoom room = world.getRoom(roomName);
        for (Map<String, String> artifactDescription : artifactDescriptions) {
            String type = artifactDescription.get("Type");
            String name = artifactDescription.get("Name");
            double strength = Double.parseDouble(artifactDescription.get("Strength"));
            double movingCost = 0.0;
            if (artifactDescription.containsKey("Moving Cost")) {
                movingCost = Double.parseDouble(artifactDescription.get("Moving Cost"));
            }
            Artifact artifact = switch (type) {
                case "Armor" -> artifactFactory.createArmor(name, strength, movingCost);
                case "Cloak" -> artifactFactory.createCloak(name, strength);
                case "Weapon" -> artifactFactory.createWeapon(name, strength, movingCost);
                case "Potion" -> artifactFactory.createPotion(name, strength);
                case "Treasure" -> artifactFactory.createTreasure(name);
                case "Treasure Map" -> artifactFactory.createTreasureMap(name);
                case "Die" -> (strength > 0) ? new FixedDie((int) strength) : artifactFactory.createRandomDie();
                default -> artifactFactory.createFood(name, strength);
            };
            world.putArtifact(artifact.getName(), artifact);
            room.addArtifact(artifact);
        }
    }

    @Then("^the (.+) room no longer contains (.+)$")
    public void roomNoLongerContainsArtifact(String roomName, String artifactName) {
        IMutableRoom room = getRoom(roomName);
        assertFalse(room.hasArtifact(world.getArtifact(artifactName)));
    }

    @And("^(.+) is now(| not) in the (.+) room$")
    public void characterIsInRoom(String characterName, String not, String roomName) {
        IMutableRoom room = getRoom(roomName);
        Character character = world.getCharacter(characterName);
        if (not.equals(" not")) {
            assertFalse(room.containsCharacter(character));
        } else {
            assertTrue(room.containsCharacter(character));
        }
    }


    @Then("{word} is still in the {word} room")
    public void characterIsStillInTheRoom(String characterName, String roomName) {
        characterIsInRoom(characterName, "", roomName);
    }

    @Given("^a (unidirectional|bidirectional), (randomly|sequentially)-distributed maze with the following attributes:$")
    public void connectedDistributedMazeWithTheFollowingAttributes(String roomConnectionType, String distributionType, Map<String, Integer> mazeArgs) {
        world.getMazeBuilder().setConnectionDirection(Objects.equals(roomConnectionType, "bidirectional"));
        if (Objects.equals(distributionType, "randomly")) {
            world.getMazeBuilder().distributeRandomly();
        } else {
            world.getMazeBuilder().distributeSequentially();
        }
        aMazeWithTheFollowingAttributes(mazeArgs);
    }

    @Given("a maze with the following rooms:")
    public void aMazeWithTheFollowingRooms(List<String> roomNames) {
        world.getMazeBuilder().createFullyConnectedRooms(roomNames);
    }

    @Given("a maze with the following rooms and neighbors:")
    public void aMazeWithTheFollowingRoomsAndNeighbors(List<Map<String, String>> roomNeighbors) {
        aMazeWithTheFollowingRoomsTypesAndNeighbors(roomNeighbors);
    }

    @Given("a maze with the following rooms, types, and neighbors:")
    public void aMazeWithTheFollowingRoomsTypesAndNeighbors(List<Map<String, String>> roomNeighbors) {
        aNamedMazeWithTheFollowingRoomsTypesAndNeighbors(DEFAULT_MAZE_NAME, roomNeighbors);
    }

    @Given("a {word} maze with the following rooms, types, and neighbors:")
    public void aNamedMazeWithTheFollowingRoomsTypesAndNeighbors(String mazeName, List<Map<String, String>> roomNeighbors) {
        Maze.Builder mazeBuilder = world.getMazeBuilder(mazeName);
        for (Map<String, String> roomKeyedNeighbors : roomNeighbors) {
            String roomName = roomKeyedNeighbors.get("Room");
            String roomType = roomKeyedNeighbors.get("RoomType");
            if (roomType == null) {
                roomType = "Standard";
            }
            if (Objects.equals(roomType, "Maze")) {
                IMutableRoom mazeRoom = world.getMaze(roomName);
                mazeBuilder.addRoom(mazeRoom);
            } else {
                mazeBuilder.addRoomIfNotAlreadyInMaze(roomName, roomType);
            }
            String neighborString = roomKeyedNeighbors.get("Neighbors");
            if (neighborString != null) {
                String[] neighbors = roomKeyedNeighbors.get("Neighbors").split(",");
                for (String neighborName : neighbors) {
                    mazeBuilder.addRoomIfNotAlreadyInMaze(neighborName.trim());
                    mazeBuilder.addNeighborToRoom(roomName, neighborName.trim());
                }
            }
        }
    }

    @And("each room is connected to {int} other rooms via {word} connections")
    public void eachRoomIsConnectedToOtherRoomsViaConnections(int numberOfConnections, String connectionType) {
        world.getMazeBuilder().setConnectionDirection(Objects.equals(connectionType, BIDIRECTIONAL));
        world.getMazeBuilder().connectRooms(numberOfConnections);
    }

    @And("{word} is in the {word} room")
    public void characterIsInTheRoom(String characterName, String roomName) {
        world.getMazeBuilder().addToRoom(roomName, world.getCharacter(characterName));
    }

    @And("food {word} is in the {word} room")
    public void foodIsInTheRoom(String foodName, String roomName) {
        world.getMazeBuilder().addToRoom(roomName, artifactFactory.createFood(foodName));
    }

    @And("{word} room does not contain {word}")
    public void roomDoesNotContainCharacter(String roomName, String characterName) {
        IMutableRoom room = getRoom(roomName);
        Character character = world.getCharacter(characterName);
        assertFalse(room.containsCharacter(character));
    }

    @And("the {word} room has {int} food items")
    public void theRoomHasFoodItems(String roomName, int expectedNumberOfFoodItems) {
        IMutableRoom room = getRoom(roomName);
        assertEquals(expectedNumberOfFoodItems, room.getItemsOf(Food.class).size());
    }

    @And("^a treasure is hidden in (\\w+) room$")
    public void treasureIsHiddenInRoom(String roomName) {
        IMutableRoom room = world.getMazeBuilder().getRoom(roomName);
        room.addArtifact(artifactFactory.createTreasure());
    }

    @And("there is no {word} in {word} room")
    public void thereIsNoArtifactInRoom(String artifactType, String roomName) {
        IMutableRoom room = getRoom(roomName);
        Class<? extends Artifact> artifactClass = switch (artifactType) {
            case "Food" -> Food.class;
            case "Treasure Map" -> TreasureMap.class;
            case "Die" -> RandomDie.class;
            case "Fixed Die" -> FixedDie.class;
            case "Armor" -> Armor.class;
            case "Weapon" -> Weapon.class;
            case "Treasure" -> Treasure.class;
            case "Potion" -> Potion.class;
            default -> Artifact.class;
        };
        assertFalse(room.hasArtifact(artifactClass));
    }

    @And("the {word} maze has the following characters in it:")
    public void theLevelMazeHasTheFollowingCharactersInIt(String mazeName, List<Map<String, String>> characterRoomMaps) {
        Maze.Builder builder = world.getMazeBuilder(mazeName);
        for (Map<String, String> characterRoomMap : characterRoomMaps) {
            String characterName = characterRoomMap.get("Character");
            String roomName = characterRoomMap.get("Room");
            Character character = world.getCharacter(characterName);
            IMutableRoom room = builder.getRoom(roomName);
            room.addCharacter(character);
        }
    }
}