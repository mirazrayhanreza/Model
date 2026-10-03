package com.example.data

data class CountryCodeItem(
    val name: String,
    val dialCode: String,
    val code: String, // ISO code
    val flag: String, // Emoji Flag
    val currencyCode: String = "USD"
)

val WORLDWIDE_COUNTRIES: List<CountryCodeItem> = CountryPaymentMaster.allCountries.map { country ->
    CountryCodeItem(
        name = country.countryName,
        dialCode = country.phoneCode,
        code = country.isoCode,
        flag = country.flag,
        currencyCode = country.currencyCode
    )
}

