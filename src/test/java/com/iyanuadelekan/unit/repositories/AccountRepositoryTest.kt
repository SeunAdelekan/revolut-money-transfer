package com.iyanuadelekan.unit.repositories

import com.iyanuadelekan.moneytransfer.components.Datastore
import com.iyanuadelekan.moneytransfer.generateUUID
import com.iyanuadelekan.moneytransfer.models.entities.Account
import org.junit.Before
import org.junit.Test
import com.iyanuadelekan.moneytransfer.repositories.AccountRepositoryImpl
import kotlin.test.assertEquals

class AccountRepositoryTest {

    private val accountRepository = AccountRepositoryImpl()

    @Before
    fun init() = Datastore.empty()

    @Test
    fun `Test saving of account`() {
        val account = Account("Kevin Hart")
        accountRepository.save(account)
        assertEquals(1, accountRepository.countRecords())
    }

    @Test
    fun `Test retrieval of account by ID`() {
        val account = Account("Kevin Hart")
        accountRepository.save(account)
        assertEquals(account, accountRepository.findById(account.id))
    }

    @Test
    fun `Test retrieval of all accounts by page`() {
        with (accountRepository) {
            save(Account("Kevin Hart"))
            save(Account("Leonardo Dicaprio"))
            save(Account("Mr Bean"))
            save(Account("Prince"))
            save(Account("Uncle Bob"))
        }
        var accounts = accountRepository.findByPage(1, 10)
        assertEquals(5, accounts.size)

        accounts = accountRepository.findByPage(1, 1)
        assertEquals(1, accounts.size)

        accounts = accountRepository.findByPage(3, 2)
        assertEquals(1, accounts.size)

        accounts = accountRepository.findByPage(2, 2)
        assertEquals(2, accounts.size)
    }

    @Test
    fun `Test querying of existing accounts`() {
        val account = Account("Uncle Bob")
        with (accountRepository) {
            this.save(account)
            assertEquals(true, this.exists(account.id))
        }
    }

    @Test
    fun `Test querying of none existing accounts`() {
        assertEquals(false, accountRepository.exists(generateUUID()))
    }

    @Test
    fun `Test querying of document count`() {
        with (accountRepository) {
            this.save(Account("Ibukun Adelekan"))
            this.save(Account("Igbagbo Adelekan"))
            assertEquals(2, this.countRecords())
        }
    }
}