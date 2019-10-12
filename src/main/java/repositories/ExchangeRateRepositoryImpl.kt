package repositories

import components.Database
import models.entities.ExchangeRate

class ExchangeRateRepositoryImpl : ExchangeRateRepository {

    override fun findBySourceAndTargetCurrencies(sourceCurrencyId: Long, targetCurrencyId: Long): ExchangeRate? {
        val key = Pair(sourceCurrencyId, targetCurrencyId)
        return Database.exchangeRateStore[key]
    }

    override fun save(entity: ExchangeRate): ExchangeRate {
        val key = Pair(entity.sourceCurrency.id, entity.targetCurrency.id)
        Database.exchangeRateStore[key] = entity
        return entity
    }
}