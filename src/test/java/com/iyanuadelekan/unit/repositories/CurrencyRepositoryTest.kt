package com.iyanuadelekan.unit.repositories

import components.Datastore
import models.entities.Currency
import org.junit.Before
import org.junit.Test
import repositories.CurrencyRepositoryImpl
import kotlin.test.assertEquals

class CurrencyRepositoryTest {

    private val currencyRepository = CurrencyRepositoryImpl()

    @Before
    fun init() = Datastore.empty()

    @Test
    fun `Test saving of currency`() {
        val naira = Currency("NGN")
        val pound = Currency("GBP")
        with (currencyRepository) {
            save(naira)
            save(pound)
        }
        assertEquals(2, currencyRepository.countRecords())
    }

    @Test
    fun `Test retrieval of currency by name`() {
        val naira = Currency("NGN")
        currencyRepository.save(naira)
        assertEquals(naira, currencyRepository.findByName(naira.name))
    }

    @Test
    fun `Test querying of none existing currency`() {
        assertEquals(null, currencyRepository.findByName("USD"))
    }
}