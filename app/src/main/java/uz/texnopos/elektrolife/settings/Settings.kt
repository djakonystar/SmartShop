package uz.texnopos.elektrolife.settings

import android.content.Context
import android.content.SharedPreferences

class Settings(context: Context) {

    companion object {
        const val SIGNED_IN = "signedIn"
        const val TOKEN = "accessToken"
        const val USERNAME = "username"
        const val USER_ROLE = "notROLE"
        const val PRODUCT = "product"
        const val PIN = "pin"
        const val USD_UZS = "usdToUzs"
        const val FIRST_RUN = "firstRun"
        const val LANGUAGE = "language"
        const val CURRENCY = "currency"

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
        set(value) = preferences.edit().putString(USER_ROLE,value).apply()
        get() = preferences.getString(USER_ROLE,"")?: ""

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
}