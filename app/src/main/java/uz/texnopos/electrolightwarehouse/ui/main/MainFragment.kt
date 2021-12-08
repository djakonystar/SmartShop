package uz.texnopos.electrolightwarehouse.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.databinding.FragmentMainBinding

class MainFragment : Fragment(R.layout.fragment_main) {
    private lateinit var binding: FragmentMainBinding
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMainBinding.bind(view)
        navController = findNavController()

        binding.apply {
            clients.onClick {
                navController.navigate(R.id.action_mainFragment_to_clientsFragment)
            }
            warehouse.onClick {
                navController.navigate(R.id.action_mainFragment_to_warehouseFragment)
            }
        }
        binding.sales.onClick {
                navController.navigate(MainFragmentDirections.actionMainFragmentToSalesFragment())
        }
    }
}