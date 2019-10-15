# Revolut Money Transfer REST API
A thread safe RESTful API facilitating money transfers between accounts.

## Getting Started
Build the program via maven with the following command:
```bash
mvn clean install
```

Start application:
```
java -jar target/money-transfer-api-1.0.0-jar-with-dependencies.jar
```

A postman collection for testing the API is available for download [here](https://www.getpostman.com/collections/ecbab17895ffe6dee918).

## Technologies used
* Kotlin as implementation language.
* Javalin as a web API framework.
* Jackson for JSON serialization and deserialization.
* JUnit and Hemcrest for unit tests.
* Unirest.

## API documentation
The following REST resources are exposed by the API:

| Http method | Endpoint                                        | Sample Request Body                                           | Description                                        |
|-------------|-------------------------------------------------|---------------------------------------------------|----------------------------------------------------------------|
| POST        | /accounts                                       | { "accountName": "Account Name", "currency": "NGN" } | This resource creates a new account.                        |
| GET         | /accounts/:account_id                           |                                                   | This resource gets the details of a given account.             |
| GET         | /accounts                                       |                                                   | This resource gets a paginated list of created accounts. 'page' and 'limit' are optional query parameters.|
| POST        | /accounts/:account_id/deposits                  | { "amount": 10000, "currency": "NGN" }            | Use this resource to fund a created account.                   |
| POST        | /accounts/:account_id/withdrawals               | { "amount": 10000, "currency": "NGN" }            | Use this resource to withdraw funds from a created account.    |
| POST        | /account/:sender_account_id/transfers/:recipient_account_id | { "amount": 1, "currency": "GBP" }    | This resource handles money transfers between created accounts.|
| GET         | /account/:account_id/transactions               |                                       | Resource used to retrieve paginated list of transactions performed on a given account.|


### POST /accounts
Create account in the service.

Sample request:
```json
{
	"accountName": "Ibukun Adelekan",
	"currency": "GBP"
}
```

Sample response:
```json
{
    "status": "success",
    "data": {
        "accountName": "Ibukun Adelekan",
        "balance": "0.00",
        "status": "enabled",
        "id": "956aec8bd9a74cb8bb52fc6060eec009",
        "currency": {
            "name": "GBP",
            "id": "a81d51037da9485cb8850d30d3fe8913"
        }
    }
}
```

### POST /accounts/:accountId/deposits
Deposit money to a given account.

Sample request:
```json
{
	"amount": "25000",
	"currency": "GBP"
}
```

Sample response:
```json
{
    "status": "success",
    "data": {
        "account": {
            "id": "956aec8bd9a74cb8bb52fc6060eec009",
            "accountName": "Ibukun Adelekan",
            "balance": "25000.00",
            "status": "enabled",
            "currency": {
                "id": "a81d51037da9485cb8850d30d3fe8913",
                "name": "GBP"
            }
        },
        "transaction": {
            "id": "9da7b5c7e38741049a7a2651848c3348",
            "amount": "25000.00",
            "transactionReference": "TXN-21ee860d990346beb1760ad4909b39a2",
            "sessionReference": "SESSION-39f475d1a71941ee99ae5ef854f735ef}",
            "type": "CREDIT",
            "category": "ACCOUNT_FUNDING",
            "balanceBefore": "0.00",
            "balanceAfter": "25000.00",
            "createdAt": "2019-10-15T02:13:09"
        }
    }
}
```

### GET /accounts/:accountId
Fetch details of a specified account - including the account currency and balance.

Sample response:
```json
{
    "status": "success",
    "data": {
        "id": "956aec8bd9a74cb8bb52fc6060eec009",
        "accountName": "Ibukun Adelekan",
        "balance": "25000.00",
        "status": "enabled",
        "currency": {
            "id": "a81d51037da9485cb8850d30d3fe8913",
            "name": "GBP"
        }
    }
}
```

### GET /accounts
Fetch a list of all created accounts.

Query Parameters:
- page (optional): Current page. 1 by default
- limit (optional): Size of page. 50 by default.

Sample response:
```json
{
    "status": "success",
    "data": [
        {
            "id": "956aec8bd9a74cb8bb52fc6060eec009",
            "accountName": "Ibukun Adelekan",
            "balance": "25000.00",
            "status": "enabled",
            "currency": {
                "id": "a81d51037da9485cb8850d30d3fe8913",
                "name": "GBP"
            }
        },
        {
            "id": "bcaa9836b0374f38b7752eeaaa9beaf8",
            "accountName": "Iyanu Adelekan",
            "balance": "0.00",
            "status": "enabled",
            "currency": {
                "id": "6aed93c40143491b80a45ff5a6f23f59",
                "name": "NGN"
            }
        }
    ]
}
```

### POST /account/:sender_account_id/transfers/:recipient_account_id
Transfers money between accounts.

Sample request:
```json
{
	"amount": 10000.00,
	"currency": "NGN",
	"description": "Hold this for me"
}
```

Sample response:
```json
{
    "status": "success",
    "data": {
        "account": {
            "id": "956aec8bd9a74cb8bb52fc6060eec009",
            "accountName": "Ibukun Adelekan",
            "balance": "24978.00",
            "status": "enabled",
            "currency": {
                "id": "a81d51037da9485cb8850d30d3fe8913",
                "name": "GBP"
            }
        },
        "transaction": {
            "id": "054b91c478e2422dbb0b93b6194a6db0",
            "amount": "22.00",
            "recipientAccountId": "bcaa9836b0374f38b7752eeaaa9beaf8",
            "transactionReference": "TXN-0edfd47b595b4cceabf8a2afe064c75b",
            "sessionReference": "SESSION-1ee393610327405eb6abdec95f5196e1}",
            "type": "DEBIT",
            "category": "BANK_TRANSFER",
            "description": "Hold this for me",
            "balanceBefore": "25000.00",
            "balanceAfter": "24978.00",
            "createdAt": "2019-10-15T02:15:27"
        }
    }
}
```

### POST /accounts/:accountId/withdrawals
Withdraws money to the given account.

Sample request:
```json
{
	"amount": "10000",
	"currency": "NGN"
}
```

Sample response:
```json
{
    "status": "success",
    "data": {
        "account": {
            "id": "956aec8bd9a74cb8bb52fc6060eec009",
            "accountName": "Ibukun Adelekan",
            "balance": "24968.00",
            "status": "enabled",
            "currency": {
                "id": "a81d51037da9485cb8850d30d3fe8913",
                "name": "GBP"
            }
        },
        "transaction": {
            "id": "64ac9c1454684b40b1b17aec51727d69",
            "amount": "10.00",
            "transactionReference": "TXN-fc1d832ee07e43c4a5a2f91d104100dc",
            "sessionReference": "SESSION-90583e2fc2664173a5c1b08ac4b14afa}",
            "type": "DEBIT",
            "category": "ACCOUNT_WITHDRAWAL",
            "balanceBefore": "24978.00",
            "balanceAfter": "24968.00",
            "createdAt": "2019-10-15T02:16:49"
        }
    }
}
```

### GET /account/:account_id/transactions
Get account transactions. Transactions are sorted in descending order by date and time performed.

Query Parameters:
- page (optional): Current page. 1 by default
- limit (optional): Size of page. 50 by default.

Sample Response:
```json
{
    "status": "success",
    "data": [
        {
            "id": "64ac9c1454684b40b1b17aec51727d69",
            "amount": "10.00",
            "transactionReference": "TXN-fc1d832ee07e43c4a5a2f91d104100dc",
            "sessionReference": "SESSION-90583e2fc2664173a5c1b08ac4b14afa}",
            "type": "DEBIT",
            "category": "ACCOUNT_WITHDRAWAL",
            "balanceBefore": "24978.00",
            "balanceAfter": "24968.00",
            "createdAt": "2019-10-15T02:16:49"
        },
        {
            "id": "054b91c478e2422dbb0b93b6194a6db0",
            "amount": "22.00",
            "recipientAccountId": "bcaa9836b0374f38b7752eeaaa9beaf8",
            "transactionReference": "TXN-0edfd47b595b4cceabf8a2afe064c75b",
            "sessionReference": "SESSION-1ee393610327405eb6abdec95f5196e1}",
            "type": "DEBIT",
            "category": "BANK_TRANSFER",
            "description": "Hold this for me",
            "balanceBefore": "25000.00",
            "balanceAfter": "24978.00",
            "createdAt": "2019-10-15T02:15:27"
        },
        {
            "id": "9da7b5c7e38741049a7a2651848c3348",
            "amount": "25000.00",
            "transactionReference": "TXN-21ee860d990346beb1760ad4909b39a2",
            "sessionReference": "SESSION-39f475d1a71941ee99ae5ef854f735ef}",
            "type": "CREDIT",
            "category": "ACCOUNT_FUNDING",
            "balanceBefore": "0.00",
            "balanceAfter": "25000.00",
            "createdAt": "2019-10-15T02:13:09"
        }
    ]
}
```

### Currencies and Exchange Rates
Deposits and transfers can be made in either NGN or USD. When transactions are done 
with two different currencies the following exchange rates apply:
* NGN 1 -> GBP 0.0022
* GBP 1 -> NGN 456.66

## Error Codes
The following are the error codes that can be received from the server.

| Server Error Code | Description                               | 
|-------------|-------------------------------------------------|
| 1000        | The request is being made in an unsupported HTTP content type.|
| 1001        | This error code means an invalid request parameter is being sent to the server|
| 1002        | The account does not have sufficient balance to perform given the transaction.|

The error codes above are distinct from and do not replace HTTP status codes.
## Tests
Unit and integration tests have been written to verify the correctness of the system.
Tests can be run with the following command:
```bash
mvn test
```
![Test Results](./assets/test-run.jpeg?raw=true "")