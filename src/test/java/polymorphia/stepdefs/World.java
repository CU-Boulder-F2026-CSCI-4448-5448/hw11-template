package polymorphia.stepdefs;

import polymorphia.ObservablePolymorphia;
import polymorphia.artifacts.Artifact;
import polymorphia.maze.IMutableRoom;
import polymorphia.maze.Maze;
import polymorphia.maze.Room;
import polymorphia.maze.RoomFactory;
import polymorphia.observers.TestObserver;
import polymorphia.characters.Character;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class World {
    public static final String DEFAULT_MAZE_NAME = "default";
    private final Map<String, Character> characters = new HashMap<>();
    private final Map<String, Double> initialCharactersHealth = new HashMap<>();
    private ObservablePolymorphia game;
    private final RoomFactory roomFactory = new RoomFactory();
    private final Map<String, Maze.Builder> mazeBuilders = new HashMap<>();
    private final Map<String, Maze> mazes = new HashMap<>();
    private final Map<String, Artifact> artifacts = new HashMap<>();
    private final TestObserver observer = new TestObserver();
    private final Map<String, IMutableRoom> roomMap = new HashMap<>();

    public void putArtifact(String artifactName, Artifact artifact) {
        artifacts.put(artifactName, artifact);
    }

    public Artifact getArtifact(String artifactName) {
        return artifacts.get(artifactName);
    }

    public Character getCharacter(String name) {
        if (mazes.isEmpty()) {
            return characters.get(name);
        }
        // Check to see if we are adding a new character into the maze, mid-game
        Optional<Character> possiblyWrappedCharacter = getCharacterWithName(name);
        return possiblyWrappedCharacter.orElseGet(() -> characters.get(name));
    }

    private Optional<Character> getCharacterWithName(String name) {
        return getMazes().stream()
                .flatMap(maze -> maze.getRooms().stream())
                .map(room -> room.getCharacterWithName(name))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    private List<Maze> getMazes() {
        return mazeBuilders.values().stream()
                .map(Maze.Builder::build)
                .collect(Collectors.toList());
    }

    public IMutableRoom getRoomWithCharacter(Character character) {
        Optional<IMutableRoom> room = roomMap.values().stream()
                .filter(r -> r.containsCharacterWithName(character.getBaseName()))
                .findFirst();
        assertTrue(room.isPresent());
        return room.get();
    }

    public void putCharacter(Character character) {
        characters.put(character.getName(), character);
        initialCharactersHealth.put(character.getName(), character.getHealth());
    }

    public double getInitialCharacterHealth(String characterName) {
        return initialCharactersHealth.get(characterName);
    }

    public ObservablePolymorphia getGame() {
        boolean mustFindTreasureToWin = false;
        return getGame(DEFAULT_MAZE_NAME, mustFindTreasureToWin );
    }

    public ObservablePolymorphia getGame(String mazeName, boolean mustFindTreasureToWin) {
        if (game == null) {
            game = new ObservablePolymorphia(getMaze(mazeName));
            game.attach(observer);
        }
        if (mustFindTreasureToWin) {
            game.mustFindTreasureToWin();
        }
        return game;
    }

    Maze getMaze() {
        return getMaze(DEFAULT_MAZE_NAME);
    }

    Maze getMaze(String mazeName) {
        if (!mazes.containsKey(mazeName)) {
            mazes.put(mazeName, getMazeBuilder(mazeName).build());
            for (IMutableRoom room : mazes.get(mazeName).getRooms()) {
                roomMap.put(room.getName(), room);
            }
        }
        return mazes.get(mazeName);
    }

    public Maze.Builder getMazeBuilder() {
        return getMazeBuilder(DEFAULT_MAZE_NAME);
    }
    public Maze.Builder getMazeBuilder(String mazeName) {
        if (!mazeBuilders.containsKey(mazeName)) {
            mazeBuilders.put(mazeName, Maze.getNewBuilder(roomFactory, mazeName));
        }
        return mazeBuilders.get(mazeName);
    }

    public TestObserver getObserver() {
        return observer;
    }

    public IMutableRoom getRoom(String roomName) {
        return mazeBuilders.values().stream()
                .flatMap(builder -> builder.getRooms().stream())
                .filter(room -> room.getName().equals(roomName))
                .findFirst()
                .orElse(null);
    }
}