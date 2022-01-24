package uz.texnopos.elektrolife.ui.finance.expense

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
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.MaskWatcherPaymentDecimal
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.changeDateFormat
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.showMessage
import uz.texnopos.elektrolife.data.model.finance.FinancePost
import uz.texnopos.elektrolife.databinding.ActionBarBinding
import uz.texnopos.elektrolife.databinding.FragmentExpenseAddBinding
import uz.texnopos.elektrolife.settings.Settings.Companion.FINANCE_EXPENSE
import uz.texnopos.elektrolife.ui.finance.FinanceViewModel
import java.text.SimpleDateFormat
import java.util.*

class ExpenseAddFragment : Fragment(R.layout.fragment_expense_add) {
    private lateinit var binding: FragmentExpenseAddBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var navController: NavController
    private val viewModel: FinanceViewModel by viewModel()
    private var dateInLong = System.currentTimeMillis()
    private var date = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT).format(dateInLong)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentExpenseAddBinding.bind(view)
        abBinding = ActionBarBinding.bind(view)
        navController = findNavController()

        val categoryList = listOf(
            getString(R.string.expense_any),
            getString(R.string.expense_administrative),
            getString(R.string.expense_rent),
            getString(R.string.expense_salary),
            getString(R.string.expense_investments),
            getString(R.string.expense_office),
            getString(R.string.expense_taxes),
            getString(R.string.expense_household),
        )

        abBinding.apply {
            tvTitle.text = context?.getString(R.string.add_expense)
            btnHome.onClick {
                navController.popBackStack()
            }
        }

        var selectedCategory = -1

        binding.apply {
            val categoryAdapter =
                ArrayAdapter(requireContext(), R.layout.item_spinner, categoryList)
            actCategory.setAdapter(categoryAdapter)
            actCategory.setOnFocusChangeListener { _, _ ->
                actCategory.showDropDown()
            }
            actCategory.setOnItemClickListener { adapterView, _, i, _ ->
                tilCategory.isErrorEnabled = false
                if (adapterView.getItemAtPosition(i) != getString(R.string.not_selected)) {
                    selectedCategory = i + 3
                }
            }

            etSum.addTextChangedListener(MaskWatcherPaymentDecimal(etSum))

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
                tilDate.isEnabled = false
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
                    date = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT).format(dateInLong)
                    etDate.setText(date)
                    tilDate.isEnabled = true
                }

                datePickerDialog.addOnDismissListener {
                    tilDate.isEnabled = true
                }

                datePickerDialog.show(requireActivity().supportFragmentManager, "DatePicker")
            }

            btnAddExpense.onClick {
                val description = etDescription.text.toString()
                val payee = etPayee.text.toString()
                val sum = etSum.text.toString().filter { it.isDigit() || it == '.' }

                if (description.isNotEmpty() && selectedCategory != -1 && sum.isNotEmpty()) {
                    val expense = FinancePost(
                        description = description,
                        date = date.changeDateFormat,
                        categoryId = selectedCategory,
                        payee = payee,
                        price = sum.toDouble(),
                        type = FINANCE_EXPENSE
                    )
                    viewModel.addFinanceDetail(expense)
                } else {
                    if (description.isEmpty()) {
                        tilDescription.error = getString(R.string.required_field)
                    }
                    if (selectedCategory == -1) {
                        tilCategory.error = getString(R.string.required_field)
                    }
                    if (sum.isEmpty()) {
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
                    if (it.data!!.successful) {
                        showMessage(context?.getString(R.string.success))
                        navController.popBackStack()
                    } else {
                        showMessage(it.data.message)
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showMessage(it.message)
                }
            }
        }
    }
}
