
A research team has gone missing on a tropical island 🌴, after a storm.
Your task is to use **satellite data** (your dataset) to help rescue the team, and locate the **two** missing explorers!


## Part 1 - who to save first?

Your first task is to determine **which explorer** is the easiest to save; this will be the explorer on the **lowest** terrain height.

---

Take the following example... You have the following explorer data; it shows their name, coordinate, and food level respectively:
```python
explorers = [
    ("Alpha", (1, 2), 9),
    ("Bravo", (2, 0), 5)
]
```
And you are given this height-map:
```python
map = [
    [0, 1, 5],
    [9, 5, 4],
    [8, 3, 0]
]
```

To read the map, you must understand that `(0, 0)` is the **top-left**. Adding `x` moves to the right, and adding `y` moves downwards.

First off, for Alpha we can see his coordinates are `(1, 2)`. Looking in the map, that has a height of `3`.
For Bravo, we can see his coordinates are `(2, 0)`. Looking in the map, that has a height of `5`.

Therefore, **Alpha** is easiest to save - he's on **lower ground**!


---


This is the data we have on the explorers right now:
```python+copy
explorer_data = %explorer_data%
```

And this is the heightmap in their area:
```python+copy
map = %island_map%
```

So, you figure it out... which explorer is **easiest** to save?

::: hint
To do this, iterate over the explorers with a for-loop. Find the terrain height at their location, and if it's the lowest so far, record their name and the new lowest.
:::