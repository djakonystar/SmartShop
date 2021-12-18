package uz.texnopos.elektrolife.ui.newpayment.dialog


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.databinding.DialogCalendarBinding
import java.util.*

class CalendarDialog(context: Context) : Dialog(context) {
    lateinit var binding: DialogCalendarBinding

    private var onDateSelected: (date: String) -> Unit = {}
    fun onDateSelectedListener(onDateSelected: (date: String) -> Unit) {
        this.onDateSelected = onDateSelected
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            cvCalendar.maxDate = System.currentTimeMillis()
            val cal = Calendar.getInstance()
            btnYes.onClick {
                val y = cvCalendar.year
                val m = cvCalendar.month
                val d = cvCalendar.dayOfMonth
                val yStr = y.toString()
                var mStr = (m + 1).toString()
                var dStr = d.toString()
                if (dStr.length != 2) dStr = "0$dStr"
                if (mStr.length != 2) mStr = "0$mStr"
                cal.set(Calendar.DAY_OF_MONTH, d)
                cal.set(Calendar.MONTH, m)
                cal.set(Calendar.YEAR, y)
                onDateSelected.invoke("$yStr-$mStr-$dStr")
                dismiss()
            }
            btnCancel.onClick {
                dismiss()
            }
        }
    }
}