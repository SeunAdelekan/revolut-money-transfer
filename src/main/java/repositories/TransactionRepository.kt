package repositories

import models.entities.Transaction

interface TransactionRepository : BaseRepository<Transaction> {

    fun findById(id: String): Transaction?

    fun findByPage(page: Int = 1, limit: Int = 50): List<Transaction>

    fun findByAccountId(accountId: String, page: Int, limit: Int): List<Transaction>
}