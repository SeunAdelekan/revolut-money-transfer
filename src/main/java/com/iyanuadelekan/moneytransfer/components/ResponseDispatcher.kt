package com.iyanuadelekan.moneytransfer.components

import io.javalin.http.Context
import com.iyanuadelekan.moneytransfer.models.ErrorResponse
import com.iyanuadelekan.moneytransfer.models.SuccessResponse

object ResponseDispatcher {

    fun sendSuccess(ctx: Context, data: Any, httpStatus: Int = 200) = with (ctx) {
        status(httpStatus)
        json(SuccessResponse(data))
    }

    fun sendError(ctx: Context, errorMessage: String, errorCode: String, httpStatus: Int = 400) = with (ctx) {
        status(httpStatus)
        json(ErrorResponse(errorMessage, errorCode))
    }
}