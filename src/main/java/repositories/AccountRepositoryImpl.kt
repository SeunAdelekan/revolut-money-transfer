package repositories

import components.Database
import models.entities.Account

class AccountRepositoryImpl : AccountRepository {

    override fun findById(id: Long): Account? {
        println(Database.accountStore)
        println(Database.accountStore[id])
        return Database.accountStore[id]
    }

    override fun findByPage(page: Int, limit: Int): List<Account> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun save(entity: Account): Account {
        println(entity)
        Database.accountStore[entity.id] = entity
        return entity
    }
}