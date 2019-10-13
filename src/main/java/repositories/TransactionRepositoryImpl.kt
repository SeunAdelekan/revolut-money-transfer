package repositories

import components.Database
import models.entities.Transaction


class TransactionRepositoryImpl : TransactionRepository, BaseRepositoryImpl<Transaction>() {

    override fun findById(id: String): Transaction? = Database.transactionStore[id]

    override fun findByPage(page: Int, limit: Int): List<Transaction> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun save(entity: Transaction): Transaction {
        Database.transactionStore[entity.id] = entity
        return entity
    }

    override fun findByAccountId(accountId: String, page: Int, limit: Int): List<Transaction> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}