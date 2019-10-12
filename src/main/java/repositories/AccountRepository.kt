package repositories

import models.entities.Account

interface AccountRepository : BaseRepository<Account> {

    fun findById(id: Long): Account?

    fun findByPage(page: Int = 1, limit: Int = 50): List<Account>
}