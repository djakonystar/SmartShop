package uz.texnopos.elektrolife.settings

import android.content.Context
import android.content.SharedPreferences

class Settings(context: Context) {

    companion object {
        private const val SIGNED_IN = "signedIn"
        private const val TOKEN = "accessToken"
        private const val USERNAME = "username"
        private const val USER_ROLE = "notROLE"
        private const val PRODUCT = "product"
        private const val PIN = "pin"
        private const val USD_UZS = "usdToUzs"
        private const val FIRST_RUN = "firstRun"
        private const val LANGUAGE = "language"
        private const val CURRENCY = "currency"
        private const val USER_ID = "userId"
        private const val BASE_URL = "baseUrl"
        private const val LANG_SELECTED = "languageSelected"
        private const val SHOP_SELECTED = "shopSelected"
        private const val COMPANY_CONFIGURED = "companyConfigured"
        private const val COMPANY_NAME = "companyName"
        private const val COMPANY_ADDRESS = "address"
        private const val COMPANY_PHONE = "companyPhone"
        private const val COMPANY_PREFIX = "companyPrefix"

        /**
         * Finance type on creating new **Expense**
         */
        const val FINANCE_EXPENSE = "consumption"
        const val FINANCE_INCOME = "income"
    }

    private val preferences: SharedPreferences =
        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

    var signedIn: Boolean
        set(value) {
            preferences.edit().putBoolean(SIGNED_IN, value).apply()
        }
        get() = preferences.getBoolean(SIGNED_IN, false)

    var token: String
        set(value) {
            preferences.edit().putString(TOKEN, value).apply()
        }
        get() = preferences.getString(TOKEN, "") ?: ""

    var username: String
        set(value) {
            preferences.edit().putString(USERNAME, value).apply()
        }
        get() = preferences.getString(USERNAME, "") ?: ""

    var pincode: String
        set(value) {
            preferences.edit().putString(PIN, value).apply()
        }
        get() = preferences.getString(PIN, "") ?: ""

    var role: String
        set(value) = preferences.edit().putString(USER_ROLE, value).apply()
        get() = preferences.getString(USER_ROLE, "") ?: ""

    var userId: Int
        set(value) = preferences.edit().putInt(USER_ID, value).apply()
        get() = preferences.getInt(USER_ID, 0)

    var usdToUzs: Float
        set(value) {
            preferences.edit().putFloat(USD_UZS, value).apply()
        }
        get() = preferences.getFloat(USD_UZS, 0.0f)

    var firstRun: Boolean
        set(value) = preferences.edit().putBoolean(FIRST_RUN, value).apply()
        get() = preferences.getBoolean(FIRST_RUN, true)

    var language: String
        set(value) = preferences.edit().putString(LANGUAGE, value).apply()
        get() = preferences.getString(LANGUAGE, "ru") ?: "ru"

    var currency: String
        set(value) = preferences.edit().putString(CURRENCY, value).apply()
        get() = preferences.getString(CURRENCY, "UZS") ?: "UZS"

    var baseUrl: String
        set(value) = preferences.edit().putString(BASE_URL, value).apply()
        get() = preferences.getString(BASE_URL, "https://example.com") ?: "https://example.com"

    var langSelected: Boolean
        set(value) = preferences.edit().putBoolean(LANG_SELECTED, value).apply()
        get() = preferences.getBoolean(LANG_SELECTED, false)

    var shopSelected: Boolean
        set(value) = preferences.edit().putBoolean(SHOP_SELECTED, value).apply()
        get() = preferences.getBoolean(SHOP_SELECTED, false)

    var companyConfigured: Boolean
        set(value) = preferences.edit().putBoolean(COMPANY_CONFIGURED, value).apply()
        get() = preferences.getBoolean(COMPANY_CONFIGURED, false)

    var companyName: String
        set(value) = preferences.edit().putString(COMPANY_NAME, value).apply()
        get() = preferences.getString(COMPANY_NAME, "") ?: ""

    var companyAddress: String
        set(value) = preferences.edit().putString(COMPANY_ADDRESS, value).apply()
        get() = preferences.getString(COMPANY_ADDRESS, "") ?: ""

    var companyPhone: String
        set(value) = preferences.edit().putString(COMPANY_PHONE, value).apply()
        get() = preferences.getString(COMPANY_PHONE, "") ?: ""

    var prefix: String
        set(value) = preferences.edit().putString(COMPANY_PREFIX, value).apply()
        get() = preferences.getString(COMPANY_PREFIX, "") ?: ""

    fun reset() {
        preferences.edit().clear().apply()
    }
}
