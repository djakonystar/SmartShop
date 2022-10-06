package uz.texnopos.elektrolife.ui.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.databinding.ActionBarBinding
import uz.texnopos.elektrolife.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSettingsBinding.bind(view)
        abBinding = ActionBarBinding.bind(view)
        navController = findNavController()

        abBinding.apply {
            btnHome.onClick {
                navController.popBackStack()
            }

            tvTitle.text = getString(R.string.settings)
        }
    }
}
