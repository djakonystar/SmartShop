package uz.texnopos.elektrolife.ui.newsale.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import uz.texnopos.elektrolife.core.extensions.onClick
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.MaskWatcherPrice
import uz.texnopos.elektrolife.databinding.DialogAddPaymentBinding
import java.text.SimpleDateFormat
import java.util.*

class AddPaymentDialog(private val totalPrice: Long) : DialogFragment() {
    private lateinit var binding: DialogAddPaymentBinding
    private var type = MutableLiveData<String>()
    private var date = ""
    private var dateForBackend = ""
    private var dateInLong = System.currentTimeMillis()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.shape_dialog)
        return inflater.inflate(R.layout.dialog_add_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DialogAddPaymentBinding.bind(view)

        observe()
        val list = arrayListOf(
            context?.getString(R.string.payment_cash),
            context?.getString(R.string.payment_card),
            context?.getString(R.string.payment_debt),
            context?.getString(R.string.payment_mix)
        )

        binding.apply {
            actSpinner.setAdapter(ArrayAdapter(requireContext(), R.layout.item_spinner, list))
            actSpinner.setOnFocusChangeListener { _, b ->
                if (b) {
                    actSpinner.showDropDown()
                }
                tilSpinner.isErrorEnabled = false
            }
            actSpinner.setOnItemClickListener { _, _, i, _ ->
                type.postValue(list[i])
            }

            etCash.addTextChangedListener(MaskWatcherPrice(etCash))
            etCard.addTextChangedListener(MaskWatcherPrice(etCard))

            etCash.addTextChangedListener {
                tilCash.isErrorEnabled = false
            }
            etCard.addTextChangedListener {
                tilCard.isErrorEnabled = false
            }
            etDate.addTextChangedListener {
                tilDate.isErrorEnabled = false
            }

            etDate.onClick {
                val datePickerDialog = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(context?.getString(R.string.choose_date_uz))
                    .setSelection(dateInLong)
                    .setCalendarConstraints(
                        CalendarConstraints.Builder()
                            .setStart(System.currentTimeMillis())
                            .setValidator(DateValidatorPointForward.now())
                            .build()
                    )
                    .build()

                datePickerDialog.addOnPositiveButtonClickListener {
                    dateInLong = it
                    date = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT).format(dateInLong)
                    dateForBackend = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(dateInLong)
                    etDate.setText(date)
                }

                datePickerDialog.show(requireActivity().supportFragmentManager, "DatePicker")
            }

            btnAdd.onClick {
                checkAndSend()
            }

            btnCancel.onClick {
                dismiss()
            }
        }
    }

    private fun String.getOnlyDigits(): String {
        val s = this.filter { it.isDigit() }
        return if (s.isEmpty()) "0" else s
    }

    private fun observe() {
        type.observe(requireActivity(), {
            binding.apply {
                when (it) {
                    context?.getString(R.string.payment_cash) -> {
                        tilCash.isVisible = true
                        tilCard.isVisible = false
                        tilDate.isVisible = true
                        tilComment.isVisible = true
                    }
                    context?.getString(R.string.payment_card) -> {
                        tilCash.isVisible = false
                        tilCard.isVisible = true
                        tilDate.isVisible = true
                        tilComment.isVisible = true
                    }
                    context?.getString(R.string.payment_debt) -> {
                        tilCash.isVisible = false
                        tilCard.isVisible = false
                        tilDate.isVisible = true
                        tilComment.isVisible = true
                    }
                    context?.getString(R.string.payment_mix) -> {
                        tilCash.isVisible = true
                        tilCard.isVisible = true
                        tilDate.isVisible = true
                        tilComment.isVisible = true
                    }
                }
            }
        })
    }

    private fun checkAndSend() {
        type.observe(requireActivity(), {
            var cash = 0L
            var card = 0L
            var debt: Long
            var dateRequired = false
            var comment: String
            binding.apply {
                when (it) {
                    context?.getString(R.string.payment_cash) -> {
                        if (etCash.text.toString().isNotEmpty()) {
                            cash = etCash.text.toString().getOnlyDigits().toLong()
                        } else {
                            tilCash.error = context?.getString(R.string.required_field)
                        }
                        debt = totalPrice - cash
                        if (debt > 0) {
                            dateRequired = true
                        }
                        comment = etComment.text.toString()
                        if (dateRequired) {
                            if (date.isEmpty()) {
                                tilDate.error = context?.getString(R.string.required_field)
                            } else {
                                sendDate.invoke(cash, card, debt, dateForBackend, comment)
                                dismiss()
                            }
                        } else {
                            sendDate.invoke(cash, card, debt, dateForBackend, comment)
                            dismiss()
                        }
                    }
                    context?.getString(R.string.payment_card) -> {
                        if (etCard.text.toString().isNotEmpty()) {
                            card = etCard.text.toString().getOnlyDigits().toLong()
                        } else {
                            tilCard.error = context?.getString(R.string.required_field)
                        }
                        debt = totalPrice - card
                        if (debt > 0) {
                            dateRequired = true
                        }
                        comment = etComment.text.toString()
                        if (dateRequired) {
                            if (date.isEmpty()) {
                                tilDate.error = context?.getString(R.string.required_field)
                            } else {
                                sendDate.invoke(cash, card, debt, dateForBackend, comment)
                                dismiss()
                            }
                        } else {
                            sendDate.invoke(cash, card, debt, dateForBackend, comment)
                            dismiss()
                        }
                    }
                    context?.getString(R.string.payment_debt) -> {
                        debt = totalPrice
                        if (debt > 0) {
                            dateRequired = true
                        }
                        comment = etComment.text.toString()
                        if (dateRequired) {
                            if (date.isEmpty()) {
                                tilDate.error = context?.getString(R.string.required_field)
                            } else {
                                sendDate.invoke(cash, card, debt, dateForBackend, comment)
                                dismiss()
                            }
                        } else {
                            sendDate.invoke(cash, card, debt, dateForBackend, comment)
                            dismiss()
                        }
                    }
                    context?.getString(R.string.payment_mix) -> {
                        if (etCash.text.toString().isNotEmpty()) {
                            cash = etCash.text.toString().getOnlyDigits().toLong()
                        } else {
                            tilCash.error = context?.getString(R.string.required_field)
                        }
                        if (etCard.text.toString().isNotEmpty()) {
                            card = etCard.text.toString().getOnlyDigits().toLong()
                        } else {
                            tilCard.error = context?.getString(R.string.required_field)
                        }
                        debt = totalPrice - (card + cash)
                        if (debt > 0) {
                            dateRequired = true
                        }
                        comment = etComment.text.toString()
                        if (dateRequired) {
                            if (date.isEmpty()) {
                                tilDate.error = context?.getString(R.string.required_field)
                            } else {
                                sendDate.invoke(cash, card, debt, dateForBackend, comment)
                                dismiss()
                            }
                        } else {
                            sendDate.invoke(cash, card, debt, dateForBackend, comment)
                            dismiss()
                        }
                    }
                }
            }
        })
    }

    private var sendDate: (cash: Long, card: Long, debt: Long, date: String, comment: String) -> Unit =
        { _: Long, _: Long, _: Long, _: String, _: String -> }

    fun setDate(sendDate: (cash: Long, card: Long, debt: Long, date: String, comment: String) -> Unit) {
        this.sendDate = sendDate
    }
}