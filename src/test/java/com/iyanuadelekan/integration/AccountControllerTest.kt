package com.iyanuadelekan.integration

import RequestError
import com.fasterxml.jackson.databind.ObjectMapper
import io.javalin.Javalin
import kong.unirest.Unirest
import models.AccountCreationResponse
import models.AccountData
import models.ErrorResponse
import models.TransactionOperationData
import models.entities.Account
import models.entities.Currency
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import repositories.AccountRepositoryImpl
import repositories.CurrencyRepositoryImpl
import startApp
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AccountControllerTest {

    companion object {
        private lateinit var app: Javalin
        private lateinit var objectMapper: ObjectMapper
        private const val PORT = 5000
        private const val BASE_URL = "http://localhost:${PORT}"

        @BeforeClass
        @JvmStatic
        fun init() {
            app = startApp(PORT)
            objectMapper = ObjectMapper()
        }

        @AfterClass
        @JvmStatic
        fun stopServer() {
            app.stop()
        }
    }

    private val accountRepository = AccountRepositoryImpl()
    private val currencyRepository = CurrencyRepositoryImpl()

    @Test
    fun `Test account creation with invalid content type`() {
        val data = AccountData("John Wick", "GBP")
        val response = Unirest
                .post("${BASE_URL}/accounts")
                .header("content-type", "text/plain").body(data).asJson()
        assertEquals(415, response.status)
        print(response.body.toString())
        val error = objectMapper.readValue(response.body.toString(), ErrorResponse::class.java)
        with (error) {
            assertEquals(status, "error")
            assertEquals(errorMessage, "Unsupported HTTP content type")
            assertEquals(errorCode, RequestError.UNSUPPORTED_CONTENT_TYPE.code)
        }
    }

    @Test
    fun `Test account creation with invalid account name`() {
        val data = AccountData("JJ", "GBP")
        val response = Unirest
                .post("${BASE_URL}/accounts")
                .header("content-type", "application/json").body(data).asJson()

        assertEquals(400, response.status)
        print(response.body.toString())
        val error = objectMapper.readValue(response.body.toString(), ErrorResponse::class.java)

        with (error) {
            assertEquals(status, "error")
            assertEquals(errorMessage, "Account name must be a minimum of 3 characters in length.")
            assertEquals(errorCode, RequestError.INVALID_PARAMETER.code)
        }
    }

    @Test
    fun `Test account creation with unsupported currency`() {
        val data = AccountData("John Wick", "USD")
        val response = Unirest
                .post("${BASE_URL}/accounts")
                .header("content-type", "application/json").body(data).asJson()

        assertEquals(400, response.status)
        print(response.body.toString())
        val errorData = objectMapper.readValue(response.body.toString(), ErrorResponse::class.java)

        with (errorData) {
            assertEquals(status, "error")
            assertEquals(errorMessage, "That currency is not supported at the moment")
            assertEquals(errorCode, RequestError.INVALID_PARAMETER.code)
        }
    }

    @Test
    fun `Test account creation with valid parameters`() {
        val accountData = AccountData("John Wick", "GBP")
        val response = Unirest
                .post("${BASE_URL}/accounts")
                .header("content-type", "application/json").body(accountData).asJson()
        assertEquals(201, response.status)

        val responseBody = objectMapper.readValue(response.body.toString(), AccountCreationResponse::class.java)
        assertEquals(responseBody.status, "success")
        assertNotNull(responseBody.data)

        with (responseBody.data) {
            assertEquals(accountData.accountName, this?.accountName)
            assertEquals(BigDecimal("0.00"), this?.balance)
            assertEquals("enabled", this?.status)
            assertEquals("GBP", this?.currency?.name)
            assertEquals(mutableListOf(), this?.transactions)
        }
    }

    @Test
    fun `Test funding of account with invalid content type`() {
        val data = TransactionOperationData(BigDecimal(1.00), "GBP")
        val response = Unirest
                .post("${BASE_URL}/accounts/89e0670ef15b4d13a8289e0c616d2983/deposits")
                .header("content-type", "text/plain").body(data).asJson()
        assertEquals(415, response.status)
        print(response.body.toString())
        val error = objectMapper.readValue(response.body.toString(), ErrorResponse::class.java)
        with (error) {
            assertEquals(status, "error")
            assertEquals(errorMessage, "Unsupported HTTP content type")
            assertEquals(errorCode, RequestError.UNSUPPORTED_CONTENT_TYPE.code)
        }
    }

    @Test
    fun `Test funding of account with invalid account ID`() {
        val accountId = "89e0670ef15b4d13a8289e0c616d2983"
        val data = TransactionOperationData(BigDecimal(500.00), "GBP")
        val response = Unirest
                .post("${BASE_URL}/accounts/$accountId/deposits")
                .header("content-type", "application/json").body(data).asJson()
        assertEquals(400, response.status)
        print(response.body.toString())
        val error = objectMapper.readValue(response.body.toString(), ErrorResponse::class.java)
        with (error) {
            assertEquals(status, "error")
            assertEquals(errorMessage, "account with id $accountId does not exist")
            assertEquals(errorCode, RequestError.INVALID_PARAMETER.code)
        }
    }

    @Test
    fun `Test funding of account with amount equal to 0`() {
        val account = Account("Olu Akinkugbe")
        account.currency = currencyRepository.findByName("GBP") as Currency
        accountRepository.save(account)

        val data = TransactionOperationData(BigDecimal(0.00), "GBP")
        val response = Unirest
                .post("${BASE_URL}/accounts/${account.id}/deposits")
                .header("content-type", "application/json").body(data).asJson()
        assertEquals(400, response.status)
        print(response.body.toString())
        val error = objectMapper.readValue(response.body.toString(), ErrorResponse::class.java)
        with (error) {
            assertEquals("error", status)
            assertEquals("Transaction amounts must be greater than 0.0", errorMessage)
            assertEquals(RequestError.INVALID_PARAMETER.code, errorCode)
        }
    }

    @Test
    fun `Test funding of account with amount below 0`() {
        val account = Account("Fela Kuti")
        account.currency = currencyRepository.findByName("GBP") as Currency
        accountRepository.save(account)

        val data = TransactionOperationData(BigDecimal(-2500.00), "GBP")

        val response = Unirest
                .post("${BASE_URL}/accounts/${account.id}/deposits")
                .header("content-type", "application/json").body(data).asJson()
        assertEquals(400, response.status)
        print(response.body.toString())
        val error = objectMapper.readValue(response.body.toString(), ErrorResponse::class.java)
        with (error) {
            assertEquals("error", status)
            assertEquals("Transaction amounts must be greater than 0.0", errorMessage)
            assertEquals(RequestError.INVALID_PARAMETER.code, errorCode)
        }
    }
}