Great stuff. This was my solution:
```python
def count_files(file_tree):
    count = 0
    for name, type_ in file_tree.items():
        if type_ == None:
            count += 1
        else:
            count += count_files(type_)
    return count

print(count_files(file_tree))
```

---

For this last challenge, you won't be getting any guidance... (Google is always available)

Figure out, across **all files**, which filetype is **most common**.

**How many** of this filetype are there?