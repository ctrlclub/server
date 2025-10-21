## Fix the bugs!
In this challenge, you will be fixing two types of bugs:
- Syntax Errors
- Logic Errors

## Bank.AI
A new startup called **Bank.AI** has launched their first piece of production code, however it is riddled with errors because they used AI instead of hiring developers.
Your task? **Fixing their code!** First, lets begin with getting the code to execute without errors.

Copy and paste the code below into your code editor - as a team, can you fix all the **syntax errors**?

```python+copy
bank_dictionary = {
    "James": 0,
    "Craig": 123,
    "Adam": 500,
    "Josh": 150,
    "Henry": 67
}

# Add a % interest to every account
add_interest(bank, interest):
    for account, balance in bank.items():
        balance += balance * interest

# Find the account with the most value
def get_richest(bank):
    largest_balance = 0
    largest_account = None
    for account, balance in bank.items():
        if balance > largest_balance:
            largest_account = account
            largest_balance = balance
    print(f"The richest person is {largest_account} with £{largest_balance}.")

# Transfer money from sender to reciever
def transfer(bank, sender, receiver, amount)
    bank[sender] -= amount
    bank[reciever] += bank[sender] - amount

print("Craig has £{bank_dictionary['Craig']}")
print(f"James has £{bank_dictionary['James']}")

# Transfer 50 from Craig to James
print("Craig is transferring £50 to James...")
transfer(bank_dictionary, "Craig", "James")

print(f"Craig has £{bank_dictionary['Craig']}")
print(f"James has £{bank_dictionary['James']}")

# Add 5% interest to each account
add_interest(bank_dictionary, 0.05)

# Display richest person
get_richest(bank_dictionary)
```
For your answer, **copy and paste** the output of the **as soon as it is able to run without errors**.
*The output will squish into one line in the answer box - that's okay.*

::: hint
Run the code and see what output you get, Python will tell you where all the errors are.
:::

::: hint
Types of error:
- missing 'def'
- spelling mistake
- missing argument to function
:::