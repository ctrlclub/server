The next task on your desk: build the **leaderboard**.
The leaderboard ranks players based on their **overall performance**, calculated by a player's stats.

Each stat is **weighted** to form a player's total score:
- Strength makes **0.5 (50%)** of the total
- Defence makes **0.3 (30%)** of the total
- Stamina makes **0.2 (20%)** of the total

So to **calculate a player's total score**, you'd do: `strength * 0.5 + defence * 0.3 + stamina * 0.2`.

Looking at the example data again:
```python
players = [
    {"username":"epic_GAMERTAG", "strength":93, "defence":24, "stamina":30},
    {"username":"i_am_weak", "strength":5, "defence":5, "stamina":20},
    {"username":"aaaaaaaa", "strength":45, "defence":38, "stamina":54}
]
```
In this case, `epic_GAMERTAG`'s score would be `93 * 0.5 + 24 * 0.3 + 30 * 0.2 = 59.7`.

Using the dataset from **Part 1**, which player has the **highest** score?