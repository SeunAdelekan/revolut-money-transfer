package repositories

import components.Database
import models.entities.Currency

class CurrencyRepositoryImpl : CurrencyRepository {

    override fun findByName(name: String): Currency? = Database.currencyStore[name]

    override fun save(entity: Currency): Currency {
        Database.currencyStore[entity.name] = entity
        return entity
    }
}