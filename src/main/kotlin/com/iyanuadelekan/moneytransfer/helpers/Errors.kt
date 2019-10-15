package com.iyanuadelekan.moneytransfer.helpers

import java.math.BigDecimal

fun buildAccountIdError(accountId: String) = "account with id $accountId does not exist"

fun buildCurrencyNameError(currencyName: String) = "Invalid currency name $currencyName"

fun buildInvalidExchangeRateError(sourceCurrencyName: String, targetCurrencyName: String): String {
    return buildString {
        append("Exchange rate not defined for currency ")
        append("source $sourceCurrencyName ")
        append("and target $targetCurrencyName")
    }
}

class UnsupportedContentTypeException(contentType: String?) :
        RuntimeException("Content type $contentType not currently supported")

class InsufficientBalanceException(amount: BigDecimal):
        RuntimeException("Cannot perform transaction of $amount due to insufficient balance.")
