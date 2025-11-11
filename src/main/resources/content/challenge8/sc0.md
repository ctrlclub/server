Now you know the basics of **enums**, let's put your knowledge to practice.

To start, we have an enum which needs a **couple extra states**:
```python+copy
# copy me into your code editor!

from enum import Enum

class HTTPCode(Enum):
    UNKNOWN = 0
    OK = 200
    UNAUTHORIZED = 401
    NOT_FOUND = 404
```

To start, please **add** `FORBIDDEN` as number `403`, and `METHOD_NOT_ALLOWED` as `405`.

Once you have done this, type `test = HTTPCode.` and wait. Does the editor show the **new states** you've created?

Finally, `print( HTTPCode(200) )`. What do you see? Hopefully `HTTPCode.OK`.
Inside the `print()` function, we transform the integer `200` into the corresponding enum. Here are more examples:
```python
HTTPCode(200) # -> HTTPCode.OK
HTTPCode(401) # -> HTTPCode.UNAUTHORIZED
HTTPCode(543543) # -> throws a ValueError (the enum doesnt exist)
HTTPCode(200).name # -> "OK" (this returns the string you defined)
```

Press "Submit" once this is all working, or ask us for help!