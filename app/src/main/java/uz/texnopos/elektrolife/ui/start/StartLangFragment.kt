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
import uz.texnopos.elektrolife.databinding.FragmentStartLangBinding
import uz.texnopos.elektrolife.settings.Settings

class StartLangFragment : Fragment(R.layout.fragment_start_lang) {
    private lateinit var binding: FragmentStartLangBinding
    private lateinit var navController: NavController
    private val settings: Settings by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentStartLangBinding.bind(view)
        navController = findNavController()

        if (settings.langSelected) {
            navController.navigate(R.id.action_startLangFragment_to_startShopFragment)
        }

        binding.apply {
            tvRu.onClick {
                settings.language = "ru"
                (requireActivity() as MainActivity).rerun()
                settings.langSelected = true
            }
            tvUz.onClick {
                settings.language = "uz"
                (requireActivity() as MainActivity).rerun()
                settings.langSelected = true
            }
            tvKk.onClick {
                settings.language = "kaa"
                (requireActivity() as MainActivity).rerun()
                settings.langSelected = true
            }
            tvEng.onClick{
                settings.language = "en"
                (requireActivity() as MainActivity).rerun()
                settings.langSelected = true
            }
        }
    }
}
