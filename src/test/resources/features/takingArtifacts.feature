Feature: Taking artifacts from the Maze
  I want to see how adventurers do in a complicated maze to search
  So that I can see how interesting I can make the game when I build it, versus random

  Scenario: An adventurer discovers a die and swaps it.
    Given a maze with the following rooms:
      | Starting |

    And the maze has the following attribute counts:
      | Dice | 1 |

    And a knight Bilbo with health 5.0 and a fixed die roll of 6
    And Bilbo is in the Starting room

    When Bilbo plays a turn
    And Bilbo now has a random die

  Scenario: An adventurer discovers a fixed die and swaps it with his own.
    Given a maze with the following rooms:
      | Starting |

    And a knight Bilbo with health 5.0 and a fixed die roll of 6
    And Bilbo is in the Starting room

    And the Starting room contains the following artifacts:
      | Type | Name      | Strength |
      | Die  | FixedDie1 | 1        |

    And Bilbo plays a turn
#    Now Bilbo has the fixed die with a roll of 1

    And a creature Orc with health 3.0 and a fixed die roll of 6
    And Orc is in the Starting room

    When Bilbo plays a turn
    Then Bilbo is now dead
