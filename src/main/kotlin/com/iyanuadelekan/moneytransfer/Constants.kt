package com.iyanuadelekan.moneytransfer

enum class TransactionType {
    CREDIT, DEBIT
}

enum class TransactionCategory {
    ACCOUNT_FUNDING, BANK_TRANSFER, ACCOUNT_WITHDRAWAL
}

enum class RequestError(val code: String) {
    UNSUPPORTED_CONTENT_TYPE("1000"),
    INVALID_PARAMETER("1001"),
    INSUFFICIENT_BALANCE("1002")
}

enum class Currency(name: String) {
    NGN("NGN"), GBP("GBP")
}