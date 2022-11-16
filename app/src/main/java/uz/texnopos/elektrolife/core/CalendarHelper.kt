package uz.texnopos.elektrolife.core

import java.text.SimpleDateFormat
import java.util.*

class CalendarHelper {
    private val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
    private val currentDateInMillis: Long
        get() = System.currentTimeMillis()

    val currentDate: String
        get() {
            simpleDateFormat.timeZone = TimeZone.getDefault()
            return simpleDateFormat.format(currentDateInMillis)
        }

    val currentDateMillis =
        currentDateInMillis + TimeZone.getDefault().getOffset(currentDateInMillis)

    val firstDayOfCurrentMonth: String
        get() {
            val c = Calendar.getInstance(TimeZone.getTimeZone("UTC+5"))
            c.timeInMillis = currentDateInMillis - TimeZone.getDefault().getOffset(currentDateInMillis)
            c[Calendar.DAY_OF_MONTH] = c.getActualMinimum(Calendar.DAY_OF_MONTH)
            return simpleDateFormat.format(c.timeInMillis)
        }

    val firstDayOfCurrentMonthMillis: Long
        get() {
            val c = Calendar.getInstance(TimeZone.getTimeZone("UTC+5"))
            c.timeInMillis = currentDateInMillis - TimeZone.getDefault().getOffset(currentDateInMillis)
            c[Calendar.DAY_OF_MONTH] = c.getActualMinimum(Calendar.DAY_OF_MONTH)
            return c.timeInMillis
        }

    val lastDayOfCurrentMonth: String
        get() {
            val c = Calendar.getInstance(TimeZone.getTimeZone("UTC+5"))
            c.timeInMillis = currentDateInMillis - TimeZone.getDefault().getOffset(currentDateInMillis)
            c[Calendar.DAY_OF_MONTH] = c.getActualMaximum(Calendar.DAY_OF_MONTH)
            return simpleDateFormat.format(c.timeInMillis)
        }

    val lastDayOfCurrentMonthMillis: Long
        get() {
            val c = Calendar.getInstance(TimeZone.getTimeZone("UTC+5"))
            c.timeInMillis = currentDateInMillis - TimeZone.getDefault().getOffset(currentDateInMillis)
            c[Calendar.DAY_OF_MONTH] = c.getActualMaximum(Calendar.DAY_OF_MONTH)
            return c.timeInMillis
        }
}