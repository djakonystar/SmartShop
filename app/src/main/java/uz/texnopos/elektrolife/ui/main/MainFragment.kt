package uz.texnopos.elektrolife.ui.main

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.gson.GsonBuilder
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.showError
import uz.texnopos.elektrolife.databinding.FragmentMainBinding
import uz.texnopos.elektrolife.settings.Settings
import uz.texnopos.elektrolife.ui.currency.CurrencyViewModel
import uz.texnopos.elektrolife.ui.dialog.LangDialog
import uz.texnopos.elektrolife.ui.newsale.Basket
import uz.texnopos.elektrolife.ui.qrscanner.QrScannerFragment
import uz.texnopos.elektrolife.ui.qrscanner.QrScannerViewModel

class MainFragment : Fragment(R.layout.fragment_main) {
    private lateinit var binding: FragmentMainBinding
    private lateinit var navController: NavController
    private val currencyViewModel: CurrencyViewModel by viewModel()
    private val qrScannerViewModel: QrScannerViewModel by viewModel()
    private val settings: Settings by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Basket.clear()
        binding = FragmentMainBinding.bind(view)
        navController = findNavController()

        val currentBackStackEntry = navController.currentBackStackEntry
        val savedStateHandle = currentBackStackEntry?.savedStateHandle
        observeQrCodeResult(savedStateHandle)

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
                    MainFragmentDirections.actionMainFragmentToQrScannerFragment()
                )
            }
            warehouse.onClick {
                navController.navigate(R.id.action_mainFragment_to_warehouseFragment)
            }

            ivSettings.onClick {
                val langDialog = LangDialog()
                langDialog.show(requireActivity().supportFragmentManager, langDialog.tag)
            }
            newSale.onClick {
                navController.navigate(MainFragmentDirections.actionMainFragmentToNewSaleFragment())
            }
            newProduct.onClick {
                navController.navigate(R.id.action_mainFragment_to_newProductFragment)
            }

            fabQrScanner.onClick {
                navController.navigate(MainFragmentDirections.actionMainFragmentToQrScannerFragment())
            }

            when (settings.role) {
                "saller" -> {
                    iconFinance.setImageResource(R.drawable.ic_sales_3d_seller)
                    titleFinance.text = context?.getString(R.string.sales)
                    finance.onClick {
                        navController.navigate(R.id.action_mainFragment_to_salesFragment)
                    }
                    iconNewProduct.setImageResource(R.drawable.ic_salary_3d_new)
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
                    finance.onClick {
                        navController.navigate(R.id.action_mainFragment_to_financeFragment)
                    }
                }
                else -> showError("Error with roles")
            }
        }

//        currencyViewModel.getCurrency()
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

    private fun observeQrCodeResult(savedStateHandle: SavedStateHandle?) {
        savedStateHandle?.getLiveData<String>(QrScannerFragment.RESULT_TEXT)
            ?.observe(viewLifecycleOwner) { result ->
                if (result.startsWith(QrScannerFragment.BASKET)) {
                    val arguments = result.split("\n")
                    val type = arguments[0]
                    val uuid = arguments[1]
                    qrScannerViewModel.getBasket(type, uuid)
                    qrScannerViewModel.order.observe(viewLifecycleOwner) {
                        when (it.status) {
                            ResourceState.LOADING -> setLoading(true)
                            ResourceState.SUCCESS -> {
                                setLoading(false)
                                val basket = it.data!!
                                val basketString =
                                    GsonBuilder().setPrettyPrinting().create()
                                        .toJson(basket)
                                navController.navigate(
                                    MainFragmentDirections.actionMainFragmentToReturnOrderFragment(
                                        basketString
                                    )
                                )
                            }
                            ResourceState.ERROR -> {
                                setLoading(false)
                                showError(it.message)
                            }
                        }
                    }
                    savedStateHandle.remove(QrScannerFragment.RESULT_TEXT)
                } else if (result.startsWith(QrScannerFragment.PRODUCT)) {
                    savedStateHandle.remove<String>(QrScannerFragment.RESULT_TEXT)
                    savedStateHandle[QrScannerFragment.RESULT_TEXT] = result
                    navController.navigate(MainFragmentDirections.actionMainFragmentToNewSaleFragment())
                } else {
                    showError(getString(R.string.unknown_qr_code))
                }
            }
    }

//    private fun optionsMenu(view: View) {
//        val optionsMenu = PopupMenu(requireContext(), view)
//        val menuInflater = optionsMenu.menuInflater
//        menuInflater.inflate(R.menu.menu_other, optionsMenu.menu)
//        optionsMenu.setOnMenuItemClickListener {
//            when (it.itemId) {
//                R.id.menu_item_lang -> {
//
//                }
//                R.id.menu_item_url -> {
//                    val urlDialog = UrlDialog()
//                    urlDialog.show(requireActivity().supportFragmentManager, urlDialog.tag)
//                }
//            }
//            return@setOnMenuItemClickListener true
//        }
//        optionsMenu.show()
//    }
}
