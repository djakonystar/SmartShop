package uz.texnopos.elektrolife.ui.noconnection

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import uz.texnopos.elektrolife.MainActivity
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.databinding.FragmentNoInternetBinding

class NoInternetFragment: Fragment(R.layout.fragment_no_internet) {
    private lateinit var binding: FragmentNoInternetBinding
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNoInternetBinding.bind(view)
        navController = findNavController()

        val activity = requireActivity() as MainActivity

        activity.setOnConnectionAvailableListener {
            navController.popBackStack()
        }

        binding.btnExit.onClick {
            requireActivity().finish()
        }
    }
}
