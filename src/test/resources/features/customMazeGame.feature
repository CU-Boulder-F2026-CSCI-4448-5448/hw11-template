Feature: How seekers and coward search a complex maze
  As a game player
  I want to see how seekers do in a complicated maze to search
  So that I can see how interesting I can make the game when I build it, versus random

  Scenario: A seeker searches a complicated Oct11_1 maze and finds the treasure
    Given a maze with the following rooms, types, and neighbors:
      | Room     | RoomType | Neighbors        |
      | F        | Pantry   |                  |
      | Starting | Portal   | B, C, D, E, F, G |
      | I        | Standard | J, K, L, M       |
      | C        | Standard | J                |
      | Map      | Standard | F                |
      | N        | Pantry   | L                |
      | O        | Standard | L                |
      | P        | Standard | O                |
      | Treasure | Standard | N                |

    And the maze has the following artifact counts:
      | Food      | 20 |
      | Creatures | 5  |

    And a seeker Seeker with health 10.0 and a fixed die roll of 6
    And Seeker is in the Starting room
    And a guard BlackKnight with health 2.0 and a fixed die roll of 2
    And BlackKnight is in the Treasure room

    And the Map room contains the following artifacts:
      | Type         | Name       | Strength |
      | Treasure Map | MapToChest | 0.0      |

    And the Treasure room contains the following artifacts:
      | Type     | Name  | Strength |
      | Treasure | Chest | 0.0      |

    When the game is played in the created maze with the following observers:
      | None |
    Then the game is over
    And Seeker has found the treasure or all creatures or all the adventurers have died


  Scenario: A coward finds the map, becomes a seeker, and finds the treasure
    Given a maze with the following rooms and neighbors:
      | Room     | Neighbors                |
      | Starting | B, C, MapRoom1, MapRoom2 |
      | MapRoom3 | J, K, L, M               |
      | C        | J                        |
      | L        | N, O                     |
      | P        | O                        |
      | Treasure | N                        |

    And the maze has the following attribute counts:
      | Food      | 20 |
      | Creatures | 5  |

    And a coward FraidyCat with health 10.0 and a fixed die roll of 6
    And FraidyCat is in the Starting room
    And a guard BlackKnight with health 5.0 and a fixed die roll of 4
    And BlackKnight is in the Treasure room

    And the MapRoom1 room contains the following artifacts:
      | Type         | Name       | Strength |
      | Treasure Map | MapToChest | 0.0      |
    And the MapRoom2 room contains the following artifacts:
      | Type         | Name       | Strength |
      | Treasure Map | MapToChest | 0.0      |
    And the MapRoom3 room contains the following artifacts:
      | Type         | Name       | Strength |
      | Treasure Map | MapToChest | 0.0      |

    And the Treasure room contains the following artifacts:
      | Type     | Name  | Strength |
      | Treasure | Chest | 0.0      |

    When the game is played in the created maze with the following observers:
      | None |

    Then the game is over
    And FraidyCat has found the treasure or all creatures or all the adventurers have died


