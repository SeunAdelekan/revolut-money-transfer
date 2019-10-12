class UnsupportedContentTypeException(val contentType: String) :
        RuntimeException("Content type $contentType not currently supported")

class InvalidUserIdException(val userId: Long) :
        RuntimeException("No matching user resource with id $userId")

class InvalidCurrencyIdException(val currencyId: Long) :
        RuntimeException("No matching currency with id $currencyId")

class InvalidParameterException(message: String): RuntimeException(message)
