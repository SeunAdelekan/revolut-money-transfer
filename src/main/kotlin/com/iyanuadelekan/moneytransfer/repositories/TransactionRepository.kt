package com.iyanuadelekan.moneytransfer.repositories

import com.iyanuadelekan.moneytransfer.models.entities.Transaction

interface TransactionRepository : BaseRepository<Transaction> {

    fun findById(id: String): Transaction?
}