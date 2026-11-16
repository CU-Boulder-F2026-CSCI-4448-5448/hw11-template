Feature: Games play with weaponized characters
  As a game player
  I want to play a game with a weapons in the maze
  So that I have a richer set of choices


  Scenario: Weapons are available in the maze and picked up by knights for extra protection during fights
    Given a maze with the following rooms:
      | Starting |
    And a knight Galahad with health 5 and a fixed die roll of 4
    And Galahad is in the Starting room
    And the Starting room contains the following artifacts:
      | Type   | Name    | Strength | Moving Cost |
      | Weapon | sword   | 1.0      | 0.3         |
      | Food   | cupcake | 1.0      | 0.10        |

    When Galahad plays a turn

    And there is no Weapon in Starting room

    And a demon Satan with health 10 and a fixed die roll of 2
    And Satan is in the Starting room
    When Galahad plays a turn
    Then a fight occurred

    # The sword's attack value of 1, makes the damage inflicted 3 instead of 2, plus standard
    # fighting loss of 0.5
    And Satan has health 6.5