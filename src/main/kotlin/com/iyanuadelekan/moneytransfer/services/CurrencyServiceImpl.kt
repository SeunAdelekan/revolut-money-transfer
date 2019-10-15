package com.iyanuadelekan.moneytransfer.services

import com.iyanuadelekan.moneytransfer.components.Datastore
import com.iyanuadelekan.moneytransfer.helpers.buildCurrencyNameError
import com.iyanuadelekan.moneytransfer.helpers.buildInvalidExchangeRateError
import com.iyanuadelekan.moneytransfer.models.CurrencyData
import com.iyanuadelekan.moneytransfer.models.entities.Currency
import com.iyanuadelekan.moneytransfer.repositories.CurrencyRepository
import com.iyanuadelekan.moneytransfer.repositories.CurrencyRepositoryImpl
import com.iyanuadelekan.moneytransfer.repositories.ExchangeRateRepository
import com.iyanuadelekan.moneytransfer.repositories.ExchangeRateRepositoryImpl
import java.math.BigDecimal
import java.math.RoundingMode

class CurrencyServiceImpl : CurrencyService {

    private val currencyRepository: CurrencyRepository = CurrencyRepositoryImpl()
    private val exchangeRateRepository: ExchangeRateRepository = ExchangeRateRepositoryImpl()

    override fun createCurrency(currencyDetails: CurrencyData): Currency {
        val currency = Currency(name = currencyDetails.name)
        return currencyRepository.save(currency)
    }

    override fun retrieveCurrency(currencyName: String): Currency? = currencyRepository.findByName(currencyName)

    override fun currencyExists(currencyName: String): Boolean = currencyRepository.findByName(currencyName) != null

    @Throws(IllegalArgumentException::class)
    override fun getExchangeAmount(
            amount: BigDecimal,
            sourceCurrencyName: String,
            targetCurrencyName: String): BigDecimal {
        val roundedAmount = amount.setScale(2, RoundingMode.HALF_UP)

        if (sourceCurrencyName == targetCurrencyName) {
            return roundedAmount
        }
        requireNotNull(Datastore.currencyStore[sourceCurrencyName]) { buildCurrencyNameError(sourceCurrencyName) }

        requireNotNull(Datastore.currencyStore[targetCurrencyName]) { buildCurrencyNameError(targetCurrencyName) }

        val exchangeRate = exchangeRateRepository.findBySourceAndTargetCurrencies(
                sourceCurrencyName,
                targetCurrencyName)
                ?: throw IllegalArgumentException(buildInvalidExchangeRateError(
                        sourceCurrencyName,
                        targetCurrencyName
                ))

        return roundedAmount.multiply(BigDecimal(exchangeRate.rate)).setScale(2, RoundingMode.HALF_UP)
    }
}