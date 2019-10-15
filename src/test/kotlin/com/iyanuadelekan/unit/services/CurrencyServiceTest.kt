package com.iyanuadelekan.unit.services

import com.iyanuadelekan.moneytransfer.constants.Currency
import com.iyanuadelekan.moneytransfer.components.Datastore
import com.iyanuadelekan.moneytransfer.models.CurrencyData
import com.iyanuadelekan.moneytransfer.services.CurrencyServiceImpl
import org.junit.Test
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CurrencyServiceTest : BaseServiceTest() {

    private val currencyService = CurrencyServiceImpl()

    @Test
    fun `Test currency creation`() {
        val currency = currencyService.createCurrency(CurrencyData("USD"))
        assertEquals(currency, Datastore.currencyStore["USD"])
    }

    @Test
    fun `Test valid currency retrieval`() {
        val currency = currencyService.retrieveCurrency(Currency.NGN.name)
        assertNotNull(currency)
        assertEquals(Currency.NGN.name, currency.name)
    }

    @Test
    fun `Test currency retrieval with invalid currency name`() {
        // CAD has not been created so this should return null
        val currency = currencyService.retrieveCurrency("CAD")
        assertEquals(null, currency)
    }

    @Test
    fun `Test exchange amount conversion with valid currencies`() {
        // Test Pound to Naira conversion
        var amount = BigDecimal(29.78)
        var convertedAmount = currencyService.getExchangeAmount(amount, Currency.GBP.name, Currency.NGN.name)
        assertEquals(BigDecimal("13599.33"), convertedAmount)

        // Test Naira to Pound conversion
        amount = BigDecimal(456.66)
        convertedAmount = currencyService.getExchangeAmount(amount, Currency.NGN.name, Currency.GBP.name)
        assertEquals(BigDecimal("1.00"), convertedAmount)

        // Test Pound to Pound conversion
        amount = BigDecimal("237.43")
        convertedAmount = currencyService.getExchangeAmount(amount, Currency.GBP.name, Currency.GBP.name)
        assertEquals(amount, convertedAmount)

        // Test Naira to Naira conversion
        amount = BigDecimal("500.00")
        convertedAmount = currencyService.getExchangeAmount(amount, Currency.NGN.name, Currency.NGN.name)
        assertEquals(amount, convertedAmount)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Test exchange amount conversion with invalid currencies`() {
        val amount = BigDecimal(29.78)
        currencyService.getExchangeAmount(amount, "GYO", Currency.NGN.name)
    }
}