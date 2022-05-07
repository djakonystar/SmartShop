package uz.texnopos.elektrolife.ui.main

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.showError
import uz.texnopos.elektrolife.databinding.FragmentMainBinding
import uz.texnopos.elektrolife.settings.Settings
import uz.texnopos.elektrolife.ui.currency.CurrencyViewModel
import uz.texnopos.elektrolife.ui.main.dialog.LangDialog
import uz.texnopos.elektrolife.ui.newsale.Basket
import uz.texnopos.elektrolife.ui.qrscanner.QrScannerFragment

class MainFragment : Fragment(R.layout.fragment_main) {
    private lateinit var binding: FragmentMainBinding
    private lateinit var navController: NavController
    private val currencyViewModel: CurrencyViewModel by viewModel()
    private val settings: Settings by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Basket.clear()
        binding = FragmentMainBinding.bind(view)
        navController = findNavController()

        binding.apply {
            val logoResId = resources.getIdentifier(
                "logo",
                "drawable",
                requireActivity().packageName
            )
            if (logoResId != 0) {
                ivLogo.setImageResource(logoResId)
            }

            clients.onClick {
                navController.navigate(R.id.action_mainFragment_to_clientsFragment)
            }
            returnOrder.onClick {
                navController.navigate(
                    MainFragmentDirections.actionMainFragmentToQrScannerFragment(QrScannerFragment.GET_BASKET)
                )
            }
            warehouse.onClick {
                navController.navigate(R.id.action_mainFragment_to_warehouseFragment)
            }

            ivOther.onClick {
//                optionsMenu(it)
                navController.navigate(MainFragmentDirections.actionMainFragmentToNewCategoryFragment())
            }
            ivLang.onClick {
                val dialog = LangDialog()
                dialog.show(requireActivity().supportFragmentManager, "LangDialog")
            }
            newSale.onClick {
                navController.navigate(MainFragmentDirections.actionMainFragmentToNewSaleFragment("null"))
            }
            newProduct.onClick {
                navController.navigate(R.id.action_mainFragment_to_newProductFragment)
            }

            when (settings.role) {
                "saller" -> {
                    iconFinance.setImageResource(R.drawable.sales)
                    titleFinance.text = context?.getString(R.string.sales)
                    finance.onClick {
                        navController.navigate(R.id.action_mainFragment_to_salesFragment)
                    }
                    iconNewProduct.setImageResource(R.drawable.salary)
                    titleNewProduct.text = getString(R.string.salaries)
                    newProduct.onClick {
                        navController.navigate(
                            MainFragmentDirections.actionMainFragmentToSalaryDetailFragment(
                                employeeId = settings.userId,
                                employeeName = "null"
                            )
                        )
                    }
                }
                "admin", "ceo" -> {
                    newProduct.isEnabled = true
                    ivOther.isVisible = false
                    finance.onClick {
                        navController.navigate(R.id.action_mainFragment_to_financeFragment)
                    }
                }
                else -> showError("Error with roles")
            }
        }

        currencyViewModel.getCurrency()
        setUpObservers()
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
        }
    }

    private fun setUpObservers() {
        currencyViewModel.currency.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    it.data!!.forEach { currency ->
                        when (currency.code) {
                            "USD" -> currency.rate.forEach { rate ->
                                when (rate.code) {
                                    "UZS" -> settings.usdToUzs = rate.rate
                                }
                            }
                        }
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }
    }
}
