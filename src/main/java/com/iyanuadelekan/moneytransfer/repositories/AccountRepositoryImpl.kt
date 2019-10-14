package com.iyanuadelekan.moneytransfer.repositories

import com.iyanuadelekan.moneytransfer.components.Datastore
import com.iyanuadelekan.moneytransfer.getPage
import com.iyanuadelekan.moneytransfer.models.entities.Account

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