As mentioned previously, many algorithms have both an **iterative solution** (e.g. for-loops) and a **recursive solution**. Typically, it's **easier and safer** to use an iterative solution, because loops are simpler to implement and avoid issues like stack-overflows.

However, one unavoidable case of recursion is the filesystem. In OneDrive for example, every **directory** (folder) either contains **files**, or **more directories**.
If you need to perform code on each folder, the function to do this can call itself on all subfolders.

---
## Recursion in File Systems

Your dataset for this challenge will be a filesystem, like this:
```json
file_tree = {
    "RecursionNotes.docx": None,
    "RandomStuff": {
        "Homework.zip": None,
        "IllegalRecipes": {
            "PineapplePizza.docx": None,
            "MangoMaki.docx": None,
        }
    },
    "SalaryTracker.xlsx": None
}
```
Each **directory** is an additional **dictionary**.
Each **file** has the type `None`.

Now, before we do anything recursive, let's recap iterating over a dictionary. Try it from knowledge or Google it first... if that's no help, feel free to use the hint.

::: hint
You can't iterate it like a typical list, as each entry, etc `"RecursionNotes.docx": None,` has two values: the string and the `None` value.

To iterate over a map, you call the `.items()` function on the dictionary like this:
```python
for key, value in file_tree.items():
    print(key + " " + str(value))
```
This allows you to iterate both the key and the value at once; you can access the key and value by the variables in the loop.
The example above would print:
```
RecursionNotes.docx None
RandomStuff {'Homework.zip': None, 'IllegalRecipes': {'PineapplePizza.docx': None, 'MangoMaki.docx': None}}
SalaryTracker.xlsx None
```
>
:::

Okay, so here is your big dataset:
```python+copy
file_tree = %tree%
```

Your first challenge is to count how many files (non-recursively) are in the **top-level** folder of the `file_tree`.
::: hint
Loop through the file tree, and count the number of `None` types you can find. Ignore anything that is not `None`.
:::