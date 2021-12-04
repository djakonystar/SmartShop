package uz.texnopos.chandelierwarehouse.core

import java.text.SimpleDateFormat
import java.util.*

class CalendarHelper {
    private val currentDateInMillis: Long
        get() = System.currentTimeMillis()

    val currentDate: String
        get() {
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
            return sdf.format(currentDateInMillis)
        }

    val firstDayOfCurrentMonth: String
        get() {
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
            val convertedDate = dateFormat.parse(currentDate)
            val c = Calendar.getInstance()
            c.time = convertedDate!!
            c[Calendar.DAY_OF_MONTH] = c.getActualMinimum(Calendar.DAY_OF_MONTH)
            return dateFormat.format(c.timeInMillis)
        }

    val lastDayOfCurrentMonth: String
        get() {
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
            val convertedDate = dateFormat.parse(currentDate)
            val c = Calendar.getInstance()
            c.time = convertedDate!!
            c[Calendar.DAY_OF_MONTH] = c.getActualMaximum(Calendar.DAY_OF_MONTH)
            return dateFormat.format(c.timeInMillis)
        }
}