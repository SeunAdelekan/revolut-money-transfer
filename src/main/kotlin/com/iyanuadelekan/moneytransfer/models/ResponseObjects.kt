package com.iyanuadelekan.moneytransfer.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.iyanuadelekan.moneytransfer.constants.TransactionCategory
import com.iyanuadelekan.moneytransfer.constants.TransactionType
import com.iyanuadelekan.moneytransfer.serializers.BigDecimalSerializer
import com.iyanuadelekan.moneytransfer.serializers.DateSerializer
import java.math.BigDecimal
import java.util.*

class AccountVO {
    lateinit var id: String
    lateinit var accountName: String
    @JsonSerialize(using = BigDecimalSerializer::class)
    lateinit var balance: BigDecimal
    lateinit var status: String
    lateinit var currency: CurrencyVO
}

class CurrencyVO {
    lateinit var id: String
    lateinit var name: String
}

class TransactionVO {

    lateinit var id: String

    @JsonSerialize(using = BigDecimalSerializer::class)
    lateinit var amount: BigDecimal

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var senderAccountId: String? = null

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var recipientAccountId: String? = null
    // Unique reference for any given transaction
    lateinit var transactionReference: String

    // Session reference tying all debit/credit legs of a transfer
    lateinit var sessionReference: String

    lateinit var type: TransactionType

    lateinit var category: TransactionCategory

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var description: String? = null

    @JsonSerialize(using = BigDecimalSerializer::class)
    lateinit var balanceBefore: BigDecimal

    @JsonSerialize(using = BigDecimalSerializer::class)
    lateinit var balanceAfter: BigDecimal

    @JsonSerialize(using = DateSerializer::class)
    lateinit var createdAt: Date
}

class TransactionOperationVO {
    lateinit var account: AccountVO
    lateinit var transaction: TransactionVO
}

@JsonPropertyOrder(value = ["status"])
open class SuccessResponse(open val data: Any? = null) {
    val status = "success"
}

@JsonPropertyOrder(value = ["status"])
data class ErrorResponse(val errorMessage: String = "", val errorCode: String = "") {
    val status = "error"
}

class AccountOperationResponse(override val data: AccountVO? = null) : SuccessResponse(data)
class GetTransactionResponse(override val data: ArrayList<TransactionVO>? = null) : SuccessResponse(data)
class GetAccountsResponse(override val data: ArrayList<AccountVO>? = null) : SuccessResponse(data)
class MoneyOperationsResponse(override val data: TransactionOperationVO? = null) : SuccessResponse(data)
