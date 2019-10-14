package com.iyanuadelekan.moneytransfer.components

import com.iyanuadelekan.moneytransfer.models.entities.Account
import com.iyanuadelekan.moneytransfer.models.entities.Currency
import com.iyanuadelekan.moneytransfer.models.entities.ExchangeRate
import com.iyanuadelekan.moneytransfer.models.entities.Transaction
import java.util.concurrent.ConcurrentHashMap

object Datastore {

    internal val accountStore = ConcurrentHashMap<String, Account>()
    internal val transactionStore = ConcurrentHashMap<String, Transaction>()
    internal val currencyStore = ConcurrentHashMap<String, Currency>()
    internal val exchangeRateStore = ConcurrentHashMap<Pair<String, String>, ExchangeRate>()

    internal fun empty() {
        accountStore.clear()
        transactionStore.clear()
        currencyStore.clear()
        exchangeRateStore.clear()
    }

    internal fun clearAccounts() = accountStore.clear()

    internal fun clearTransactions() = transactionStore.clear()
}