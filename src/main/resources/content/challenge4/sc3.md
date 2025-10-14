### Part 4 - Finding the Basins


The scientists have one final, difficult challenge for you.
They want you to **count the number** of "basins" in the 2D array.

> *In the **floodplain dataset**, a basin is any number whereby all neighbours (up, down, left and right) all have a higher value*

Take the following example data:
```python
water_level = [
    [3, 9, 8],
    [4, 2, 5],
    [4, 7, 8]
]
```

This dataset has an obvious basin in the center, value `2` at position `(1, 1)`, as values `9, 5, 7, 4` that surround it are all larger than `2`.

Notice, `3` in the top right at position `(0, 0)` is also a basin. The top and left values are disregarded, and `3` is smaller than both `4, 9`.

::: hint
A tip for you:
- Try write an algorithm to detect basins on a smaller dataset, such as the one above
- This makes it easier understand the problem
- This is also easier to debug - there is less data to work with
- Once you are confident in your algorithm on the small dataset, try run it on the bigger dataset
:::

You have two tasks:
- Write an algorithm to **find all basins**.
- Perform this algorithm on your **floodplain dataset** from **Part 1**. Calculate the total number of basins - **this is your answer**.

