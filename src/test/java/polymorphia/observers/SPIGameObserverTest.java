package polymorphia.observers;

import ooad.gameobserver.intf.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SPIGameObserverTest {

    // TODO: This test will pass when you implement an IMazeObserverProvider and IMazeObserver via the SPI interface
    @Test
    void getMazeObserverViaSPI() {
        List<IGameObserver> observers = getGameObserversViaSPI();
        System.out.println("Found " + observers.size() + " observers");
        System.out.println(observers);
        assertTrue(observers.size() > 0);
    }

    public static List<IGameObserver> getGameObserversViaSPI() {
        return getGameObserversViaSPI(new SimpleServiceContext());
    }

    public static List<IGameObserver> getGameObserversViaSPI(IServiceContext serviceContext) {
        List<IGameObserver> observers = new ArrayList<>();

        ServiceLoader<IGameObserverProvider> loader = ServiceLoader.load(IGameObserverProvider.class);
        Iterator<IGameObserverProvider> iterator = loader.iterator();

        while (iterator.hasNext()) {
            IGameObserverProvider gameObserverProvider = iterator.next();
            System.out.println("Found provider implementation: " + gameObserverProvider.getClass().getName());
            assertNotNull(gameObserverProvider);

            try {
                IGameObserver gameObserver = gameObserverProvider.create(serviceContext);
                System.out.println("\tFound observer implementation: " + gameObserver.getClass().getName());
                assertNotNull(gameObserver);
                observers.add(gameObserver);
            } catch (Exception e) {
                System.out.println("\tError creating IGameObserver from provider " + gameObserverProvider.getClass().getName() + ": " + e.getMessage());
            }
        }
        return observers;
    }
}
