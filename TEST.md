### RESTful-API TEST

#### 1 . test list out all bank accounts. 

``` curl -XGET 'http://localhost:3000/api/account' | python -m json.tool ```

GET /api/account

expected output:
```
[
    {
        "accountNumber": "a0003",
        "balance": {
            "amount": 24.6,
            "currency": "GBP"
        }
    },
    {
        "accountNumber": "a0002",
        "balance": {
            "amount": 2975.4,
            "currency": "GBP"
        }
    },
    {
        "accountNumber": "a0001",
        "balance": {
            "amount": 120,
            "currency": "GBP"
        }
    }
]
```


#### 2.1 test get a particular bank account with the accountNumbe = "a0001".

``` curl -XGET 'http://localhost:3000/api/account/a0001' | python -m json.tool ```

GET /api/account/a0001

expected output: 
```
{
    "accountNumber": "a0001",
    "balance": {
        "amount": 120,
        "currency": "GBP"
    }
}
```


#### 2.2 test get a bank account with non-exists account number="a0004".

``` curl -XGET 'http://localhost:3000/api/account/a0004' | python -m json.tool ```

GET /api/account/a0004

expected output: 
```
{
    "message": "Entity 'a0004' of BankAccount is not found",
    "code": 404
}
```


#### 3.1 test transfer money from account a0002 to a0001

```
curl --header "Content-Type: application/json" \
  --request POST \
  --data '{ "senderAccountNumber": "a0002", "receiverAccountNumber": "a0001", "currency": "GBP", "amount": "12.7", "description": "hi" }' \
	'http://localhost:3000/api/transaction' | python -m json.tool
```

POST /api/transaction

request body:
```
{
	"senderAccountNumber": "a0002",
	"receiverAccountNumber": "a0001",
	"currency": "GBP",
	"amount": "12.7",
	"description": "hi"
}
```

expected output (transactionNumber and requestedTime may be different):
```
{
    "transactionNumber": "805052874285362946",
    "requestedTime": 1536871479881,
    "senderAccountNumber": "a0002",
    "receiverAccountNumber": "a0001",
    "description": "hi",
    "status": "PENDING",
    "processedTime": null,
    "failedReason": null,
    "money": {
        "amount": 12.7,
        "currency": "GBP"
    }
}
```

#### 3.2 test transfer money more than what the sender has from account a0002 to a0001

```
curl --header "Content-Type: application/json" \
  --request POST \
  --data '{ "senderAccountNumber": "a0002", "receiverAccountNumber": "a0001", "currency": "GBP", "amount": "9999", "description": "hi" }' \
	'http://localhost:3000/api/transaction' | python -m json.tool
```

request body:
just change the amount from 12.7 to 9999 from the request body in test-case#3.1

expected output:
```
{
    "message": "money subtracted to negative. src:2987.3, other:9999",
    "code": 400
}
```

#### 3.3 test transfer money more than what the sender has from account a0002 to non-exists bank account with account number="a0004"

```
curl --header "Content-Type: application/json" \
  --request POST \
  --data '{ "senderAccountNumber": "a0002", "receiverAccountNumber": "a0004", "currency": "GBP", "amount": "12.7", "description": "hi" }' \
	'http://localhost:3000/api/transaction' | python -m json.tool
```

request body:
just change the receiverAccountNumber from "a0001" to "a0004" from the request body in test-case#3.1

expected output:
```
{
    "message": "bank account of account number='a0004' does not exists",
    "code": 404
}
```

#### 3.4 test transfer from account a0002 but missing the receiverNumber
```
curl -i --header "Content-Type: application/json" \
  --request POST \
  --data '{ "senderAccountNumber": "a0002", "currency": "GBP", "amount": "12.7" }' \
	'http://localhost:3000/api/transaction' 
```

request body:
just remove the receiverAccountNumber from the request body in test-case#3.1

expected output:
```
HTTP/1.1 400 Bad Request
Vary: Accept
Content-Type: text/plain
Connection: close
Content-Length: 102

may not be null (path = TransactionResource.transfer.arg0.receiverAccountNumber, invalidValue = null)
```


#### 4. test list out all transactions

``` curl -XGET 'http://localhost:3000/api/transaction' | python -m json.tool ```

GET /api/transaction

```
[
    {
        "transactionNumber": "805052874285362946",
        "requestedTime": 1536871479881,
        "senderAccountNumber": "a0002",
        "receiverAccountNumber": "a0001",
        "description": "hi",
        "status": "SUCCESS",
        "processedTime": 1536871480612,
        "failedReason": null,
        "money": {
            "amount": 12.7,
            "currency": "GBP"
        }
    }
]
```

#### 5.1 test get a particular transaction with the transactionNumber
GET /api/transaction/{transacationNumber}

replace the {transacationNumber} the transactionNumber shown above in test-case#4

expected output: 
```
{
    "transactionNumber": "805052874285362946",
    "requestedTime": 1536871479881,
    "senderAccountNumber": "a0002",
    "receiverAccountNumber": "a0001",
    "description": "hi",
    "status": "SUCCESS",
    "processedTime": 1536871480612,
    "failedReason": null,
    "money": {
        "amount": 12.7,
        "currency": "GBP"
    }
}
```


#### 5.2 test get a transaction with non-exists transaction number.

GET /api/transaction/{transacationNumber}

replace the {transacationNumber} the transactionNumber which is NOT shown above in test-case#4

expected output: 
```
{
    "message": "Entity '{transactionNumber}' of PersistedTransactionDao is not found",
    "code": 404
}
```

#### 6. test list out all bank accounts again to see the updated values.

``` curl -XGET 'http://localhost:3000/api/account' | python -m json.tool ```

GET /api/account

expected output:
```
[
    {
        "accountNumber": "a0003",
        "balance": {
            "amount": 0,
            "currency": "GBP"
        }
    },
    {
        "accountNumber": "a0002",
        "balance": {
            "amount": 2987.3,
            "currency": "GBP"
        }
    },
    {
        "accountNumber": "a0001",
        "balance": {
            "amount": 132.7,
            "currency": "GBP"
        }
    }
]
```


#### 7. test concurrently request transfer money from account a0001 to account a0003
```
for i in `seq 20`; do \
curl -i --header "Content-Type: application/json" \
  --request POST \
  --data '{ "senderAccountNumber": "a0001", "receiverAccountNumber":"a0003", "currency": "GBP", "amount": "13.27" }' \
	'http://localhost:3000/api/transaction' & done
```

##### 7.1 list all the transactions

``` curl -XGET 'http://localhost:3000/api/transaction' | python -m json.tool ```

GET /api/account

expected output:

There should be some FAILED transactions of a0001 sending to a0003 
with "failedReason" = "Invalid money amount"
, along with 10 SUCCESS transactions of amount=13.27

##### 7.2 check if the money values of account a0001 and a0003 are correct

``` curl -XGET 'http://localhost:3000/api/account' | python -m json.tool ```

GET /api/account

expected output:

```
[
    {
        "accountNumber": "a0003",
        "balance": {
            "amount": 132.7,
            "currency": "GBP"
        }
    },
    {
        "accountNumber": "a0002",
        "balance": {
            "amount": 2987.3,
            "currency": "GBP"
        }
    },
    {
        "accountNumber": "a0001",
        "balance": {
            "amount": 0.0,
            "currency": "GBP"
        }
    }
]
```


