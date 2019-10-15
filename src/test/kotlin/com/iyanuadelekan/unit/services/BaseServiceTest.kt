package com.iyanuadelekan.unit.services

import com.iyanuadelekan.moneytransfer.helpers.seedCurrencies
import com.iyanuadelekan.moneytransfer.helpers.seedExchangeRates
import org.junit.BeforeClass

abstract class BaseServiceTest {

    companion object {

        @JvmStatic
        @BeforeClass
        fun seedDB() {
            seedCurrencies()
            seedExchangeRates()
        }
    }
}