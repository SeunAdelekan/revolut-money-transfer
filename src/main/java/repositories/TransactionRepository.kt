package repositories

import models.entities.Transaction

interface TransactionRepository : BaseRepository<Transaction> {

    fun findById(id: Long): Transaction?
}