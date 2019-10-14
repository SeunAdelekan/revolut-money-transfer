enum class TransactionType {
    CREDIT, DEBIT
}

enum class TransactionCategory {
    ACCOUNT_FUNDING, BANK_TRANSFER,
}

enum class RequestError(val code: String) {
    UNSUPPORTED_CONTENT_TYPE("1000"),
    INVALID_PARAMETER("1001"),
    INSUFFICIENT_BALANCE("1002")
}