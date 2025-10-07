
## Part 3 - who is going to die?

**Great!** It looks like we've found two more explorers:

```python+copy
new_explorers = %new_explorers%
```

**Add this to your explorer data** - you should have 7 explorers stranded now.

Unfortunately due to sustained winds, you can't send in help.
You must determine how many explorers will make it out alive.

All explorers have **some food left** with them (this is the final number in the tuple), and they **eat one** for **each square they move**.
The explorers are all aiming to make it to the North West of the map, `(0, 0)`, but if they don't have enough food **they die**. If they have enough food to make it to `(0, 0)`, they'll survive.

How many explorers will make it out alive?

::: hint
The most efficient movement path is in two straight lines, moving to the coordinate axes `x = 0` and `y = 0`.

Under careful consideration, you can see the total squares moved is the **sum of components** of their coordinates, or simply put: `x + y`.
:::
