package models

data class SuccessResponse(val data: Any) {
    val status = "success"
}

data class ErrorResponse(val errorMessage: String, val errorCode: String) {
    val status = "error"
}