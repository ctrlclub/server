## Context
Scientists have recently discovered a new underground floodplain deep beneath the mountains, and they've gathered a bit of data for your team to analyse.

The data you received is a **2D grid of integers**, where each value represents the **current water level** in that cell. Each value is between **100 (max water)** and **0 (min water)**.

Take the example:
```python
# purely example data
water_level = [
    [100, 23, 12],
    [83, 72, 56],
    [20, 1, 0],
]
```
In this example, the water is highest in the left-most of the grid, `(0, 0)`. There is **no** water in the right-most of the grid, `(2, 2)`.

---
Now that you understand the data, your team’s first task is simple:

## Part 1 — Total Water
Calculate **the total amount of water** in the floodplain, by **summing** all the values in the floodplain grid.

Your answer should be a **single integer**. Have a few team members compute the total water - this way, you can compare your results.

Your dataset is below:
```python+copy
water_level = %map%
```