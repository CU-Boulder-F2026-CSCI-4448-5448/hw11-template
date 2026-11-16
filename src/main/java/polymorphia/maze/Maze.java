package polymorphia.maze;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import polymorphia.artifacts.Artifact;
import polymorphia.characters.Character;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Maze implements IRoom, IMutableRoom, Iterable<CharacterLocationPair> {
    private String name;
    private List<IMutableRoom> rooms = new ArrayList<>();

    private Maze() {
    }

    @Override
    public Iterator<CharacterLocationPair> iterator() {
        return new CharacterLocationIterator();
    }

    private class CharacterLocationIterator implements Iterator {
        private List<CharacterLocationPair> characterLocationPairs = new ArrayList<>();
        private int currentIndex = 0;

        CharacterLocationIterator() {
            for (IMutableRoom room : rooms) {
                for (Character character : room.getLivingCharacters()) {
                    characterLocationPairs.add(new CharacterLocationPair(character, room));
                }
            }
            // Shuffle the list to randomize the order of characters for their turns
            Collections.shuffle(characterLocationPairs);
        }

        @Override
        public boolean hasNext() {
            return currentIndex < characterLocationPairs.size();
        }

        @Override
        public CharacterLocationPair next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return characterLocationPairs.get(currentIndex++);
        }
    }

    public int size() {
        return rooms.size();
    }

    public String toString() {
        return String.join("\n\n", rooms.stream().map(Object::toString).toList());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<IRoom> getNeighbors() {
        return rooms.stream()
                .flatMap(room -> room.getNeighbors().stream())
                .filter(room -> !rooms.contains(room))
                .collect(Collectors.toSet());
    }

    public Set<Character> getLivingCharacters() {
        Set<Character> characters = new HashSet<>();
        for (IMutableRoom room : rooms) {
            characters.addAll(room.getLivingCharacters());
        }
        return characters;
    }

    @Override
    public Map<IRoom, Set<Character>> getLivingCharactersByRoom() {
        Map<IRoom, Set<Character>> charactersByRoom = new LinkedHashMap<>();
        for (IMutableRoom room : rooms) {
            charactersByRoom.putAll(room.getLivingCharactersByRoom());
        }
        return charactersByRoom;
    }

    public List<IMutableRoom> getRooms() {
        return List.copyOf(rooms);
    }

    private IMutableRoom getRandomRoom() {
        return rooms.get((new Random()).nextInt(rooms.size()));
    }

    public static Builder getNewBuilder(RoomFactory roomFactory) {
        return new Builder(roomFactory);
    }

    public static Builder getNewBuilder(RoomFactory roomFactory, String mazeName) {
        return new Builder(roomFactory, mazeName);
    }

    public IMutableRoom getRoom(String roomName) {
        return rooms.stream()
                .filter(room -> room.getName().equals(roomName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No room with name " + roomName));
    }

    public Optional<IMutableRoom> getRoomContainingCharacterWithName(String baseName) {
        return rooms.stream()
                .filter(room -> room.containsCharacterWithName(baseName))
                .findFirst();
    }

    public Optional<Character> getCharacterWithName(String baseName) {
        return rooms.stream()
                .map(room -> room.getCharacterWithName(baseName))
                .flatMap(Optional::stream) // Optional<Character> → 0 or 1 Character
                .filter(Character::isAlive)
                .findFirst();
    }

    @Override
    public <T> Set<T> getItemsOf(Class<T> type) {
        return rooms.stream()
                .flatMap(room -> room.getItemsOf(type).stream())
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public void replace(Character myself, Character newMe) {
        IMutableRoom room = getRoomContaining(myself);
        room.replace(myself, newMe);
    }

    private void addRoom(IMutableRoom room) {
        rooms.add(room);
    }

    public IMutableRoom getRoomContaining(Character character) {
        for (IMutableRoom room : rooms) {
            if (room.containsCharacter(character)) {
                return room;
            }
        }
        throw new IllegalArgumentException("Character not found in any room");
    }

    public void addToRandomRoom(Character newCharacter) {
        IMutableRoom room = getRandomRoom();
        room.addCharacter(newCharacter);
    }

    @Override
    public void addArtifact(Artifact artifact) {
        IMutableRoom room = getRandomRoom();
        room.addArtifact(artifact);
    }

    @Override
    public void removeArtifact(Artifact artifact) {
        IMutableRoom room = rooms.stream()
                .filter(candidate -> candidate.hasArtifact(artifact))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Artifact not found in any room"));
        room.removeArtifact(artifact);
    }

    @Override
    public void addCharacter(Character character) {
        addToRandomRoom(character);
    }

    @Override
    public void removeCharacter(Character character) {
        getRoomContaining(character).removeCharacter(character);
    }

    @Override
    public void addNeighbor(IMutableRoom neighbor, boolean bidirectional) {
        IMutableRoom room = getRandomRoom();
        room.addNeighbor(neighbor, bidirectional);
    }

    @Override
    public void enter(Character character, IRoom previousRoom) {
        if (previousRoom instanceof IMutableRoom mutablePreviousRoom) {
            mutablePreviousRoom.removeCharacter(character);
            addCharacter(character);
        }
    }

    public static class Builder {
        private static final String DEFAULT_MAZE_NAME = "Maze";
        static Logger logger = LoggerFactory.getLogger(Builder.class);

        private final Random rand = new Random();
        public static String[] grid2x2RoomNames = new String[]{"Northwest", "Northeast", "Southwest", "Southeast"};
        public static String[] grid3x3RoomNames = new String[]{"Northwest", "North", "Northeast", "West", "Center", "East", "Southwest", "South", "Southeast"};

        final Maze maze = new Maze();
        Map<String, IMutableRoom> roomMap = new HashMap<>();
        private Boolean distributeRandomly = true;
        private boolean useBidirectionalConnections = true;
        private int currentRoomIndex = 0;
        private final RoomFactory roomFactory;

        private Builder(RoomFactory roomFactory) {
            this(roomFactory, DEFAULT_MAZE_NAME);
        }

        private Builder(RoomFactory roomFactory, String name) {
            this.roomFactory = roomFactory;
            this.maze.name = name;
        }

        private IMutableRoom nextRoom() {
            if (distributeRandomly) {
                return getRandomRoom();
            }
            return maze.getRooms().get(currentRoomIndex++ % maze.getRooms().size());
        }

        public IMutableRoom getRandomRoom() {
            int index = rand.nextInt(maze.rooms.size());
            return maze.rooms.get(index);
        }

        public Builder createStraightLineRooms(String... roomNames) {
            return createLinearlyConnectedRooms(false, roomNames);
        }

        private Builder createLinearlyConnectedRooms(boolean makeRing, String... roomNames) {
            List<Room> rooms = new ArrayList<>();
            for (String roomName : roomNames) {
                Room room = roomFactory.createRoom(roomName);
                roomMap.put(roomName, room);
                rooms.add(room);
            }
            for (int i = 0; i < rooms.size() - 1; i++) {
                rooms.get(i).addNeighbor(rooms.get(i + 1), useBidirectionalConnections);
            }
            maze.rooms.addAll(rooms);
            return this;
        }

        public Builder createRingOfConnectedRooms(String... roomNames) {
            return createLinearlyConnectedRooms(true, roomNames);
        }

        public Builder createFullyConnectedRooms(String... roomNames) {
            List<IMutableRoom> rooms = new ArrayList<>();
            for (String roomName : roomNames) {
                Room currentRoom = roomFactory.createRoom(roomName);
                roomMap.put(currentRoom.getName(), currentRoom);
                rooms.add(currentRoom);
            }
            maze.rooms.addAll(rooms);
            connectRooms(rooms, rooms.size() - 1);
            return this;
        }

        public Builder createAGridOfRooms(int gridWidth, int gridHeight, String gridRoomNamePrefix) {
            String[] roomNames = new String[gridHeight * gridWidth];
            for (int row = 0; row < gridHeight; row++) {
                for (int column = 0; column < gridWidth; column++) {
                    roomNames[row * gridWidth + column] = gridRoomNamePrefix + row + "-" + column;
                }
            }
            return createGridOfRooms(gridHeight, gridWidth, roomNames);
        }

        public Builder createGridOfRooms(int rows, int columns, String[] roomNames) {
            IMutableRoom[][] roomGrid = new Room[rows][columns];
            List<IMutableRoom> rooms = new ArrayList<>();
            // Notice -- don't use i and j. Use row and column -- they are better
            for (int row = 0; row < rows; row++) {
                for (int column = 0; column < columns; column++) {
                    Room newRoom = roomFactory.createRoom(roomNames[row * columns + column]);
                    roomGrid[row][column] = newRoom;
                    rooms.add(newRoom);
                    roomMap.put(newRoom.getName(), newRoom);
                }
            }
            maze.rooms.addAll(rooms);

            // Now connect the rooms
            for (int row = 0; row < rows; row++) {
                for (int column = 0; column < columns; column++) {
                    IMutableRoom currentRoom = roomGrid[row][column];
                    IMutableRoom neighbor;
                    if (row > 0) {
                        neighbor = roomGrid[row - 1][column];
                        currentRoom.addNeighbor(neighbor, useBidirectionalConnections);
                    }
                    if (column > 0) {
                        neighbor = roomGrid[row][column - 1];
                        currentRoom.addNeighbor(neighbor, useBidirectionalConnections);
                    }
                }
            }
            return this;
        }

        public Builder create2x2Grid() {
            return createGridOfRooms(2, 2, grid2x2RoomNames);
        }

        public Builder create3x3Grid() {
            return createGridOfRooms(3, 3, grid3x3RoomNames);
        }

        public Builder createFullyConnectedRooms(List<String> roomNames) {
            maze.rooms = new ArrayList<>();
            for (String roomName : roomNames) {
                Room currentRoom = roomFactory.createRoom(roomName);
                roomMap.put(currentRoom.getName(), currentRoom);
                maze.rooms.add(currentRoom);
            }
            fullyConnectRooms();
            return this;
        }

        public Builder createRooms(List<String> roomNames) {
            maze.rooms = new ArrayList<>();
            for (String roomName : roomNames) {
                Room currentRoom = roomFactory.createRoom(roomName);
                roomMap.put(currentRoom.getName(), currentRoom);
                maze.rooms.add(currentRoom);
            }
            return this;
        }

        public Builder fullyConnectRooms() {
            for (IMutableRoom room : maze.rooms) {
                for (IMutableRoom otherRoom : maze.rooms) {
                    if (!room.equals(otherRoom)) {
                        room.addNeighbor(otherRoom, useBidirectionalConnections);
                    }
                }
            }
            return this;
        }

        public Builder addRoomIfNotAlreadyInMaze(String roomName) {
            return addRoomIfNotAlreadyInMaze(roomName, "Standard");
        }

        public Builder addRoom(IMutableRoom room) {
            roomMap.put(room.getName(), room);
            maze.addRoom(room);
            return this;
        }

        public Builder addRoomIfNotAlreadyInMaze(String roomName, String roomType) {
            if (!roomMap.containsKey(roomName)) {
                Room room = switch (roomType) {
                    case "Standard" -> roomFactory.createRoom(roomName);
                    case "Pantry" -> roomFactory.createPantry(roomName);
                    case "Portal" -> roomFactory.createPortal(roomName);
                    default -> throw new IllegalArgumentException("Invalid room type: " + roomType);
                };
                roomMap.put(roomName, room);
                maze.addRoom(room);
            }
            return this;
        }

        public Builder addNeighborToRoom(String roomName, String neighborName) {
            IMutableRoom room = roomMap.get(roomName);
            IMutableRoom neighbor = roomMap.get(neighborName);
            room.addNeighbor(neighbor, useBidirectionalConnections);
            return this;
        }

        public Builder createConnectedRooms(Integer minConnections, Integer numRooms) {
            createRooms(numRooms);
            connectRooms(minConnections);
            return this;
        }

        public Builder connectRooms(Integer minConnections) {
            return connectRooms(maze.rooms, minConnections);
        }

        public Builder connectRooms(List<IMutableRoom> rooms, int minConnections) {
            if (maze.size() < 2) {
                return this;
            }
            int realMinimumConnections = Math.min(minConnections, Math.max(maze.size() - 1, 1));
            for (IMutableRoom room : rooms) {
                while (room.numberOfNeighbors() < realMinimumConnections) {
                    IMutableRoom neighbor = nextRoom();
                    if (!room.equals(neighbor) && !room.hasNeighbor(neighbor)) {
                        room.addNeighbor(neighbor, useBidirectionalConnections);
                    }
                }
            }
            return this;
        }

        public Builder add(Character character) {
            nextRoom().addCharacter(character);
            return this;
        }

        public Builder addCharacters(List<Character> characters) {
            for (Character character : characters) {
                nextRoom().addCharacter(character);
            }
            return this;
        }

        public Builder addArtifacts(List<Artifact> artifacts) {
            for (Artifact artifact : artifacts) {
                nextRoom().addArtifact(artifact);
            }
            return this;
        }

        public Builder addToSameRoom(Character character, Artifact artifact) {
            IMutableRoom room = nextRoom();
            room.addCharacter(character);
            room.addArtifact(artifact);
            return this;
        }

        public Maze build() {
            if (maze.size() == 0) {
                logger.warn("No rooms created. Creating a single room.");
                addRoomIfNotAlreadyInMaze("Only Room");
            }
            for (IMutableRoom room : maze.rooms) {
                if (room.numberOfNeighbors() == 0 && maze.rooms.size() > 1) {
                    logger.warn("Room {} has no neighbors. Connecting it to another room.", room.getName());
                    room.addNeighbor(nextRoom(), useBidirectionalConnections);
                }
            }
            return maze;
        }

        public Builder addToRoom(String roomName, Character character) {
            IMutableRoom room = getRoom(roomName);
            room.addCharacter(character);
            return this;
        }

        public Builder addToRoom(String roomName, Artifact artifact) {
            IMutableRoom room = getRoom(roomName);
            if (room instanceof IMutableRoom mutableRoom) {
                mutableRoom.addArtifact(artifact);
            }
            return this;
        }

        public IMutableRoom getRoom(String roomName) {
            return roomMap.get(roomName);
        }

        public Builder distributeSequentially() {
            distributeRandomly = false;
            return this;
        }

        public Builder distributeRandomly() {
            distributeRandomly = true;
            return this;
        }

        public Builder setConnectionDirection(Boolean useBidirectionalConnections) {
            this.useBidirectionalConnections = useBidirectionalConnections;
            return this;
        }

        public Builder createNRoomsWithMConnections(int numberOfRooms, int numberOfConnections) {
            createConnectedRooms(numberOfConnections, numberOfRooms);
            return this;
        }

        private Builder createRooms(int numberOfRooms) {
            maze.rooms = new ArrayList<>();
            IntStream.range(0, numberOfRooms).forEach(_ -> {
                Room currentRoom = roomFactory.createRoom();
                roomMap.put(currentRoom.getName(), currentRoom);
                maze.rooms.add(currentRoom);
            });
            return this;
        }

        public List<IMutableRoom> getRooms() {
            return List.copyOf(maze.rooms);
        }
    }
}