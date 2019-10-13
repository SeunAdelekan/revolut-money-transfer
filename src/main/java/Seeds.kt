import models.entities.Currency
import models.entities.ExchangeRate
import repositories.CurrencyRepositoryImpl
import repositories.ExchangeRateRepositoryImpl

private val currencyRepository = CurrencyRepositoryImpl()
private val exchangeRateRepository = ExchangeRateRepositoryImpl()

fun seedCurrencies() {
    val naira = Currency("NGN")
    val pound = Currency("GBP")
    currencyRepository.save(naira)
    currencyRepository.save(pound)
}

fun seedExchangeRates() {
    val naira = currencyRepository.findByName("NGN")
    val pound = currencyRepository.findByName("GBP")

    if (naira != null && pound != null) {
        val nairaToPounds = ExchangeRate(naira, pound, 0.0022)
        val poundsToNaira = ExchangeRate(pound, naira, 456.66)
        exchangeRateRepository.save(nairaToPounds)
        exchangeRateRepository.save(poundsToNaira)
    }
}