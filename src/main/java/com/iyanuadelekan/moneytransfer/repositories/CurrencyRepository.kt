package com.iyanuadelekan.moneytransfer.repositories

import com.iyanuadelekan.moneytransfer.models.entities.Currency

interface CurrencyRepository : BaseRepository<Currency> {

    fun findByName(name: String): Currency?
}