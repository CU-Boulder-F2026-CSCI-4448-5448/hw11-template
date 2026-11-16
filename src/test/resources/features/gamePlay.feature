Feature: Playing Complete Games
  As a game player
  I want to play a game with a variety of characters
  So that I can see how they interact with each other


  Scenario: Play the simplest game
    Given a maze with the following attributes:
      | Rooms     | 1 |
      | Knights   | 1 |
      | Creatures | 1 |

    When the game is played in the created maze

    Then the game is over
    And all the adventurers or all of the creatures have died


  Scenario: Play a complicated game
    Given a maze with the following attributes:
      | Rooms     | 7  |
      | Food      | 20 |
      | Knights   | 4  |
      | Gluttons  | 3  |
      | Cowards   | 1  |
      | Creatures | 5  |
      | Demons    | 2  |
      | Seekers   | 2  |
      | Treasures | 1  |


    When the game is played in the created maze

    Then the game is over
    And all the adventurers or all of the creatures have died


  Scenario: Fighting
    Given a maze with the following rooms:
      | Starting |
    Given a knight Bilbo with health 5 and a fixed die roll of 4
    And Bilbo is in the Starting room
    And a creature Goblin with health 3 and a fixed die roll of 2
    And Goblin is in the Starting room

    When Bilbo plays a turn

    Then Bilbo has health 4.45
    And Goblin has health 0.5


  Scenario: Play a game in a unidirectional, randomly-distributed maze
    Given a unidirectional, randomly-distributed maze with the following attributes:
      | Rooms       | 11 |
      | Connections | 3  |
      | Food        | 10 |
      | Knights     | 3  |
      | Gluttons    | 2  |
      | Cowards     | 1  |
      | Creatures   | 5  |
      | Demons      | 2  |

    When the game is played in the created maze

    Then the game is over
    And all the adventurers or all of the creatures have died

  Scenario: Play a game in a bidirectional, sequentially-distributed maze
    Given a bidirectional, sequentially-distributed maze with the following attributes:
      | Rooms       | 11 |
      | Connections | 3  |
      | Food        | 10 |
      | Knights     | 3  |
      | Gluttons    | 2  |
      | Cowards     | 1  |
      | Creatures   | 5  |
      | Demons      | 2  |

    When the game is played in the created maze

    Then the game is over
    And all the adventurers or all of the creatures have died
