package repositories

import models.entities.Currency
import models.entities.ExchangeRate

interface ExchangeRateRepository : BaseRepository<ExchangeRate> {

    fun findBySourceAndTargetCurrencies(sourceCurrencyName: String, targetCurrencyName: String): ExchangeRate?
}