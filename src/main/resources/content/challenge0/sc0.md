Welcome to the CTRL Room!

Before you start on any real challenges, let's make sure we have the **basics** covered.
Importantly, let's learn about the `for` loop.

Simply, a `for` loop **repeats a block of code** a **set** number of times.

Let's look at this example below:
```python
for index in range(0, 5):
    print(index)
```
It might look tricky at first! Let's break down what's going on:
- The `for` directive tells Python we want a **loop**
- Next, `index` is a **variable** we define
  - In this scenario, represents **how many times the loop has looped**, so far
  - **It can be used within the loop** - we've done this here, by printing it
  - We can actually call `index` anything we want
- `in range(0, 5)` tells the Python interpreter we want to loop **5 times**
- Any code indented below will be looped

Any guesses as to what this code will do? Click on the hint below to reveal!

::: hint
This code will repeatedly print `index`, so it will count from 0 -> 4:
```python
Output:
0
1
2
3
4
```

Remember when we used `range(0, 5)`?
Well, the output hasn't printed up to 5 as you can see - it's only **printed up to 4**.
The `range(start, end)` includes the start number, but excludes the end number.
:::

How many times will `range(2, 9)` loop?
