Recently, your fellow game developers have **added armour** into the game.

Each armour piece **increases** a player's defence by a fixed amount.
- `helmet` (+10 defence)
- `chestplate` (+15 defence)
- `gauntlets` (+5 defence)
- `shield` (+15 defence)

The player dataset has been updated with a new attribute called armour, which lists all the armour pieces a player owns:
```python
players = [
    {"username":"epic_GAMERTAG", "strength":93, "defence":24, "stamina":30, armour:["chestplate", "helmet"]},
    {"username":"i_am_weak", "strength":5, "defence":5, "stamina":20, armour:[]},
    {"username":"aaaaaaaa", "strength":45, "defence":38, "stamina":54, armour:["shield"]}
]
```
A player can have **no armour** or **up to 4 pieces** of armour (if they have all the armour types).

To calculate a player's **final defence**, you must take their **initial defence** and add all the subsequent armour piece bonuses.
For example, `epic_GAMERTAG` has the initial `24` defence, plus `15` and `10` for the chestplate and helmet respectively. Therefore `epic_GAMERTAG`'s **final defence** is `24 + 15 + 10 = 49`.

Here is your updated dataset:
```python+copy
players = %players%
```

Which player has the highest **final defence**?