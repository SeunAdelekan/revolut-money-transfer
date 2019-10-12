package repositories

import components.Database
import models.entities.ExchangeRate

class ExchangeRateRepositoryImpl : ExchangeRateRepository {

    override fun findBySourceAndTargetCurrencies(sourceCurrencyId: String, targetCurrencyId: String): ExchangeRate? {
        val key = Pair(sourceCurrencyId, targetCurrencyId)
        return Database.exchangeRateStore[key]
    }

    override fun save(entity: ExchangeRate): ExchangeRate {
        val key = Pair(entity.sourceCurrency.name, entity.targetCurrency.name)
        Database.exchangeRateStore[key] = entity
        return entity
    }
}