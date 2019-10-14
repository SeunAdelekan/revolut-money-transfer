package com.iyanuadelekan.moneytransfer.components

import com.iyanuadelekan.moneytransfer.models.AccountVO
import com.iyanuadelekan.moneytransfer.models.entities.Account

class AccountAssembler {

    private val currencyAssembler = CurrencyAssembler()

    fun toAccountVO(account: Account): AccountVO {
        val accountVO = AccountVO()
        with (accountVO) {
            id = account.id
            accountName = account.accountName
            balance = account.balance
            status = account.status
            createdAt = account.createdAt
            updatedAt = account.updatedAt
            currency = currencyAssembler.toCurrencyVO(account.currency)
            return this
        }
    }

    fun toAccountListVO(accounts: List<Account>): List<AccountVO> {
        return accounts.map(this::toAccountVO)
    }
}