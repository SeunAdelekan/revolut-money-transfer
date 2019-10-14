package com.iyanuadelekan.moneytransfer.repositories

import com.iyanuadelekan.moneytransfer.models.entities.ExchangeRate

interface ExchangeRateRepository : BaseRepository<ExchangeRate> {

    fun findBySourceAndTargetCurrencies(sourceCurrencyName: String, targetCurrencyName: String): ExchangeRate?
}