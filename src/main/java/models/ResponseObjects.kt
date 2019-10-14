package models

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import models.entities.Account
import models.entities.Transaction

data class TransferVO(val account: Account, val transaction: Transaction)

@JsonPropertyOrder(value = ["status"])
open class SuccessResponse(open val data: Any? = null) {
    val status = "success"
}

@JsonPropertyOrder(value = ["status"])
data class ErrorResponse(val errorMessage: String = "", val errorCode: String = "") {
    val status = "error"
}

class AccountCreationResponse(override val data: Account? = null) : SuccessResponse(data)