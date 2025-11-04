### The Golf Club
The owner of a golfing club wants to automatically keep track of all members using a new computer system. She has all the names stored in a file:
```md+copy
%file%
```
Copy the file contents, and save it to your local area.

A new member called `%name%` has just joined the club.
Use python to append this new name to the end of the file.
::: hint
https://www.w3schools.com/python/python_file_write.asp

You will want:
```python
with open("myfile.txt", "a") as file:
    # here, the word file allows you to make edits to the file
    file.write("Appending content")
```
:::

Paste the output of your new file below (it will squish into one-line, don't worry)