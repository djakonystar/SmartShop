package uz.texnopos.elektrolife.core.extensions

import android.content.Context
import uz.texnopos.elektrolife.R

object Constants {
    const val CLOUD_NAME = "http-electro-life-texnopos-site"
    const val ROLE_CEO = "ceo"
    const val ROLE_ADMIN = "admin"
    const val ROLE_SELLER = "saller"

    fun provideBaseUrls(): Map<String, String> = mapOf(
        "https://smart-shop.my-project.site" to "logo_test",
        "https://new-electro-life.texnopos.uz" to "logo_electro_life",
        "https://new-qoniratshop.texnopos.uz" to "logo_kungrad_shop",
        "https://sinyor.texnopos.uz" to "logo_sinyor",
        "https://raya-bijuteriya.texnopos.uz" to "logo_raya_bujiteriya",
        "https://mobishop.texnopos.uz" to "logo_mobi_shop",
        "https://ideal-magazin.texnopos.uz" to "logo_ideal_magazin",
        "https://imran.texnopos.uz" to "logo_test",
        "https://rolfshop.texnopos.uz" to "logo_rolfshop",
        "https://mobishop2.texnopos.uz" to "logo_mobi_shop",
        "https://takayama.texnopos.uz" to "logo_test",
        "https://johnny.texnopos.uz" to "johnny_logo"
    )

    fun getUnitName(context: Context, unitId: Int): String {
        return when (unitId) {
            1 -> context.getString(R.string.measure_pcs)
            2 -> context.getString(R.string.measure_tonne)
            3 -> context.getString(R.string.measure_kg)
            4 -> context.getString(R.string.measure_gr)
            5 -> context.getString(R.string.measure_meter)
            6 -> context.getString(R.string.measure_cm)
            7 -> context.getString(R.string.measure_liter)
            else -> "Error"
        }
    }

    fun getUnits(context: Context): List<String> {
        return listOf(
            context.getString(R.string.measure_pcs),
            context.getString(R.string.measure_tonne),
            context.getString(R.string.measure_kg),
            context.getString(R.string.measure_gr),
            context.getString(R.string.measure_meter),
            context.getString(R.string.measure_cm),
            context.getString(R.string.measure_liter)
        )
    }

    fun getMonthName(context: Context, id: Int): String {
        return when (id) {
            1 -> context.getString(R.string.month_first)
            2 -> context.getString(R.string.month_second)
            3 -> context.getString(R.string.month_third)
            4 -> context.getString(R.string.month_fourth)
            5 -> context.getString(R.string.month_fifth)
            6 -> context.getString(R.string.month_sixth)
            7 -> context.getString(R.string.month_seventh)
            8 -> context.getString(R.string.month_eighth)
            9 -> context.getString(R.string.month_ninth)
            10 -> context.getString(R.string.month_tenth)
            11 -> context.getString(R.string.month_eleventh)
            12 -> context.getString(R.string.month_twelfth)
            else -> "Error"
        }
    }

    fun getFinanceCategories(context: Context): List<String> {
        return listOf(
            context.getString(R.string.expense_any),
            context.getString(R.string.expense_administrative),
            context.getString(R.string.expense_rent),
            context.getString(R.string.expense_salary),
            context.getString(R.string.expense_investments),
            context.getString(R.string.expense_office),
            context.getString(R.string.expense_taxes),
            context.getString(R.string.expense_household),
        )
    }
}
