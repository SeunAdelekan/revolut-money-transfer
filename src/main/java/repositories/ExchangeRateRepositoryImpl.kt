package repositories

import components.Datastore
import models.entities.ExchangeRate
import javax.xml.crypto.Data

class ExchangeRateRepositoryImpl : ExchangeRateRepository {

    override fun findBySourceAndTargetCurrencies(sourceCurrencyName: String, targetCurrencyName: String): ExchangeRate? {
        val key = Pair(sourceCurrencyName, targetCurrencyName)
        return Datastore.exchangeRateStore[key]
    }

    override fun save(entity: ExchangeRate): ExchangeRate {
        val key = Pair(entity.sourceCurrency.name, entity.targetCurrency.name)
        Datastore.exchangeRateStore[key] = entity
        return entity
    }

    override fun countRecords(): Int = Datastore.exchangeRateStore.size
}