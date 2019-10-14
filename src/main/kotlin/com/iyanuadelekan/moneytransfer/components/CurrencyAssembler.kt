package com.iyanuadelekan.moneytransfer.components

import com.iyanuadelekan.moneytransfer.models.CurrencyVO
import com.iyanuadelekan.moneytransfer.models.entities.Currency

class CurrencyAssembler {

    fun toCurrencyVO(currency: Currency): CurrencyVO {
        val currencyVO = CurrencyVO()
        with (currencyVO) {
            id = currency.id
            name = currency.name

            return this
        }
    }
}