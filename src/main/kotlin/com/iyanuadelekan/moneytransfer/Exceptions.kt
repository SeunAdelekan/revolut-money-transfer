package com.iyanuadelekan.moneytransfer

import java.math.BigDecimal

class UnsupportedContentTypeException(contentType: String?) :
        RuntimeException("Content type $contentType not currently supported")

class InsufficientBalanceException(amount: BigDecimal):
        RuntimeException("Cannot perform transaction of $amount due to insufficient balance.")