Wonderful.
So hopefully **recursive functions** make sense. Please ask us if you want any more of an explanation 😀

---

## Your first real challenge
Below is the factorial function we spoke about earlier. It has two major errors (hint: one is causing a **stack overflow**).
```python
def factorial(n):
    if n == 0:
        return 0
    else:
        return n * factorial(n)
 
print(factorial(20))
```

Fix these two logical mistakes, and for your answer calculate `factorial(20)`.
