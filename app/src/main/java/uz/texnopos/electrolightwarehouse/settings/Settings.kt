package uz.texnopos.electrolightwarehouse.settings

import android.content.Context
import android.content.SharedPreferences

class Settings(context: Context) {

    companion object {
        const val SIGNED_IN = "signedIn"
        const val TOKEN = "accessToken"
        const val USERNAME = "username"
        const val PRODUCT = "product"
        const val PIN = "pin"
    }

    private val preferences: SharedPreferences =
        context.getSharedPreferences("WarehousePreferences", Context.MODE_PRIVATE)

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
}