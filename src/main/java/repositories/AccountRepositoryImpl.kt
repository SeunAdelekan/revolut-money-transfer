package repositories

import components.Datastore
import getPage
import models.entities.Account

class AccountRepositoryImpl : AccountRepository {

    override fun findById(id: String): Account? = Datastore.accountStore[id]

    override fun findByPage(page: Int, limit: Int): List<Account>
            = Datastore.accountStore.values.toList().getPage(page, limit)

    override fun save(entity: Account): Account {
        Datastore.accountStore[entity.id] = entity
        return entity
    }

    override fun exists(accountId: String): Boolean = findById(accountId) != null

    override fun countRecords(): Int = Datastore.accountStore.size
}