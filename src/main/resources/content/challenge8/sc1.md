Wonderful. So let's process some data.

---

You'll be given a **list of logs** like this:
```markdown
404 - 86.16.145.230
200 - 1.1.1.1
403 - 1.1.1.1
200 - 86.16.145.230
401 - 8.43.1.50
```
This is a list of **IPs**, and their **connection status** to our website.

Your **initial task** is to convert this log list into a dictionary, of **IPs** and their **associated HTTPCode**'s like this:
```python
{
	"86.16.145.230": [HTTPCode.NOT_FOUND, HTTPCode.OK],
	"1.1.1.1": [HTTPCode.OK, HTTPCode.FORBIDDEN],
	"8.43.1.50": [HTTPCode.UNAUTHORIZED]
}
```

Here is your dataset:
```python+copy
log_array = [%logs%]
```

::: hint
Let's break this into subproblems:
1. Create a **dictionary** with all the IPs as keys, and empty arrays as values.
2. Then, loop through your dataset line-by-line;
3. Split each string by the `-` and evaluate the HTTPCode into an object as we show above. Add this to the corresponding IP in the map, as shown by the example result above.
:::

You can confirm if you got the correct answer by **printing your dictionary** - the output should be relatively readable.

I won't check you get this right - **you'll need it for the next challenge** though so you should probably do it. **Press "Submit" to continue.**