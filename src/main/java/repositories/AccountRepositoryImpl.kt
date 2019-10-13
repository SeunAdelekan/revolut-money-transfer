package repositories

import components.Database
import getPage
import models.entities.Account

class AccountRepositoryImpl : AccountRepository {

    override fun findById(id: String): Account? = Database.accountStore[id]

    override fun findByPage(page: Int, limit: Int): List<Account>
            = Database.accountStore.values.toList().getPage(page, limit)

    override fun save(entity: Account): Account {
        Database.accountStore[entity.id] = entity
        return entity
    }

    override fun exists(accountId: String): Boolean = findById(accountId) != null
}