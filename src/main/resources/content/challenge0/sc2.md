
For the last challenge, you may have used a loop!
It's much easier to use the **sum** function, for example:
```python
numbers = [1, 2, 3]
print(sum(numbers)) # this will print 6
```



The manager doesn't seem satisfied. She's worried production levels aren't consistent across days... To help her do further analysis, she wants you to **sum** the production numbers for **every second day**.

::: hint
It may be hard to use `sum()` here.
We'd recommend a `for` loop - using the `range()` function.
```python
for index in range(0, len(numbers)):
    # any code here will repeat n times, where n = the number of numbers
```
Check the index is positive - if it is, add it to a total.
:::

