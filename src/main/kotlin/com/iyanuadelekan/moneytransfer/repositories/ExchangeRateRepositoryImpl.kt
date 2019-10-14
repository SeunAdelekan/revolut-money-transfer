package com.iyanuadelekan.moneytransfer.repositories

import com.iyanuadelekan.moneytransfer.components.Datastore
import com.iyanuadelekan.moneytransfer.models.entities.ExchangeRate

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