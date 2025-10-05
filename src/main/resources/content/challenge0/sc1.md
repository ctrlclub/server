Great job! You've just learned how `range(start, end)` controls **how many times** a loop iterates.

Now, how can we use this in practice?
Commonly, we will want to loop through lists.
As you know, we use the **index-operator** to access an item in the list.

Take the example below:
```python
fruits = ["Apple", "Banana", "Cherry"]

print(fruits[0]) # This will print Apple
print(fruits[3]) # This will throw an error! Don't forget, list indexes start at 0 - there's no item at index 3
print(fruits[2]) # This will print Cherry
```

We can **combine** loops and this index-operator to **print all the items in the list**:
```python
fruits = ["Apple", "Banana", "Cherry", "Desert Quandong"] # a "desert quandong" is a fruit according to chatgpt lol
number_of_fruits = len(fruits)

for index in range(0, number_of_fruits): 
    print(fruits[index])
```
In this code, we define a `number_of_fruits` variable - it's value is the length of the fruits list.
This means the loop will always iterate for as many items there are in the list.

In the example above, what is the value of `number_of_fruits`?
