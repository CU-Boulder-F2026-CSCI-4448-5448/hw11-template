package polymorphia.artifacts;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class ArtifactBag {
    private final List<Artifact> artifacts = new ArrayList<>();

    // Yes, this cast is a bit of code smell, which is why this method is private and
    // this odor is contained within this class. The alternative would be to have a separate list for each type of
    // artifact, but that would be more code and more complexity.
    public <T> Set<T> getItemsOf(Class<T> artifactClass) {
        return artifacts.stream()
                .filter(artifactClass::isInstance)
                .map(artifactClass::cast)
                .collect(Collectors.toUnmodifiableSet());
    }

    public boolean contains(Artifact artifact) {
        return artifacts.contains(artifact);
    }

    public boolean contains(Class<? extends Artifact> artifactClass) {
        return artifacts.stream()
                .anyMatch(artifactClass::isInstance);
    }

    public Artifact[] allItems() {
        return artifacts.toArray(new Artifact[0]);
    }

    public void add(Artifact artifact) {
        artifacts.add(artifact);
    }

    public <T extends Artifact> Optional<T> removeOneItemOfType(Class<T> type) {
        Optional<T> artifact = artifacts.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .findFirst();
        artifact.ifPresent(artifacts::remove);
        return artifact;
    }

    @Override
    public String toString() {
        return artifacts.stream()
                .map(Artifact::toString)
                .collect(Collectors.joining(", "));
    }

    public Set<Artifact> getAllArtifacts() {
        return Set.copyOf(artifacts);
    }

    public <T extends Artifact> Set<T> getArtifacts(Class<T> type) {
        return artifacts.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .collect(Collectors.toSet());
    }

    public void remove(Artifact artifact) {
        artifacts.remove(artifact);
    }
}
