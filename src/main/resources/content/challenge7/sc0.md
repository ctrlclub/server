## Recursion
A **recursive function** is a function which **calls itself**. That's it.

Often, a **single algorithm** could have **both** an iterative (loop-based) and recursive implementation. *It's easier to write, understand and debug iterative solutions, so for now I'd always recommend them. However, this is a recursive challenge.*

This is a basic recursive function to generate a **factorial** (1 * 2 * 3 * ... * n):

```python
def factorial(n):
    if n == 1:
        return 1 # the base case
    else:
        return n * factorial(n - 1) # the recursion
```

It has **two** distinct features:
- The recursive call, where **the function calls itself**
- It's **base case**, whereby the recursive call does **NOT** call itself, and the functions unwind

---
## Code Walkthrough
Let's trace:
```python
factorial(3)
```

Tracing the function goes like this:
> The function lets n = 3
> 3 == 1 is False
> return 3 * factorial(2)

So as you can see, we return `factorial(2)`:
> The function lets n = 2
> 2 == 1 is False
> return 2 * factorial(1)

And for `factorial(1)`:
> The function lets n = 1
> 1 == 1 is True
> return 1

From this point the values all return or "unwind", causing 1 * 2 * 3 to be calculated.
 
---
## Your first challenge isn't actually challenge...
```python+copy
def factorial(n):
    if n == 1:
        print("Returning 1 << BASE CASE!")
        return 1
    else:
        print("Calculating " + str(n - 1) + " factorial")
        factorial_n_minus = factorial(n - 1)
        print("Returning " + str(n * factorial_n_minus))
        return n * factorial_n_minus
 
print(factorial(10))
```
Paste the code in your editor, and view the output.
Can you see the function stack building as the calculations grow, and finally it all unwrap as your reach the base case?

Enter "yes" to move onto the next challenge.

