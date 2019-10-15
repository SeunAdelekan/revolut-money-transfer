package com.iyanuadelekan.unit.repositories

import com.iyanuadelekan.moneytransfer.components.Datastore
import com.iyanuadelekan.moneytransfer.models.entities.Currency
import com.iyanuadelekan.moneytransfer.models.entities.ExchangeRate
import org.junit.Before
import org.junit.Test
import com.iyanuadelekan.moneytransfer.repositories.CurrencyRepositoryImpl
import com.iyanuadelekan.moneytransfer.repositories.ExchangeRateRepositoryImpl
import com.iyanuadelekan.moneytransfer.helpers.seedCurrencies
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class ExchangeRateRepositoryTest {

    private val currencyRepository = CurrencyRepositoryImpl()
    private val exchangeRateRepository = ExchangeRateRepositoryImpl()

    @Before
    fun init() = Datastore.empty()

    @Test
    fun `Test saving of exchange rate`() {
        seedCurrencies()
        val naira = currencyRepository.findByName("NGN") as Currency
        val pound = currencyRepository.findByName("GBP") as Currency

        val nairaToPounds = ExchangeRate(naira, pound, 0.0022)
        exchangeRateRepository.save(nairaToPounds)
        assertNotNull(exchangeRateRepository.findBySourceAndTargetCurrencies(naira.name, pound.name))
    }

    @Test
    fun `Test retrieval of exchange rate item count`() {
        seedCurrencies()
        val naira = currencyRepository.findByName("NGN") as Currency
        val pound = currencyRepository.findByName("GBP") as Currency

        val nairaToPounds = ExchangeRate(naira, pound, 0.0022)
        exchangeRateRepository.save(nairaToPounds)
        assertEquals(1, exchangeRateRepository.countRecords())
    }
}