package models

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import models.entities.Account
import models.entities.Transaction

data class TransferVO(val account: Account, val transaction: Transaction)

@JsonPropertyOrder(value = ["status"])
data class SuccessResponse(val data: Any) {
    val status = "success"
}

@JsonPropertyOrder(value = ["status"])
data class ErrorResponse(val errorMessage: String, val errorCode: String) {
    val status = "error"
}