Wonderful!

So, we've just looked at **range-based** `for` loops, where the iteration count (or loop count) is defined by a **range**.
We used this knowledge to print a list of fruits - now's a good time to tell you there *is* an easier way...

Let's look at **for-each** loops - arguably the more-commonly used of the two.
The **for-each** loop is used exclusively to **loop over lists**. It's easier to show this with an example, so take a look below:

```python
fruits = ["Apple", "Banana", "Cherry", "Desert Quandong"]

for fruit in fruits:
    print(fruit)
```

In this example, we don't have `index` as our variable anymore - instead we have `fruit` directly. As the code loops, the value of `fruit` takes each element from the `fruits` list, one by one.
This style of loop is **super easy to use,** and avoids all the `index` shenanigans we faced previously when trying to loop over a list.

That's not to say this loop is just for strings though... it can also be used for a list of numbers:
```python
days_per_month = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31]
days_in_year = 0

for days in days_per_month:
    days_in_year = days_in_year + days
    
print(days_in_year) # This will print 365
```
In this example, we loop over the list of days in each month:
- `days_in_year` starts at zero. As the loop progresses, the value increments upwards towards 365
- The `days` variable in the loop is 31 on the first iteration, 28 on the second, 31 on the third...
- `days_in_year = days_in_year + days` adds the `days` variable to the `days_in_year` variable
- Finally we print the output

If you want to confuse yourself, look at the **range-based** counterpart in this hint... it's not very nice is it?
::: hint
```python
days_per_month = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31]
days_in_year = 0
months_in_year = len(days_per_month)

for month_index in range(0, months_in_year):
    days_in_year = days_in_year + days_per_month[month_index]
    
print(days_in_year) # This will print 365
```
:::

Aaaaaand that's pretty much **all there is** to loops. Not much, eh?
I hope you found them simple - and if you didn't, **don't worry**. Most of the following challenges will require loops, which will give you solid practice to get familiar. Why do you think I taught you them first? 😉

As a final send-off, use your knowledge to sum this list for the answer to this challenge:
```python+copy
numbers = [4825, 8783, 1804, 820, 3913, 438, 7761, 210, 3079, 37787]
```


