package repositories

import components.Database
import models.entities.Transaction


class TransactionRepositoryImpl : TransactionRepository, BaseRepositoryImpl<Transaction>() {

    override fun findById(id: Long): Transaction? = Database.transactionStore[id]

    override fun save(entity: Transaction): Transaction {
        Database.transactionStore[entity.id] = entity
        return entity
    }
}