package uz.texnopos.electrolightwarehouse.ui.main

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.inject
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.core.extensions.showMessage
import uz.texnopos.electrolightwarehouse.databinding.FragmentMainBinding
import uz.texnopos.electrolightwarehouse.settings.Settings
import uz.texnopos.electrolightwarehouse.ui.newsale.Basket

class MainFragment : Fragment(R.layout.fragment_main) {
    private lateinit var binding: FragmentMainBinding
    private lateinit var navController: NavController
    private val setting: Settings by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Basket.mutableProducts.clear()
        binding = FragmentMainBinding.bind(view)
        navController = findNavController()

        binding.apply {
            sales.onClick {
                navController.navigate(R.id.action_mainFragment_to_salesFragment)
            }
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
                optionsMenu(it)
            }
            newSale.onClick {
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToNewSaleFragment())
            }
            newProduct.onClick {
                navController.navigate(R.id.action_mainFragment_to_newProductFragment)
            }

            when (setting.role) {
                "seller" -> {
                    newProduct.isEnabled = false
                    iconNewProduct.setBackgroundColor(resources.getColor(R.color.is_enabled_color))
                    titleNewProduct.setBackgroundColor(resources.getColor(R.color.is_enabled_color))
                }
                "admin" -> {
                    newProduct.isEnabled = true
                }
                else -> showMessage("Administratsyaga murojat qiling")
            }
        }
    }

    private fun optionsMenu(view: View) {
        val optionsMenu = PopupMenu(requireContext(), view)
        val menuInflater = optionsMenu.menuInflater
        menuInflater.inflate(R.menu.menu_other, optionsMenu.menu)
        optionsMenu.setOnMenuItemClickListener {
            when(setting.role)
            {
                "seller"->{
                    when(it.itemId){
                        R.id.menuNewPinCode -> {
                            // todo go to setting
                        }
                        R.id.menuNewCategory -> {
                            showMessage("Administratsyaga murojat qiling")
                        }
                    }
                }
                "admin"->{
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
