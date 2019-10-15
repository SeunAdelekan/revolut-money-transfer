package com.iyanuadelekan.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.iyanuadelekan.moneytransfer.*
import com.iyanuadelekan.moneytransfer.components.Datastore
import com.iyanuadelekan.moneytransfer.constants.Currency
import com.iyanuadelekan.moneytransfer.constants.RequestError
import com.iyanuadelekan.moneytransfer.constants.TransactionCategory
import com.iyanuadelekan.moneytransfer.constants.TransactionType
import com.iyanuadelekan.moneytransfer.helpers.generateUUID
import com.iyanuadelekan.moneytransfer.models.*
import io.javalin.Javalin
import kong.unirest.HttpResponse
import kong.unirest.JsonNode
import kong.unirest.Unirest
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AccountControllerTest {

    companion object {
        private const val PORT = 5000
        private lateinit var app: Javalin
        private lateinit var objectMapper: ObjectMapper
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

    @Before
    fun clearData() {
        with (Datastore) {
            clearAccounts()
            clearTransactions()
        }
    }

    @Test
    fun `Test account creation with invalid content type`() {
        val data = AccountData("John Wick", Currency.GBP.name)
        val response = Unirest
                .post("${BASE_URL}/accounts")
                .header("content-type", "text/plain").body(data).asJson()

        assertEquals(415, response.status)
        val error = objectMapper.readValue(response.body.toString(), ErrorResponse::class.java)

        with (error) {
            assertEquals("error", status)
            assertEquals("Unsupported HTTP content type", errorMessage)
            assertEquals(RequestError.UNSUPPORTED_CONTENT_TYPE.code, errorCode)
        }
    }

    @Test
    fun `Test account creation with invalid account name`() {
        val data = AccountData("JJ", Currency.GBP.name)
        val response = Unirest
                .post("${BASE_URL}/accounts")
                .header("content-type", "application/json").body(data).asJson()

        assertEquals(400, response.status)
        val error = objectMapper.readValue(response.body.toString(), ErrorResponse::class.java)
        val message = "Request body as AccountData invalid - Account name must be a minimum of 3 characters in length."
        with (error) {
            assertEquals("error", status)
            assertEquals(message, errorMessage)
            assertEquals(RequestError.INVALID_PARAMETER.code, errorCode)
        }
    }

    @Test
    fun `Test account creation with unsupported currency`() {
        val data = AccountData("John Wick", "GUP")
        val response = Unirest
                .post("${BASE_URL}/accounts")
                .header("content-type", "application/json").body(data).asJson()

        assertEquals(400, response.status)
        val errorData = objectMapper.readValue(response.body.toString(), ErrorResponse::class.java)
        val message = "Request body as AccountData invalid - That currency is not supported at the moment"
        with (errorData) {
            assertEquals("error", status)
            assertEquals(errorMessage, message)
            assertEquals(RequestError.INVALID_PARAMETER.code, errorCode)
        }
    }

    @Test
    fun `Test account creation with valid parameters`() {
        val accountData = AccountData("John Wick", Currency.GBP.name)
        val response = Unirest
                .post("${BASE_URL}/accounts")
                .header("content-type", "application/json").body(accountData).asJson()

        assertEquals(201, response.status)
        val responseBody = objectMapper.readValue(response.body.toString(), AccountOperationResponse::class.java)

        assertEquals(responseBody.status, "success")
        assertNotNull(responseBody.data)

        with (responseBody.data) {
            assertEquals(accountData.accountName, this?.accountName)
            assertEquals(BigDecimal("0.00"), this?.balance)
            assertEquals("enabled", this?.status)
            assertEquals("GBP", this?.currency?.name)
        }
    }

    @Test
    fun `Test funding of account with invalid content type`() {
        val data = TransactionOperationData(BigDecimal(1.00), Currency.GBP.name)
        val response = Unirest
                .post("${BASE_URL}/accounts/89e0670ef15b4d13a8289e0c616d2983/deposits")
                .header("content-type", "text/plain").body(data).asJson()

        assertEquals(415, response.status)
        val error = objectMapper.readValue(response.body.toString(), ErrorResponse::class.java)

        with (error) {
            assertEquals("error", status)
            assertEquals("Unsupported HTTP content type", errorMessage)
            assertEquals(RequestError.UNSUPPORTED_CONTENT_TYPE.code, errorCode)
        }
    }

    @Test
    fun `Test funding of account with invalid account ID`() {
        val accountId = "89e0670ef15b4d13a8289e0c616d2983"
        val data = TransactionOperationData(BigDecimal(500.00), Currency.GBP.name)
        val response = Unirest
                .post("${BASE_URL}/accounts/$accountId/deposits")
                .header("content-type", "application/json").body(data).asJson()

        assertEquals(400, response.status)
        val error = objectMapper.readValue(response.body.toString(), ErrorResponse::class.java)
        val message = "Path parameter 'account_id' with value '$accountId' invalid - An account with that ID does not exist."
        with (error) {
            assertEquals("error", status)
            assertEquals(message, errorMessage)
            assertEquals(RequestError.INVALID_PARAMETER.code, errorCode)
        }
    }

    @Test
    fun `Test funding of account with amount equal to 0`() {
        val account = createAccount("Olu Akinkugbe", Currency.GBP.name)

        val data = TransactionOperationData(BigDecimal(0.00), Currency.GBP.name)
        val response = Unirest
                .post("${BASE_URL}/accounts/${account.id}/deposits")
                .header("content-type", "application/json").body(data).asJson()

        assertEquals(400, response.status)
        val error = objectMapper.readValue(response.body.toString(), ErrorResponse::class.java)

        with (error) {
            assertEquals("error", status)
            assertEquals("Request body as TransactionOperationData invalid " +
                    "- Transaction amounts must be greater than 0.00", errorMessage)
            assertEquals(RequestError.INVALID_PARAMETER.code, errorCode)
        }
    }

    @Test
    fun `Test funding of account with amount below 0`() {
        val account = createAccount("Fela Kuti", Currency.GBP.name)
        val data = TransactionOperationData(BigDecimal(-2500.00), Currency.GBP.name)

        val response = Unirest
                .post("${BASE_URL}/accounts/${account.id}/deposits")
                .header("content-type", "application/json").body(data).asJson()

        assertEquals(400, response.status)
        val error = objectMapper.readValue(response.body.toString(), ErrorResponse::class.java)

        with (error) {
            assertEquals("error", status)
            assertEquals("Request body as TransactionOperationData invalid " +
                    "- Transaction amounts must be greater than 0.00", errorMessage)
            assertEquals(RequestError.INVALID_PARAMETER.code, errorCode)
        }
    }

    @Test
    fun `Test account retrieval with invalid account id`() {
        val accountId = generateUUID()
        val response = fetchAccountData(accountId)

        assertEquals(400, response.status)
        val error = objectMapper.readValue(response.body.toString(), ErrorResponse::class.java)

        with (error) {
            assertEquals("error", status)
            assertEquals("Path parameter 'account_id' with value '$accountId' invalid - " +
                    "An account with that ID does not exist.", errorMessage)
            assertEquals(RequestError.INVALID_PARAMETER.code, errorCode)
        }
    }

    @Test
    fun `Test account retrieval with valid account id`() {
        val account = createAccount("Seun Kuti", Currency.NGN.name)

        val response = fetchAccountData(account.id)

        assertEquals(200, response.status)
        val responseBody = objectMapper.readValue(response.body.toString(), AccountOperationResponse::class.java)

        assertEquals(responseBody.status, "success")
        assertNotNull(responseBody.data)

        with (responseBody.data) {
            assertEquals(account.accountName, this?.accountName)
            assertEquals(BigDecimal("0.00"), this?.balance)
            assertEquals("enabled", this?.status)
            assertEquals(account.id, this?.id)
            assertEquals(account.currency.name, this?.currency?.name)
        }
    }

    @Test
    fun `Test account transaction retrieval with invalid account id`() {
        val accountId = generateUUID()
        val response = fetchAccountTransactions(accountId)

        assertEquals(400, response.status)
        val error = objectMapper.readValue(response.body.toString(), ErrorResponse::class.java)

        with (error) {
            assertEquals("error", status)
            assertEquals("Path parameter 'account_id' with value '$accountId' invalid - " +
                    "An account with that ID does not exist.", errorMessage)
            assertEquals(RequestError.INVALID_PARAMETER.code, errorCode)
        }
    }

    @Test
    fun `Test account transaction retrieval with valid account id with no transactions`() {
        val account = createAccount("Seun Kuti", Currency.NGN.name)
        val response = fetchAccountTransactions(account.id)

        assertEquals(200, response.status)
        val responseBody = objectMapper.readValue(response.body.toString(), GetTransactionResponse::class.java)

        assertEquals(responseBody.status, "success")
        assertNotNull(responseBody.data)

        with (responseBody.data) {
            assertEquals(this?.size, 0)
        }
    }

    @Test
    fun `Test account transaction retrieval with valid account id with transactions`() {
        val account = createAccount("Seun Kuti", Currency.NGN.name)
        fundAccount(account.id, BigDecimal(1000.00), Currency.NGN.name)

        val response = fetchAccountTransactions(account.id)

        assertEquals(200, response.status)
        val responseBody = objectMapper.readValue(response.body.toString(), GetTransactionResponse::class.java)

        assertEquals(responseBody.status, "success")
        assertNotNull(responseBody.data)

        with (responseBody.data as List<TransactionVO>) {
            assertTrue(this.isNotEmpty())
        }
    }

    @Test
    fun `Test account list retrieval`() {
        createAccount("Test Account 1", Currency.NGN.name)
        createAccount("Test Account 2", Currency.NGN.name)

        val response = fetchAccounts(1, 2)
        assertEquals(200, response.status)
        val responseBody = objectMapper.readValue(response.body.toString(), GetAccountsResponse::class.java)

        assertEquals(responseBody.status, "success")
        assertNotNull(responseBody.data)

        with (responseBody.data as List<AccountVO>) {
            assertTrue(this.isNotEmpty())
            assertEquals(2, this.size)
        }
    }

    @Test
    fun `Test money transfer with invalid content type`() {
        val accountOne = createAccount("Test Account 1", Currency.GBP.name)
        val accountTwo = createAccount("Test Account 2", Currency.GBP.name)

        fundAccount(accountOne.id,  BigDecimal("1000.00"), Currency.GBP.name)
        val transactionDescription = "Pocket Money"

        val transactionData = TransactionOperationData(
                BigDecimal("20.00"),
                Currency.GBP.name,
                transactionDescription)

        val response = Unirest
                .post("$BASE_URL/accounts/${accountOne.id}/transfers/${accountTwo.id}")
                .header("content-type", "text/html").body(transactionData).asJson()

        val responseBody = objectMapper.readValue(response.body.toString(), ErrorResponse::class.java)

        with (responseBody) {
            assertEquals("error", status)
            assertEquals("Unsupported HTTP content type", errorMessage)
            assertEquals(RequestError.UNSUPPORTED_CONTENT_TYPE.code, errorCode)
        }
    }

    @Test
    fun `Test money transfer with valid sender and recipient`() {
        val accountOne = createAccount("Test Account 1", Currency.GBP.name)
        val accountTwo = createAccount("Test Account 2", Currency.GBP.name)

        fundAccount(accountOne.id,  BigDecimal("1000.00"), Currency.GBP.name)
        val transactionDescription = "Pocket Money"

        val response = transferMoney(
                BigDecimal("20.00"),
                accountOne.id,
                accountTwo.id,
                Currency.GBP.name,
                transactionDescription)

        val responseBody = objectMapper.readValue(response.body.toString(), TransferMoneyResponse::class.java)

        with (responseBody) {
            assertEquals("success", status)
            assertTrue(responseBody.data != null)
            val data = responseBody.data as TransferVO

            assertEquals(accountOne.id, data.account.id)
            assertEquals(BigDecimal("980.00"), data.account.balance)
            assertEquals(accountOne.accountName, data.account.accountName)
            assertEquals(accountOne.status, data.account.status)
            assertEquals(accountOne.createdAt, data.account.createdAt)


            assertEquals(TransactionType.DEBIT, data.transaction.type)
            assertEquals(TransactionCategory.BANK_TRANSFER, data.transaction.category)
            assertEquals(BigDecimal("1000.00"), data.transaction.balanceBefore)
            assertEquals(BigDecimal("980.00"), data.transaction.balanceAfter)
            assertEquals(transactionDescription, data.transaction.description)
        }
    }

    @Test
    fun `Test withdrawal of account with invalid content type`() {
        val accountId = "89e0670ef15b4d13a8289e0c616d2983"
        val data = TransactionOperationData(BigDecimal(1.00), Currency.GBP.name)
        val response = Unirest
                .post("${BASE_URL}/accounts/${accountId}/withdrawals")
                .header("content-type", "text/plain").body(data).asJson()

        assertEquals(415, response.status)
        val error = objectMapper.readValue(response.body.toString(), ErrorResponse::class.java)

        with (error) {
            assertEquals("error", status)
            assertEquals("Unsupported HTTP content type", errorMessage)
            assertEquals(RequestError.UNSUPPORTED_CONTENT_TYPE.code, errorCode)
        }
    }

    @Test
    fun `Test withdrawal of account with invalid account ID`() {
        val accountId = "89e0670ef15b4d13a8289e0c616d2983"
        val response = withdrawAccount(accountId, BigDecimal(500.00), Currency.GBP.name)

        assertEquals(400, response.status)
        val error = objectMapper.readValue(response.body.toString(), ErrorResponse::class.java)
        val message = "Path parameter 'account_id' with value '$accountId' invalid - An account with that ID does not exist."
        with (error) {
            assertEquals("error", status)
            assertEquals(message, errorMessage)
            assertEquals(RequestError.INVALID_PARAMETER.code, errorCode)
        }
    }

    @Test
    fun `Test withdrawal of account with amount equal to 0`() {
        val account = createAccount("Olu Akinkugbe", Currency.GBP.name)
        val response = withdrawAccount(account.id, BigDecimal(0.00), Currency.GBP.name)

        assertEquals(400, response.status)
        val error = objectMapper.readValue(response.body.toString(), ErrorResponse::class.java)

        with (error) {
            assertEquals("error", status)
            assertEquals("Request body as TransactionOperationData invalid " +
                    "- Transaction amounts must be greater than 0.00", errorMessage)
            assertEquals(RequestError.INVALID_PARAMETER.code, errorCode)
        }
    }

    @Test
    fun `Test withdrawal of account with amount below 0`() {
        val account = createAccount("Fela Kuti", Currency.GBP.name)
        val response = withdrawAccount(account.id, BigDecimal(-2500.00), Currency.GBP.name)

        assertEquals(400, response.status)
        val error = objectMapper.readValue(response.body.toString(), ErrorResponse::class.java)

        with (error) {
            assertEquals("error", status)
            assertEquals("Request body as TransactionOperationData invalid " +
                    "- Transaction amounts must be greater than 0.00", errorMessage)
            assertEquals(RequestError.INVALID_PARAMETER.code, errorCode)
        }
    }

    @Test
    fun `Test withdrawal of account with amount greater than account balance`() {
        val account = createAccount("Fela Kuti", Currency.GBP.name)
        val response = withdrawAccount(account.id, BigDecimal(2500.00), Currency.GBP.name)

        assertEquals(400, response.status)
        val error = objectMapper.readValue(response.body.toString(), ErrorResponse::class.java)

        with (error) {
            assertEquals("error", status)
            assertEquals("Cannot perform transaction of 2500.00 due to insufficient balance.", errorMessage)
            assertEquals(RequestError.INSUFFICIENT_BALANCE.code, errorCode)
        }
    }

    @Test
    fun `Test withdrawal of account with valid parameters`() {
        val account = createAccount("Fela Kuti", Currency.GBP.name)
        fundAccount(account.id, BigDecimal("1000.00"), Currency.GBP.name)
        val response = withdrawAccount(account.id, BigDecimal(200.00), Currency.GBP.name)

        assertEquals(200, response.status)
        val responseData = objectMapper.readValue(response.body.toString(), AccountOperationResponse::class.java)

        assertEquals("success", responseData.status)
        with (responseData.data as AccountVO) {
            assertEquals(BigDecimal("800.00"), balance)
            assertEquals("enabled", status)
        }
    }

    private fun transferMoney(
            amount: BigDecimal,
            senderId: String,
            recipientId: String,
            currencyName: String,
            description: String? = null): HttpResponse<JsonNode> {
        val transactionData = TransactionOperationData(amount, currencyName, description)

        return Unirest
                .post("$BASE_URL/accounts/$senderId/transfers/$recipientId")
                .header("content-type", "application/json").body(transactionData).asJson()
    }

    private fun withdrawAccount(accountId: String, amount: BigDecimal, currencyName: String): HttpResponse<JsonNode> {
        val data = TransactionOperationData(amount, currencyName)

        return Unirest
                .post("${BASE_URL}/accounts/${accountId}/withdrawals")
                .header("content-type", "application/json").body(data).asJson()
    }

    private fun fetchAccounts(page: Int = 1, limit: Int = 50): HttpResponse<JsonNode> {
        return Unirest
                .get("${BASE_URL}/accounts?page=$page&limit=$limit")
                .header("content-type", "application/json").asJson()
    }

    private fun fundAccount(accountId: String, amount: BigDecimal, currencyName: String): HttpResponse<JsonNode> {
        val data = TransactionOperationData(amount, currencyName)

        return Unirest
                .post("${BASE_URL}/accounts/$accountId/deposits")
                .header("content-type", "application/json").body(data).asJson()
    }

    private fun fetchAccountData(accountId: String): HttpResponse<JsonNode> {
        return Unirest
                .get("${BASE_URL}/accounts/$accountId")
                .asJson()
    }

    private fun fetchAccountTransactions(accountId: String): HttpResponse<JsonNode> {
        return Unirest
                .get("${BASE_URL}/accounts/$accountId/transactions")
                .asJson()
    }

    private fun createAccount(accountName: String, currencyName: String): AccountVO {
        val accountData = AccountData(accountName, currencyName)
        val response = Unirest
                .post("${BASE_URL}/accounts")
                .header("content-type", "application/json").body(accountData).asJson()
        val responseBody = objectMapper.readValue(response.body.toString(), AccountOperationResponse::class.java)

        return responseBody.data as AccountVO
    }
}