package uz.texnopos.elektrolife.ui.main

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.showMessage
import uz.texnopos.elektrolife.databinding.FragmentMainBinding
import uz.texnopos.elektrolife.settings.Settings
import uz.texnopos.elektrolife.ui.newsale.Basket

class MainFragment : Fragment(R.layout.fragment_main) {
    private lateinit var binding: FragmentMainBinding
    private lateinit var navController: NavController
    private val viewModel: MainViewModel by viewModel()
    private val settings: Settings by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Basket.mutableProducts.clear()
        binding = FragmentMainBinding.bind(view)
        navController = findNavController()

        binding.apply {
            clients.onClick {
                navController.navigate(R.id.action_mainFragment_to_clientsFragment)
            }
            newPayment.onClick {
                navController.navigate(
                    MainFragmentDirections.actionMainFragmentToNewPayment(client = "null")
                )
            }
            warehouse.onClick {
                navController.navigate(R.id.action_mainFragment_to_warehouseFragment)
            }


            ivOther.onClick {
//                optionsMenu(it)
                navController.navigate(MainFragmentDirections.actionMainFragmentToNewCategoryFragment())
            }
            newSale.onClick {
                navController.navigate(MainFragmentDirections.actionMainFragmentToNewSaleFragment())
            }
            newProduct.onClick {
                navController.navigate(R.id.action_mainFragment_to_newProductFragment)
            }

            when (settings.role) {
                "seller" -> {
                    newProduct.isEnabled = false
                    ivOther.isVisible = false
                    iconNewProduct.setBackgroundColor(resources.getColor(R.color.is_enabled_color))
                    titleNewProduct.setBackgroundColor(resources.getColor(R.color.is_enabled_color))
                    titleFinance.text = context?.getString(R.string.sales)
                    iconFinance.setImageResource(R.drawable.sales)
                    finance.onClick {
                        navController.navigate(R.id.action_mainFragment_to_salesFragment)
                    }
                }
                "admin" -> {
                    newProduct.isEnabled = true
                    ivOther.isVisible = true
                    finance.onClick {
                        navController.navigate(R.id.action_mainFragment_to_financeFragment)
                    }
                }
                else -> showMessage("Administratsiyaga murojaat qiling")
            }
        }

        viewModel.getDollarRate()
        setUpObservers()
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
        }
    }

    private fun setUpObservers() {
        viewModel.dollarRate.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    if (it.data!!.successful) {
                        settings.dollarRate = it.data.payload.usd
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

    private fun optionsMenu(view: View) {
        val optionsMenu = PopupMenu(requireContext(), view)
        val menuInflater = optionsMenu.menuInflater
        menuInflater.inflate(R.menu.menu_other, optionsMenu.menu)
        optionsMenu.setOnMenuItemClickListener {
            when (settings.role) {
                "seller" -> {
                    when (it.itemId) {
                        R.id.menuNewPinCode -> {
                            // todo go to setting
                        }
                        R.id.menuNewCategory -> {
                            showMessage("Administratsyaga murojaat qiling")
                        }
                    }
                }
                "admin" -> {
                    R.id.menuNewCategory
                    when (it.itemId) {
                        R.id.menuNewCategory -> {
                            navController.navigate(R.id.action_mainFragment_to_newCategoryFragment)
                        }
                        R.id.menuNewPinCode -> {
                            // todo go to setting
                        }
                    }
                }


            }
            return@setOnMenuItemClickListener true
        }
        optionsMenu.show()
    }
}
