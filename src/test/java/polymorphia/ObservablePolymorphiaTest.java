package polymorphia;

import ooad.gameobserver.intf.*;
import ooad.mazeobserver.MazeObserver;
import ooad.mazeobserver.RoomShape;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import polymorphia.artifacts.ArtifactFactory;
import polymorphia.characters.CharacterFactory;
import polymorphia.maze.Maze;
import polymorphia.maze.RoomFactory;
import polymorphia.observers.SPIGameObserverTest;
import polymorphia.observers.TestObserver;
import polymorphia.strategy.StrategyFactory;

import java.awt.*;
import java.util.List;

class ObservablePolymorphiaTest {
    private final RoomFactory roomFactory = new RoomFactory();
    private final ArtifactFactory artifactFactory = new ArtifactFactory();
    private final CharacterFactory characterFactory = new CharacterFactory(new StrategyFactory());

    @Disabled("Usually disabled because this test adds audible and visual observers")
    @Test
    void testComplexMazeShapeWithObserver() throws InterruptedException {
        int gridWidth = 3;
        int gridHeight = 2;
        String gridRoomNamePrefix = "Grid";
        Maze maze = Maze.getNewBuilder(roomFactory)
//                .createFullyConnectedRooms("FC 1", "FC 2", "FC 3", "FC 4")
                .createStraightLineRooms("S:Rivendell", "S:Mordor", "S:BagEnd")
                .createRingOfConnectedRooms("R:Crystal", "R:Lava Pool", "R:Fangorn Forest")
                .addNeighborToRoom("S:BagEnd", "R:Crystal")
//                .addNeighborToRoom("FC 1", "R:Fangorn Forest")
                .createAGridOfRooms(gridWidth, gridHeight, gridRoomNamePrefix)
//                .addNeighborToRoom("FC 4", gridRoomNamePrefix + "0-0")
                .addNeighborToRoom("S:Rivendell", gridRoomNamePrefix + "0-0")
                .addCharacters(characterFactory.createCharacters(12))
                .addArtifacts(artifactFactory.createFood(8))
                .addToSameRoom(characterFactory.createGuard(), artifactFactory.createTreasure())
                .build();

        ObservablePolymorphia game = new ObservablePolymorphia(maze);

        // Add Observers
        IGameObserver testObserver = new TestObserver();
        game.attach(testObserver);

        // Add Maze Viewer
        MazeObserver mazeViewer = MazeObserver.getNewBuilder(game, "Ploymorphia Maze Viewer")
                .useSquareRooms()
                .setDimension(1000)
                .setRoomDimension(150)
                .setBackgroundColor(Color.WHITE)
                .setRoomBackgroundColor(Color.LIGHT_GRAY)
                .setTextColor(Color.BLACK)
                .build();
        game.attach(mazeViewer);

        game.play();

        Thread.currentThread().sleep(5000);
        System.out.println(maze.toString());
    }
}
