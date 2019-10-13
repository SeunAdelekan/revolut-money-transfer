package com.iyanuadelekan.unit.repositories

import components.Datastore
import generateUUID
import models.entities.Transaction
import org.junit.Before
import org.junit.Test
import repositories.TransactionRepositoryImpl
import java.math.BigDecimal
import kotlin.test.assertEquals

class TransactionRepositoryTest {

    private val transactionRepository = TransactionRepositoryImpl()

    @Before
    fun init() = Datastore.empty()

    @Test
    fun `Test saving of transactions`() {
        val sessionReference = "SESSION-${generateUUID()}}"

        val transactionOne = Transaction(
                BigDecimal(12.50),
                "TXN-${generateUUID()}",
                sessionReference,
                TransactionType.DEBIT,
                TransactionCategory.BANK_TRANSFER,
                "Pocket money")

        val transactionTwo = Transaction(
                BigDecimal(12.50),
                "TXN-${generateUUID()}",
                sessionReference,
                TransactionType.CREDIT,
                TransactionCategory.BANK_TRANSFER,
                "Pocket money")

        with (transactionRepository) {
            save(transactionOne)
            save(transactionTwo)
            assertEquals(transactionOne, findById(transactionOne.id))
            assertEquals(transactionTwo, findById(transactionTwo.id))
        }
    }

    @Test
    fun `Test retrieval of transactions by ID`() {
        val transaction = Transaction(
                BigDecimal(1000.01),
                "TXN-${generateUUID()}",
                "SESSION-${generateUUID()}}",
                TransactionType.CREDIT,
                TransactionCategory.ACCOUNT_FUNDING)

        with (transactionRepository) {
            save(transaction)
            assertEquals(transaction, findById(transaction.id))
        }
    }

    @Test
    fun `Test retrieval of saved transaction counts`() {
        val transaction = Transaction(
                BigDecimal(1.00),
                "TXN-${generateUUID()}",
                "SESSION-${generateUUID()}}",
                TransactionType.CREDIT,
                TransactionCategory.ACCOUNT_FUNDING)

        with (transactionRepository) {
            save(transaction)
            assertEquals(1, countRecords())

        }
    }
}