package polymorphia.maze;

import polymorphia.artifacts.ArtifactFactory;
import polymorphia.artifacts.Food;

import java.util.Set;

public class Pantry extends Room implements IRoom {
    private static final ArtifactFactory artifactFactory = new ArtifactFactory();

    public Pantry(String name) {
        super(name);
    }

    @Override
    public <T> Set<T> getItemsOf(Class<T> type) {
        if (type == Food.class && super.getItemsOf(type).isEmpty()) {
            logger.info("Adding food to pantry in room {}", getName());
            addArtifact(artifactFactory.createFood());
        }
        return super.getItemsOf(type);
    }
}
