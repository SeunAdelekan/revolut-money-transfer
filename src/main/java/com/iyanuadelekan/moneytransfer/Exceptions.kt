package com.iyanuadelekan.moneytransfer

import java.math.BigDecimal

class UnsupportedContentTypeException(contentType: String?) :
        RuntimeException("Content type $contentType not currently supported")

class InvalidUserIdException(val userId: Long) :
        RuntimeException("No matching user resource with id $userId")

class InvalidParameterException(message: String): RuntimeException(message)

class InsufficientBalanceException(amount: BigDecimal):
        RuntimeException("Cannot perform transaction of $amount due to insufficient balance.")