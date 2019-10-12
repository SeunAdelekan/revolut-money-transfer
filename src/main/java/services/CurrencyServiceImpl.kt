package services

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

    override fun getExchangeAmount(amount: BigDecimal, sourceCurrency: String, targetCurrency: String): BigDecimal {
        return if (sourceCurrency == targetCurrency) {
            amount
        } else {

            BigDecimal(0)
        }
    }
}