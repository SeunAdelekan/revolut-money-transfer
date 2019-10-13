package services

import components.Datastore
import models.CurrencyData
import models.entities.Currency
import repositories.CurrencyRepository
import repositories.CurrencyRepositoryImpl
import repositories.ExchangeRateRepository
import repositories.ExchangeRateRepositoryImpl
import java.math.BigDecimal

class CurrencyServiceImpl : CurrencyService, BaseServiceImpl() {

    private val currencyRepository: CurrencyRepository = CurrencyRepositoryImpl()
    private val exchangeRateRepository: ExchangeRateRepository = ExchangeRateRepositoryImpl()

    override fun createCurrency(currencyDetails: CurrencyData): Currency {
        val currency = Currency(name = currencyDetails.name)
        return currencyRepository.save(currency)
    }

    override fun retrieveCurrency(currencyName: String): Currency? = currencyRepository.findByName(currencyName)

    override fun currencyExists(currencyName: String): Boolean = currencyRepository.findByName(currencyName) != null

    override fun getExchangeAmount(amount: BigDecimal, sourceCurrencyName: String, targetCurrencyName: String): BigDecimal {
        val normalizedAmount = amount.setScale(2, BigDecimal.ROUND_HALF_UP)

        println(Pair(sourceCurrencyName, targetCurrencyName))
        return if (sourceCurrencyName == targetCurrencyName) {
            println(normalizedAmount)
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

            println(normalizedAmount.multiply(BigDecimal(exchangeRate.rate).setScale(2, BigDecimal.ROUND_HALF_UP)))
            normalizedAmount.multiply(BigDecimal(exchangeRate.rate).setScale(2, BigDecimal.ROUND_HALF_UP))
        }
    }
}