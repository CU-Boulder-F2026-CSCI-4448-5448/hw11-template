Feature: Treating an entire maze as a single room in another maze
  As a game player
  I want to play games mazes within mazes
  So that I can see how interesting I can make the game

  Scenario: A maze with a portal to a second level
    # Make a game that requires a key to open a portal to
    # a new maze (one way connection to the new maze)

    Given a Level2 maze with the following rooms, types, and neighbors:
      | Room       | RoomType | Neighbors              |
      | L2R1       | Pantry   | L2R2                   |
      | L2R2       | Standard | L2R3, L2R4, L2Treasure |
      | L2Treasure | Standard |                        |
    And a guard BlackKnight with health 2.0 and a fixed die roll of 2
    And the Level2 maze has the following characters in it:
      | Character   | Room       |
      | BlackKnight | L2Treasure |
    And the L2Treasure room contains the following artifacts:
      | Name       | Type     | Strength | Moving Cost |
      | Holy Grail | Treasure | 0        | 0           |

    And a Level1 maze with the following rooms, types, and neighbors:
      | Room     | RoomType | Neighbors |
      | A        | Pantry   |           |
      | Starting | Portal   | A, B      |
      | B        | Standard | C, D      |
      | Map      | Standard | D         |
      | Level2   | Maze     | D         |
      | KeyRoom  | Standard | B         |
    And the Level1 maze has the following artifact counts:
      | Food      | 20 |
      | Creatures | 5  |
    And a seeker Seeker with health 10.0 and a fixed die roll of 6
    And the Level1 maze has the following characters in it:
      | Character | Room     |
      | Seeker    | Starting |

    When the game is played in the Level1 maze where the treasure must be found to win

    Then the game is over




