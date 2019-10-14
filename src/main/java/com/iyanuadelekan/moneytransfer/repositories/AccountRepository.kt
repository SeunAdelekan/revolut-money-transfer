package com.iyanuadelekan.moneytransfer.repositories

import com.iyanuadelekan.moneytransfer.models.entities.Account

interface AccountRepository : BaseRepository<Account> {

    fun findById(id: String): Account?

    fun findByPage(page: Int = 1, limit: Int = 50): List<Account>

    fun exists(accountId: String): Boolean
}