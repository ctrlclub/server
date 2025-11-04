**Wonderful!**

Hopefully your code looks something like this:
```python
count = 0
for name, type_ in file_tree.items():
    if type_ == None:
        count += 1
print(count)
```

---

Now, we can very easily adjust this code to act **recursively**, and count **all files** in the directory and all subdirectories.

To start, take your existing code to count files, and use it to define a new `count_files` function:
- For the parameters, it should accept a map as input. This map must be a file tree.
- It should return the total number of files it counts.
 
```python
def count_files(file_tree):
    ...
    return count
```

By wrapping the **logic from Part 3** in a **function**, we can have this function call itself for all subdirectories.

---

**Now, we must modify the code slightly to handle subdirectories.**

If the `type_ != None`, we have a subdirectory. We can call our `count_files` on this subdirectory, and add the total to the count.

::: hint
We should still count files as normally, but if we encounter a subdirectory we recursively call our new `count_files` function, and pass in our subdirectory.
```python
if type_ == None:
    count += 1
else:
    count += count_files(type_)
```
:::

---

Your challenge is to modify the existing code to act recursively, and to count all the files in your dataset. How many files are there?

