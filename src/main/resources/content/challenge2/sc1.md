
## Part 2 - locate the lost explorers...

Wonderful! You found %lowest_explorer% is **easiest** to save - you'll try save him if you get a chance.

Your next task is to find the **two lost explorers**, who have lost radio signals because of the storm.
The two lost explorers are stranded at the **highest** and **lowest** points on the island.

Once you find their coordinates, **add the components** to get your final answer.

For example, take the map:
```python
island_map = [
    [1, 2, 0]
    [3, 2, 9],
    [8, 1, 5],
]
```
The lowest point is at `(2, 0)` and the highest point is at `(2, 1)`.
Therefore, **adding** `2 + 0 + 2 + 1 = 5`, making `5` your final answer.

::: hint
Using a **for-loop**, find the highest and lowest points on the map.
If you're struggling, find the highest and the lowest in two separate loops.

e.g.
Loop through each **unique combination** of `(x, y)`, if the height is lower than the current lowest, store the new lowest height and the corresponding coordinates.
:::

Can you help us find the lost explorers?