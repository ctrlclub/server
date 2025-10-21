## Dictionary recap

You’ve been hired as a **developer** for the fantasy game “Quest of Legends”.

The **game server** keeps track of all active players and their stats in a dictionary.
Every player starts as a **Rank 1**, but the game automatically promotes players to **Rank 2** if they’re strong enough to handle tougher dungeons.

To qualify as **Rank 2**, a player must satisfy the following targets:
- **Strength** must be **30** or higher
- **Defence** must be **10** or higher
- **Stamina** must be **30** or higher

You're given a list of players, and their related stats - it looks like this:
```python
players = [
    {"username":"epic_GAMERTAG", "strength":93, "defence":24, "stamina":30},
    {"username":"i_am_weak", "strength":5, "defence":5, "stamina":20},
    {"username":"aaaaaaaa", "strength":45, "defence":38, "stamina":54}
]
```
Your first job is to calculate all players who satisfy for **Rank 2**.

Here is your dataset:
```python+copy
players = %players%
```

To answer this challenge, find **the number of Rank II** players.