### Sorting it out.
NOTE: The rest of the challenges are based on the file you uploaded for your last answer.

The owner wants to keep her member list more organised by having it in alphabetical order. Your task:
- Read the file
- Sort the names alphabetically **by surname**
- Overwrite the file with the new, sorted names

Copy and paste the final file into the answer box.

::: hint
This code will sort a list of names into alphabetical order:
```python
# Just an example, don't use these names...
names = ["John Smith", "Alice Johnson", "Bob Brown", "Charlie McDonald"]

# Sort by surname (last word)
sorted_names = sorted(names, key=lambda name: name.split()[-1])
```
>
:::