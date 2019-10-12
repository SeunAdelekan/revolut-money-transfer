package components

import models.entities.Account
import models.entities.Currency
import models.entities.ExchangeRate
import models.entities.Transaction
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object Database {

    internal val accountStore = ConcurrentHashMap<String, Account>()
    internal val transactionStore = ConcurrentHashMap<String, Transaction>()
    internal val currencyStore = ConcurrentHashMap<String, Currency>()
    internal val exchangeRateStore = ConcurrentHashMap<Pair<String, String>, ExchangeRate>()
}