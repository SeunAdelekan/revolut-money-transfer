# Revolut Money Transfer REST API
A RESTful API facilitating money transfers between accounts.

## Getting Started
Install the program via maven with the following command:
```bash
mvn clean install
```
Package the application:
```
mvn package
```

Start application:
```
java -jar target/money-transfer-api-1.0.0-jar-with-dependencies.jar
```


## Technologies used
* Kotlin as implementation language
* Javalin as a web API framework.
* Jackson for JSON serialization and deserialization.
* JUnit and Hemcrest for unit tests.
* Unirest

## API documentation
The following REST resources are exposed by the API:

| Http method | Endpoint                                        | Request                                           | Description                                                    |
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
	"accountName": "Iyanu Adelekan",
	"currency": "NGN"
}
```
Currency should be passed in capital letters.

Sample response:
```json
{
    "status": "success",
    "data": {
        "accountName": "Iyanu Adelekan",
        "balance": "0.00",
        "status": "enabled",
        "createdAt": "2019-10-14T11:08:22",
        "updatedAt": "2019-10-14T11:08:22",
        "id": "7fdbd2b7c4ba4ce8bee9878b846f282b",
        "currency": {
            "name": "NGN",
            "id": "1ae61577f5fc4f248bbcfd25d7ec0c4d"
        }
    }
}
```

### POST /accounts/:accountId/deposits
Deposit money to the given account.

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
        "id": "7fdbd2b7c4ba4ce8bee9878b846f282b",
        "accountName": "Iyanu Adelekan",
        "balance": "10000.00",
        "status": "enabled",
        "createdAt": "2019-10-14T11:08:22",
        "updatedAt": "2019-10-14T11:08:22",
        "currency": {
            "id": "1ae61577f5fc4f248bbcfd25d7ec0c4d",
            "name": "NGN"
        }
    }
}
```

### POST /accounts/:accountId/withdrawals
Withdraws money to the given account.

Sample request:
```json
{
	"amount": "1000",
	"currency": "NGN"
}
```

Sample response:
```json
{
    "status": "success",
    "data": {
        "id": "cbf3224554b94cafa06cab91b0f44a66",
        "accountName": "Ibukun Adelekan",
        "balance": "9000.00",
        "status": "enabled",
        "createdAt": "2019-10-15T10:06:39",
        "updatedAt": "2019-10-15T10:06:39",
        "currency": {
            "id": "39df6dff7dce4d809bcea9b382f3b229",
            "name": "NGN"
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
        "id": "7fdbd2b7c4ba4ce8bee9878b846f282b",
        "accountName": "Iyanu Adelekan",
        "balance": "10000.00",
        "status": "enabled",
        "createdAt": "2019-10-14T11:08:22",
        "updatedAt": "2019-10-14T11:08:22",
        "currency": {
            "id": "1ae61577f5fc4f248bbcfd25d7ec0c4d",
            "name": "NGN"
        }
    }
}
```

### GET /accounts
Lists all created accounts.

Query Parameters:
- page (optional): Current page. 1 by default
- limit (optional): Size of page. 50 by default.

Sample response:
```json
{
    "status": "success",
    "data": [
        {
            "id": "3c484de9501b429d9be6467d7c5bac6f",
            "accountName": "Mr. Bean",
            "balance": "0.00",
            "status": "enabled",
            "createdAt": "2019-10-14T11:14:57",
            "updatedAt": "2019-10-14T11:14:57",
            "currency": {
                "id": "1e931c999f8a4b4f8e2918dd83b5c623",
                "name": "GBP"
            }
        },
        {
            "id": "9855ec8b00314852b9a506af5cd96c42",
            "accountName": "Opeyemi Oyedele",
            "balance": "0.00",
            "status": "enabled",
            "createdAt": "2019-10-14T11:12:12",
            "updatedAt": "2019-10-14T11:12:12",
            "currency": {
                "id": "1ae61577f5fc4f248bbcfd25d7ec0c4d",
                "name": "NGN"
            }
        },
        {
            "id": "7fdbd2b7c4ba4ce8bee9878b846f282b",
            "accountName": "Iyanu Adelekan",
            "balance": "10000.00",
            "status": "enabled",
            "createdAt": "2019-10-14T11:08:22",
            "updatedAt": "2019-10-14T11:08:22",
            "currency": {
                "id": "1ae61577f5fc4f248bbcfd25d7ec0c4d",
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
	"amount": 456.66,
	"currency": "NGN",
	"description": "Something Small"
}
```

Sample response:
```json
{
    "status": "success",
    "data": {
        "account": {
            "id": "7fdbd2b7c4ba4ce8bee9878b846f282b",
            "accountName": "Iyanu Adelekan",
            "balance": "9543.34",
            "status": "enabled",
            "createdAt": "2019-10-14T11:08:22",
            "updatedAt": "2019-10-14T11:08:22",
            "currency": {
                "id": "1ae61577f5fc4f248bbcfd25d7ec0c4d",
                "name": "NGN"
            }
        },
        "transaction": {
            "id": "768bb1a7d9da4cf9a8c668f55f309208",
            "amount": "456.66",
            "transactionReference": "TXN-100eaa428737411386d90a211a05e34d",
            "sessionReference": "SESSION-76d1cd06d19a4b0b8bf88fdaa892a117}",
            "type": "DEBIT",
            "category": "BANK_TRANSFER",
            "description": "Something Small",
            "balanceBefore": "10000.00",
            "balanceAfter": "9543.34",
            "createdAt": "2019-10-14T11:43:46",
            "updatedAt": "2019-10-14T11:43:46"
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
            "id": "768bb1a7d9da4cf9a8c668f55f309208",
            "amount": "456.66",
            "transactionReference": "TXN-100eaa428737411386d90a211a05e34d",
            "sessionReference": "SESSION-76d1cd06d19a4b0b8bf88fdaa892a117}",
            "type": "DEBIT",
            "category": "BANK_TRANSFER",
            "description": "Something Small",
            "balanceBefore": "10000.00",
            "balanceAfter": "9543.34",
            "createdAt": "2019-10-14T11:43:46",
            "updatedAt": "2019-10-14T11:43:46"
        },
        {
            "id": "47455a1d134a412bae2a4e26c1992ec3",
            "amount": "10000.00",
            "transactionReference": "TXN-a42f832e2cae452b82073730541e6ef3",
            "sessionReference": "SESSION-ee2e93863ba44dc397d905536684dc39}",
            "type": "CREDIT",
            "category": "ACCOUNT_FUNDING",
            "description": null,
            "balanceBefore": "0.00",
            "balanceAfter": "10000.00",
            "createdAt": "2019-10-14T11:08:49",
            "updatedAt": "2019-10-14T11:08:49"
        }
    ]
}
```

### Currencies and Exchange Rates
Deposits and transfers can be made in either NGN or USD. When transactions are done 
with two different currencies the following exchange rates apply:
* NGN 1 -> GBP 0.0022
* GBP 1 -> NGN 456.66

## Tests
Unit and integration tests have been written to verify the correctness of the system.
Tests can be run with the following command:
```bash
mvn test
```