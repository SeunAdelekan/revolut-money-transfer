package com.iyanuadelekan.unit.services

import com.iyanuadelekan.moneytransfer.components.Datastore
import com.iyanuadelekan.moneytransfer.constants.Currency
import com.iyanuadelekan.moneytransfer.constants.TransactionCategory
import com.iyanuadelekan.moneytransfer.constants.TransactionType
import com.iyanuadelekan.moneytransfer.helpers.InsufficientBalanceException
import com.iyanuadelekan.moneytransfer.helpers.generateUUID
import com.iyanuadelekan.moneytransfer.models.AccountData
import com.iyanuadelekan.moneytransfer.models.TransactionOperationData
import com.iyanuadelekan.moneytransfer.services.AccountServiceImpl
import com.iyanuadelekan.moneytransfer.services.TransactionServiceImpl
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class TransactionsServiceTest : BaseServiceTest() {

    private val accountService = AccountServiceImpl()
    private val transactionService = TransactionServiceImpl()

    @Before
    fun init() = Datastore.accountStore.clear()

    @Test(expected = IllegalArgumentException::class)
    fun `Test fund deposit with invalid account ID`() {
        val data = TransactionOperationData(BigDecimal("10000.45"), Currency.NGN.name)
        transactionService.processDeposit(generateUUID(), data)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Test fund deposit with amount below 0`() {
        val account = accountService.createBankAccount(
                AccountData("current account", Currency.NGN.name))

        val data = TransactionOperationData(BigDecimal("-888"), Currency.NGN.name)
        transactionService.processDeposit(account.id, data)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Test fund deposit with 0`() {
        val account = accountService.createBankAccount(
                AccountData("current account", Currency.NGN.name))

        val data = TransactionOperationData(BigDecimal("0"), Currency.NGN.name)
        transactionService.processDeposit(account.id, data)
    }

    @Test
    fun `Test fund deposit with valid parameters`() {
        val account = accountService.createBankAccount(
                AccountData("current account", Currency.NGN.name))
        val amount = BigDecimal("200.00")
        val data = TransactionOperationData(amount, Currency.NGN.name)
        val result = transactionService.processDeposit(account.id, data)

        with (result.first) {
            assertEquals(account, this)
            assertEquals(amount, this.balance)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Test account transfer with invalid source ID`() {
        val data = TransactionOperationData(BigDecimal("23678.00"), Currency.NGN.name)
        transactionService.processTransfer(generateUUID(), generateUUID(), data)
    }

    @Test(expected = InsufficientBalanceException::class)
    fun `Test account transfer from source account with insufficient balance`() {
        val account = accountService.createBankAccount(
                AccountData("savings account", Currency.NGN.name))

        val data = TransactionOperationData(BigDecimal("23678.00"), Currency.NGN.name)
        transactionService.processTransfer(account.id, generateUUID(), data)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Test account transfer with invalid recipient id`() {
        val account = accountService.createBankAccount(
                AccountData("money bank", Currency.NGN.name))

        var data = TransactionOperationData(BigDecimal("200000.00"), Currency.NGN.name)
        transactionService.processDeposit(account.id, data)

        data = TransactionOperationData(BigDecimal("60000.00"), Currency.NGN.name)
        transactionService.processTransfer(account.id, generateUUID(), data)
    }

    @Test
    fun `Test account transfer with valid parameters`() {
        val sourceAccount = accountService.createBankAccount(
                AccountData("source", Currency.NGN.name))
        val destinationAccount = accountService.createBankAccount(
                AccountData("destination", Currency.NGN.name))

        var data = TransactionOperationData(BigDecimal("200000.00"), Currency.NGN.name)
        transactionService.processDeposit(sourceAccount.id, data)

        val transferAmount = BigDecimal("60000.00")
        data = TransactionOperationData(transferAmount, Currency.NGN.name)
        val response = transactionService.processTransfer(sourceAccount.id, destinationAccount.id, data)

        assertEquals(response.first, sourceAccount)

        with (response.second) {
            assertEquals(TransactionType.DEBIT, type)
            assertEquals(TransactionCategory.BANK_TRANSFER, category)
            assertEquals(amount, transferAmount)
            assertEquals(sourceAccount.balance, balanceAfter)
            assertEquals(recipientAccountId, destinationAccount.id)
            assertEquals(sourceAccount.balance.plus(transferAmount), balanceBefore)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Test fund withdrawal with invalid account ID`() {
        val data = TransactionOperationData(BigDecimal("10000.45"), Currency.NGN.name)
        transactionService.withdrawFunds(generateUUID(), data)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Test fund withdrawal with amount below 0`() {
        val account = accountService.createBankAccount(
                AccountData("current account", Currency.NGN.name))

        val data = TransactionOperationData(BigDecimal("-888"), Currency.NGN.name)
        transactionService.withdrawFunds(account.id, data)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Test fund withdrawal with 0`() {
        val account = accountService.createBankAccount(
                AccountData("current account", Currency.NGN.name))

        val data = TransactionOperationData(BigDecimal("0"), Currency.NGN.name)
        transactionService.withdrawFunds(account.id, data)
    }

    @Test(expected = InsufficientBalanceException::class)
    fun `Test fund withdrawal with 0 balance`() {
        val account = accountService.createBankAccount(
                AccountData("current account", Currency.NGN.name))

        val data = TransactionOperationData(BigDecimal("100.00"), Currency.NGN.name)
        transactionService.withdrawFunds(account.id, data)
    }

    @Test
    fun `Test fund withdrawal with valid parameters`() {
        val account = accountService.createBankAccount(
                AccountData("current account", Currency.NGN.name))

        // deposit money into newly created account.
        val depositAmount = BigDecimal("200.00")
        var data = TransactionOperationData(depositAmount, Currency.NGN.name)
        transactionService.processDeposit(account.id, data)

        // withdraw funds from account
        val withdrawalAmount = BigDecimal("100.00")
        data = TransactionOperationData(withdrawalAmount, Currency.NGN.name)
        val result = transactionService.withdrawFunds(account.id, data)

        with (result.first) {
            assertEquals(account, account)
            assertEquals(depositAmount - withdrawalAmount, balance)
        }
    }
}