package com.iyanuadelekan.moneytransfer.models

import java.math.BigDecimal

data class CurrencyData(val name: String)

data class AccountData(val accountName: String, val currency: String)

data class TransactionOperationData(
        val amount: BigDecimal,
        val currency: String,
        val description: String? = null
)