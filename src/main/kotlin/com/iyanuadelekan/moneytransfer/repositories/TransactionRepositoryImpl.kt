package com.iyanuadelekan.moneytransfer.repositories

import com.iyanuadelekan.moneytransfer.components.Datastore
import com.iyanuadelekan.moneytransfer.models.entities.Transaction


class TransactionRepositoryImpl : TransactionRepository {

    override fun findById(id: String): Transaction? = Datastore.transactionStore[id]

    override fun save(entity: Transaction): Transaction {
        Datastore.transactionStore[entity.id] = entity
        return entity
    }

    override fun countRecords(): Int = Datastore.transactionStore.size
}