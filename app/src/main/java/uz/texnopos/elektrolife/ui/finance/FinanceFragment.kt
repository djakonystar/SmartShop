package uz.texnopos.elektrolife.ui.finance

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.showMessage
import uz.texnopos.elektrolife.core.extensions.toSumFormat
import uz.texnopos.elektrolife.databinding.ActionBarBinding
import uz.texnopos.elektrolife.databinding.FragmentFinanceBinding

class FinanceFragment : Fragment(R.layout.fragment_finance) {
    private lateinit var binding: FragmentFinanceBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var navController: NavController
    private val viewModel: FinanceViewModel by viewModel()
    private var lastSum = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentFinanceBinding.bind(view)
        abBinding = ActionBarBinding.bind(view)
        navController = findNavController()

        abBinding.apply {
            tvTitle.text = context?.getString(R.string.finance)
            btnHome.onClick {
                navController.popBackStack()
            }
        }

        binding.apply {
            cardSales.onClick {
                navController.navigate(R.id.action_financeFragment_to_salesFragment)
            }
            cardIncomes.onClick {
                navController.navigate(R.id.action_financeFragment_to_incomeFragment)
            }
            cardExpenses.onClick {
                navController.navigate(R.id.action_financeFragment_to_expenseFragment)
            }
        }

        viewModel.getCashboxBalance()
        setUpObservers()
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            cardSales.isEnabled = !loading
            cardIncomes.isEnabled = !loading
            cardExpenses.isEnabled = !loading
        }
    }

    private fun setUpObservers() {
        viewModel.balance.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    if (it.data!!.successful) {
                        val newSum = it.data.payload.balance
                        startAnimationCounter(lastSum, newSum)
                        lastSum = newSum
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

    @SuppressLint("SetTextI18n")
    private fun startAnimationCounter(start: Double, end: Double) {
        val animator = ValueAnimator.ofFloat(start.toFloat(), end.toFloat())
        animator.addUpdateListener {
            val newValue = "%.2f".format((it.animatedValue as Float).toDouble())
                .replace(',', '.').toDouble().toSumFormat
            binding.tvCashboxMoney.text = context?.getString(
                R.string.sum_text,
                newValue
            )
        }
        animator.duration = 500
        animator.start()
    }
}
