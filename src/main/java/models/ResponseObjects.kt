package models

import models.entities.Account
import models.entities.Transaction

data class TransferVO(val account: Account, val transaction: Transaction)

data class SuccessResponse(val data: Any) {
    val status = "success"
}

data class ErrorResponse(val errorMessage: String, val errorCode: String) {
    val status = "error"
}