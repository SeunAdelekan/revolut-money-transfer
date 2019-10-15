package com.iyanuadelekan.unit.components

import com.iyanuadelekan.moneytransfer.constants.Currency
import com.iyanuadelekan.moneytransfer.components.Datastore
import com.iyanuadelekan.moneytransfer.models.AccountData
import com.iyanuadelekan.moneytransfer.models.TransactionOperationData
import com.iyanuadelekan.moneytransfer.helpers.seedCurrencies
import com.iyanuadelekan.moneytransfer.helpers.seedExchangeRates
import com.iyanuadelekan.moneytransfer.services.AccountServiceImpl
import com.iyanuadelekan.moneytransfer.services.TransactionServiceImpl
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertTrue

class DatastoreTest {

    private val accountService = AccountServiceImpl()
    private val transactionService = TransactionServiceImpl()

    @Before
    fun init() {
        seedCurrencies()
        seedExchangeRates()
    }

    @Test
    fun `Test clearing of all data`() {
        Datastore.empty()
        assertTrue(Datastore.accountStore.isEmpty())
        assertTrue(Datastore.transactionStore.isEmpty())
        assertTrue(Datastore.currencyStore.isEmpty())
        assertTrue(Datastore.exchangeRateStore.isEmpty())
    }

    @Test
    fun `Test clearing of accounts`() {
        accountService.createBankAccount(AccountData("savings", Currency.NGN.name))
        Datastore.clearAccounts()
        assertTrue(Datastore.accountStore.isEmpty())
    }

    @Test
    fun `Test clearing of transactions`() {
        val account = accountService.createBankAccount(AccountData("savings", Currency.NGN.name))
        val depositData = TransactionOperationData(BigDecimal("10.00"), Currency.NGN.name)
        transactionService.processDeposit(account.id, depositData)
        Datastore.clearTransactions()
        assertTrue(Datastore.transactionStore.isEmpty())
    }
}