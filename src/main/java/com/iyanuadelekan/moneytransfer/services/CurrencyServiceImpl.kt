package com.iyanuadelekan.moneytransfer.services

import com.iyanuadelekan.moneytransfer.components.Datastore
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

    override fun getExchangeAmount(amount: BigDecimal, sourceCurrencyName: String, targetCurrencyName: String): BigDecimal {
        val normalizedAmount = amount.setScale(2, RoundingMode.HALF_UP)
        return if (sourceCurrencyName == targetCurrencyName) {
            normalizedAmount
        } else {
            Datastore.currencyStore[sourceCurrencyName]
                    ?: throw IllegalArgumentException("Invalid currency name $sourceCurrencyName")
            Datastore.currencyStore[targetCurrencyName]
                    ?: throw IllegalArgumentException("Invalid currency name $targetCurrencyName")

            val exchangeRate = exchangeRateRepository.findBySourceAndTargetCurrencies(
                    sourceCurrencyName, targetCurrencyName)
                    ?: throw IllegalArgumentException(
                            "Exchange rate not defined for currency " +
                                    "source $sourceCurrencyName " +
                                    "and target $targetCurrencyName")
            normalizedAmount.multiply(BigDecimal(exchangeRate.rate)).setScale(2, RoundingMode.HALF_UP)
        }
    }
}