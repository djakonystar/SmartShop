package uz.texnopos.elektrolife.ui.finance.income

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.CalendarHelper
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.*
import uz.texnopos.elektrolife.data.model.finance.FinancePost
import uz.texnopos.elektrolife.databinding.ActionBarBinding
import uz.texnopos.elektrolife.databinding.FragmentIncomeAddBinding
import uz.texnopos.elektrolife.settings.Settings
import uz.texnopos.elektrolife.settings.Settings.Companion.FINANCE_INCOME
import uz.texnopos.elektrolife.ui.finance.FinanceViewModel
import site.texnopos.djakonystar.suminputmask.SumInputMask
import java.text.SimpleDateFormat
import java.util.*

class IncomeAddFragment : Fragment(R.layout.fragment_income_add) {
    private lateinit var binding: FragmentIncomeAddBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var navController: NavController
    private val viewModel: FinanceViewModel by viewModel()
    private val settings: Settings by inject()
    private val calendarHelper = CalendarHelper()
    private val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
    private var dateInLong = calendarHelper.currentDateMillis
    private var date = simpleDateFormat.format(dateInLong)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentIncomeAddBinding.bind(view)
        abBinding = ActionBarBinding.bind(view)
        navController = findNavController()

        val categoryList = Constants.getFinanceCategories(requireContext())
        val paymentTypeList =
            listOf(getString(R.string.payment_cash), getString(R.string.payment_card))

        abBinding.apply {
            tvTitle.text = context?.getString(R.string.add_income)
            btnHome.onClick {
                navController.popBackStack()
            }
        }

        var selectedCategory = 1
        var selectedPaymentType = 1

        binding.apply {
            val categoryAdapter =
                ArrayAdapter(requireContext(), R.layout.item_spinner, categoryList)
            actCategory.setAdapter(categoryAdapter)
            actCategory.threshold = 100
            actCategory.setText(categoryList[0])
            actCategory.setOnItemClickListener { _, _, i, _ ->
                selectedCategory = i + 1
            }

            val paymentTypeAdapter =
                ArrayAdapter(requireContext(), R.layout.item_spinner, paymentTypeList)
            actPaymentType.setAdapter(paymentTypeAdapter)
            actPaymentType.threshold = 100
            actPaymentType.setText(paymentTypeList[0])
            actPaymentType.setOnItemClickListener { _, _, i, _ ->
                selectedPaymentType = i + 1
            }

            tilSum.suffixText = settings.currency
            SumInputMask(etSum)

            etDescription.addTextChangedListener {
                tilDescription.isErrorEnabled = false
            }
            etSum.addTextChangedListener {
                tilSum.isErrorEnabled = false
            }
            etDate.addTextChangedListener {
                tilDate.isErrorEnabled = false
            }

            etDate.setText(date)
            etDate.onClick {
                val datePickerDialog = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(context?.getString(R.string.choose_date_uz))
                    .setSelection(dateInLong)
                    .setCalendarConstraints(
                        CalendarConstraints.Builder()
                            .setEnd(System.currentTimeMillis())
                            .setValidator(DateValidatorPointBackward.now())
                            .build()
                    )
                    .build()

                datePickerDialog.addOnPositiveButtonClickListener {
                    dateInLong = it
                    date = simpleDateFormat.format(dateInLong)
                    etDate.setText(date)
                }

                datePickerDialog.addOnDismissListener {
                    tilDate.isEnabled = true
                }

                datePickerDialog.show(
                    requireActivity().supportFragmentManager,
                    datePickerDialog.tag
                )
                tilDate.isEnabled = false
            }

            btnAddExpense.onClick {
                val description = etDescription.text.toString()
                val payee = etPayee.text.toString()
                val sum = etSum.text.toString().toDouble

                if (description.isNotEmpty() && sum != 0.0) {
                    val expense = FinancePost(
                        description = description,
                        date = date.changeDateFormat,
                        categoryId = selectedCategory,
                        payee = payee,
                        price = sum,
                        type = FINANCE_INCOME,
                        paymentType = if (selectedPaymentType == 1) "cash" else "card"
                    )
                    viewModel.addFinanceDetail(expense)
                } else {
                    if (description.isEmpty()) {
                        tilDescription.error = getString(R.string.required_field)
                    }
                    if (sum == 0.0) {
                        tilSum.error = getString(R.string.required_field)
                    }
                }
            }
        }

        setUpObservers()
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            scrollView.isEnabled = !loading
        }
    }

    private fun setUpObservers() {
        viewModel.financePost.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    showSuccess(context?.getString(R.string.success))
                    navController.popBackStack()
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }
    }
}
