package services

import components.Database
import models.CurrencyData
import models.entities.Currency
import repositories.CurrencyRepository
import repositories.CurrencyRepositoryImpl
import java.math.BigDecimal

class CurrencyServiceImpl : CurrencyService, BaseServiceImpl() {

    private val currencyRepository: CurrencyRepository = CurrencyRepositoryImpl()

    override fun createCurrency(currencyDetails: CurrencyData): Currency {
        val currency = Currency(name = currencyDetails.name)
        return currencyRepository.save(currency)
    }

    override fun retrieveCurrency(currencyName: String): Currency? = currencyRepository.findByName(currencyName)

    override fun currencyExists(currencyName: String): Boolean = currencyRepository.findByName(currencyName) != null

    override fun getExchangeAmount(amount: BigDecimal, sourceCurrencyName: String, targetCurrencyName: String): BigDecimal {
        println(Pair(sourceCurrencyName, targetCurrencyName))
        return if (sourceCurrencyName == targetCurrencyName) {
            println(amount)
            amount
        } else {
            Database.currencyStore[sourceCurrencyName]
                    ?: throw IllegalArgumentException("Invalid currency name $sourceCurrencyName")
            Database.currencyStore[targetCurrencyName]
                    ?: throw IllegalArgumentException("Invalid currency name $targetCurrencyName")

            val exchangeRateKey = Pair(sourceCurrencyName, targetCurrencyName)
            val exchangeRate = Database.exchangeRateStore[exchangeRateKey]
                    ?: throw IllegalArgumentException(
                            "Exchange rate not defined for currency " +
                                    "source $sourceCurrencyName " +
                                    "and target $targetCurrencyName")

            println(amount.multiply(BigDecimal(exchangeRate.rate)))
            amount.multiply(BigDecimal(exchangeRate.rate))
        }
    }
}