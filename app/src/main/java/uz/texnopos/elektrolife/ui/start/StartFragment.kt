package uz.texnopos.elektrolife.ui.start

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.inject
import uz.texnopos.elektrolife.MainActivity
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.databinding.FragmentStartBinding
import uz.texnopos.elektrolife.settings.Settings

class StartFragment : Fragment(R.layout.fragment_start) {
    private lateinit var binding: FragmentStartBinding
    private lateinit var navController: NavController
    private val settings: Settings by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentStartBinding.bind(view)
        navController = findNavController()

        if (!settings.firstRun) {
            navController.navigate(R.id.action_startFragment_to_signInFragment)
        }

        binding.apply {
            tvRu.onClick {
                settings.language = "ru"
                (requireActivity() as MainActivity).setNewLocale()
                settings.firstRun = false
            }
            tvUz.onClick {
                settings.language = "uz"
                (requireActivity() as MainActivity).setNewLocale()
                settings.firstRun = false
            }
            tvKk.onClick {
                settings.language = "kaa"
                (requireActivity() as MainActivity).setNewLocale()
                settings.firstRun = false
            }
            tvEng.onClick{
                settings.language = "en"
                (requireActivity() as MainActivity).setNewLocale()
                settings.firstRun = false
            }
        }
    }
}