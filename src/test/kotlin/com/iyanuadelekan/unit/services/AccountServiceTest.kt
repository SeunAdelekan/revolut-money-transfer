package com.iyanuadelekan.unit.services

import com.iyanuadelekan.moneytransfer.constants.Currency
import com.iyanuadelekan.moneytransfer.components.Datastore
import com.iyanuadelekan.moneytransfer.helpers.generateUUID
import com.iyanuadelekan.moneytransfer.models.AccountData
import com.iyanuadelekan.moneytransfer.models.TransactionOperationData
import com.iyanuadelekan.moneytransfer.services.AccountServiceImpl
import com.iyanuadelekan.moneytransfer.services.TransactionServiceImpl
import org.junit.Before
import org.junit.Test
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AccountServiceTest : BaseServiceTest() {

    private val accountService = AccountServiceImpl()
    private val transactionService = TransactionServiceImpl()

    @Before
    fun init() = Datastore.accountStore.clear()

    @Test
    fun `Test bank account creation`() {
        val account = AccountData("Savings", Currency.NGN.name)
        val createdAccount = accountService.createBankAccount(account)

        assertEquals(1, Datastore.accountStore.size)
        assertEquals(account.accountName, createdAccount.accountName)
        assertEquals(account.currency, createdAccount.currency.name)
        assertEquals("enabled", createdAccount.status)
        assertEquals(0, createdAccount.transactions.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Test retrieval of single account with invalid account ID throws exception`() {
        accountService.getAccount(generateUUID())
    }

    @Test
    fun `Test retrieval of single account with valid account ID`() {
        val account = AccountData("Savings", Currency.NGN.name)
        val savedAccount = accountService.createBankAccount(account)
        val fetchedAccount = accountService.getAccount(savedAccount.id)

        with (savedAccount) {
            assertEquals(id, fetchedAccount.id)
            assertEquals(accountName, fetchedAccount.accountName)
            assertEquals(balance, fetchedAccount.balance)
            assertEquals(status, fetchedAccount.status)
            assertEquals(createdAt, fetchedAccount.createdAt)
            assertEquals(updatedAt, fetchedAccount.updatedAt)
            assertEquals(currency, fetchedAccount.currency)
            assertEquals(transactions, fetchedAccount.transactions)
        }
    }

    @Test
    fun `Test retrieval of accounts by pagination`() {
        val account = AccountData("Savings", Currency.NGN.name)
        val savedAccount = accountService.createBankAccount(account)

        var transactionData = TransactionOperationData(BigDecimal(50.00), Currency.NGN.name)
        transactionService.processDeposit(savedAccount.id, transactionData)
        transactionData = TransactionOperationData(BigDecimal(100.99), Currency.NGN.name)
        transactionService.processDeposit(savedAccount.id, transactionData)

        val transactions = accountService.getAccountTransactions(savedAccount.id, 1, 1)
        assertEquals(1, transactions.size)
    }

    @Test
    fun `Test account registration verification`() {
        val account = AccountData("Savings", Currency.NGN.name)
        val savedAccount = accountService.createBankAccount(account)

        assertTrue(accountService.verifyAccountRegistered(savedAccount.id))
        assertEquals(false, accountService.verifyAccountRegistered(generateUUID()))
    }

    @Test
    fun `Test account transaction retrieval`() {
        val account = AccountData("Savings", Currency.NGN.name)
        val savedAccount = accountService.createBankAccount(account)
        var transactions = accountService.getAccountTransactions(savedAccount.id)

        assertEquals(savedAccount.transactions, transactions)

        val transactionData = TransactionOperationData(BigDecimal(50.00), Currency.NGN.name)
        transactionService.processDeposit(savedAccount.id, transactionData)
        transactions = accountService.getAccountTransactions(savedAccount.id)


        assertEquals(transactions, savedAccount.transactions)
        assertEquals(1, savedAccount.transactions.size)
    }
}