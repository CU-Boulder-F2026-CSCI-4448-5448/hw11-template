Feature: Games play with armored characters
  As a game player
  I want to play a game with a armor in the maze
  So that I have a richer set of choices

  Scenario: Armored suits are available in the maze and worn by knights for extra protection during fights
    Given a maze with the following rooms:
      | Starting |
    And a knight Galahad with health 6 and a fixed die roll of 4
    And Galahad is in the Starting room
    And the Starting room contains the following artifacts:
      | Type  | Name    | Strength | Moving Cost |
      | Armor | iron    | 2.0      | 0.3         |
      | Food  | cupcake | 1.0      | 0.0         |

    When Galahad plays a turn

    Then there is no Armor in Starting room

    Given a demon Satan with health 6 and a fixed die roll of 6
    And Satan is in the Starting room
    When Galahad plays a turn

    Then a fight occurred
    And Galahad has been in a fight
    And Satan has been in a fight


  Scenario: Armored suits are heavy and incur extra health costs when moving
    Given a maze with the following rooms:
      | Starting |
      | Other    |
    And a knight named Galahad in the Starting room
    And the Starting room contains the following artifacts:
      | Type  | Name | Strength | Moving Cost |
      | Armor | iron | 2.0      | 0.3         |

    When Galahad plays a turn

    Then Galahad picked up the Armor
    And there is no Armor in Starting room

    When Galahad plays a turn

    Then Galahad is now in the Other room
    And Galahad lost the default-moving cost plus 0.3 in health


  Scenario: Knight wears two suits of armor to get even more protection
    Given a maze with the following rooms:
      | Starting |
    And a knight Galahad with health 6 and a fixed die roll of 1
    And Galahad is in the Starting room
    And the Starting room contains the following artifacts:
      | Type  | Name    | Strength | Moving Cost |
      | Armor | iron    | 2.0      | 0.3         |
      | Armor | mithril | 3.0      | 0.1         |

    When Galahad plays a turn

    Then Galahad picked up the Armor

    When Galahad plays a turn
    # Now Galahad is wearing two suits of armor
    Then there is no Armor in Starting room

    Given a demon Satan with health 10 and a fixed die roll of 6
    And Satan is in the Starting room
    When Galahad plays a turn

    Then a fight occurred

    # Despite losing the fight by 5, the two armored suits prevents the loss because
    # their total strength is 5.
    And Galahad has been in a fight
    And Satan has been in a fight
    And Galahad has lost the default fight cost in health plus the aging cost of 3 turns
