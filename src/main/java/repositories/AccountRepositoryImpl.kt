package repositories

import components.Database
import models.entities.Account

class AccountRepositoryImpl : AccountRepository {

    override fun findById(id: String): Account? = Database.accountStore[id]

    override fun findByPage(page: Int, limit: Int): List<Account> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun save(entity: Account): Account {
        Database.accountStore[entity.id] = entity
        return entity
    }
}