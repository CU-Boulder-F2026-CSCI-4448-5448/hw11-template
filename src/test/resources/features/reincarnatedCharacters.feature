Feature: Games play with reincarnated characters
  As a game player
  I want to play a game with a potions that reincarnate in the maze
  So that I have a richer set of choices


  Scenario: Potions are available in the maze and drank up by characters so that they can reincarnate
    Given a maze with the following rooms:
      | Starting |
      | Other    |
    And a glutton Galahad with health 3 and a fixed die roll of 1
    And Galahad is in the Starting room
    And the Starting room contains the following artifacts:
      | Type   | Name           | Strength | Moving Cost |
      | Potion | elixir of life | 3.5      | 0.0         |
      | Food   | cupcake        | 1.0      | 0.10        |

    When Galahad plays a turn
    Then there is no Potion in Starting room

    Given a demon Satan with health 10 and a fixed die roll of 8
    And Satan is in the Starting room

    When Satan plays a turn
    Then a death occurred
    And Galahad has health 3.5
    # This is because Galahad reincarnates with the health of the elixir