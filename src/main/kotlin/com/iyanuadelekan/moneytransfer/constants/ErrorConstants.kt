package com.iyanuadelekan.moneytransfer.constants

enum class RequestError(val code: String) {
    UNSUPPORTED_CONTENT_TYPE("1000"),
    INVALID_PARAMETER("1001"),
    INSUFFICIENT_BALANCE("1002")
}

enum class ErrorMessage(val message: String) {
    UNSUPPORTED_CONTENT_TYPE("Unsupported HTTP content type"),
    ACCOUNT_CREATION_PARAMS_REQUIRED("accountName and currency are required."),
    INVALID_ACCOUNT_NAME("Account name must be a minimum of 3 characters in length."),
    INVALID_CURRENCY_NAME("That currency is not supported at the moment"),
    INVALID_ACCOUNT_ID("An account with that ID does not exist."),
    INVALID_PAGE("Page must be greater than 0"),
    INVALID_PAGE_LIMIT("Limit must be greater than 0"),
    INVALID_SENDER_ACCOUNT_ID("A sender account with that ID does not exist."),
    INVALID_RECIPIENT_ACCOUNT_ID("A recipient account with that ID does not exist."),
    TRANSACTION_OPERATION_PARAMS_REQUIRED("amount and currency are required."),
    INVALID_TRANSACTION_AMOUNT("Transaction amounts must be greater than 0.00"),
}