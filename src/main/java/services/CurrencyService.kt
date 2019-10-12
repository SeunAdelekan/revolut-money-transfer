package services

import models.CurrencyData
import models.entities.Currency
import java.math.BigDecimal

interface CurrencyService {

    fun createCurrency(currencyDetails: CurrencyData): Currency

    fun retrieveCurrency(currencyName: String): Currency?

    fun currencyExists(currencyName: String): Boolean

    fun getExchangeAmount(amount: BigDecimal, sourceCurrency: String, targetCurrency: String): BigDecimal
}