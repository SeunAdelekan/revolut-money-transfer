package com.iyanuadelekan.moneytransfer.repositories

import com.iyanuadelekan.moneytransfer.components.Datastore
import com.iyanuadelekan.moneytransfer.models.entities.Currency

class CurrencyRepositoryImpl : CurrencyRepository {

    override fun findByName(name: String): Currency? = Datastore.currencyStore[name]

    override fun save(entity: Currency): Currency {
        Datastore.currencyStore[entity.name] = entity
        return entity
    }

    override fun countRecords(): Int = Datastore.currencyStore.size
}