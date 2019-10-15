package com.iyanuadelekan.unit.services

import com.iyanuadelekan.moneytransfer.seedCurrencies
import com.iyanuadelekan.moneytransfer.seedExchangeRates
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