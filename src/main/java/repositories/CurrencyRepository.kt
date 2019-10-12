package repositories

import models.entities.Currency

interface CurrencyRepository : BaseRepository<Currency> {

    fun findByName(name: String): Currency?
}