package polymorphia;

import ooad.gameobserver.intf.*;
import polymorphia.maze.Maze;
import polymorphia.characters.Character;
import polymorphia.observers.EventBus;
import polymorphia.observers.IPolymorphiaEventsObserver;

import java.util.List;

public class ObservablePolymorphia extends Polymorphia implements IGame, IMazeSubject, IPolymorphiaEventsObserver {

    public ObservablePolymorphia(Maze maze) {
        super(maze);
        // Polymorphia also listens for events, specifically when a character gets reincarnated
        EventBus.getInstance().attach(this);
    }

    @Override
    public void attach(IGameObserver observer) {
        EventBus.getInstance().attach(observer);
    }

    @Override
    public void detach(IGameObserver observer) {
        EventBus.getInstance().detach(observer);
    }

    @Override
    public void attach(IGameObserver observer, List<EventType> events) {
        EventBus.getInstance().attach(observer, events);
    }

    @Override
    protected void playTurn() {
        super.playTurn();
        EventBus.getInstance().postEvent(EventType.FINISHED_TURN, "Finished turn " + getTurnCount());
    }

    @Override
    public IMaze getMaze() {
        return new MazeAdapter(maze);
    }

    @Override
    public void update(EventType eventType, Object eventObject) {
        switch (eventType) {
            case ADD_CHARACTER:
                Character newCharacter = (Character) eventObject;
                addToRandomRoom(newCharacter);
                break;
            default:
                logger.info("Unhandled event type: " + eventType);
        }
    }

    private void addToRandomRoom(Character newCharacter) {
        maze.addToRandomRoom(newCharacter);
    }
}
