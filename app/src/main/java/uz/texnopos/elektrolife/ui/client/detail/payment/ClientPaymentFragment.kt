package uz.texnopos.elektrolife.ui.client.detail.payment

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.checkModule
import uz.texnopos.elektrolife.core.extensions.showError
import uz.texnopos.elektrolife.core.extensions.toSumFormat
import uz.texnopos.elektrolife.data.model.clients.Client
import uz.texnopos.elektrolife.databinding.FragmentClientPaymentBinding
import uz.texnopos.elektrolife.settings.Settings

class ClientPaymentFragment(private val client: Client) :
    Fragment(R.layout.fragment_client_payment) {
    private lateinit var binding: FragmentClientPaymentBinding
    private val viewModel: ClientPaymentViewModel by viewModel()
    private val adapter: ClientPaymentAdapter by inject()
    private val settings: Settings by inject()
    private var lastSum = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentClientPaymentBinding.bind(view)

        binding.apply {
            recyclerView.adapter = adapter

            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = false
                viewModel.getClientPayments(clientId = client.id)
            }
        }

        animateTotalSum(lastSum, lastSum)
        viewModel.getClientPayments(clientId = client.id)
        setUpObservers()
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            swipeRefresh.isEnabled = !loading
        }
    }

    private fun setUpObservers() {
        viewModel.clientPayments.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    if (it.data!!.successful) {
                        adapter.models = it.data.payload
                        var newSum = it.data.payload.sumOf { p -> p.cash }
                        newSum += it.data.payload.sumOf { p -> p.card }
                        animateTotalSum(lastSum, newSum.toDouble())
                        lastSum = newSum.toDouble()
                    } else {
                        showError(it.data.message)
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun animateTotalSum(start: Double, end: Double) {
        val animator = ValueAnimator.ofFloat(start.toFloat(), end.toFloat())
        animator.addUpdateListener {
            val newValue = "%.2f".format((it.animatedValue as Float).toDouble())
                .replace(',', '.').toDouble().checkModule.toSumFormat
            binding.tvTotalPrice.text = context?.getString(
                R.string.total_sum_text,
                newValue,
                settings.currency
            )
        }
        animator.duration = 300
        animator.start()
    }
}
