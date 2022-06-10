package uz.texnopos.elektrolife.ui.start

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.inject
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.extensions.Constants
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.scope
import uz.texnopos.elektrolife.databinding.FragmentStartUrlBinding
import uz.texnopos.elektrolife.settings.Settings

class StartUrlFragment : Fragment(R.layout.fragment_start_url) {
    private lateinit var binding: FragmentStartUrlBinding
    private lateinit var navController: NavController
    private lateinit var baseUrls: List<String>
    private val settings: Settings by inject()
    private lateinit var selectedShop: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentStartUrlBinding.bind(view)
        navController = findNavController()
        baseUrls = Constants.provideBaseUrls().keys.toList()

        if (settings.shopSelected) {
            navController.navigate(R.id.action_startShopFragment_to_signInFragment)
        }

        binding.apply {
            etUrl.addTextChangedListener {
                "https://${it.toString()}".scope { url ->
                    btnContinue.isEnabled = baseUrls.contains(url)
                    selectedShop = url
                }
            }

            btnContinue.onClick {
                settings.baseUrl = selectedShop
                settings.shopSelected = true
                navController.navigate(R.id.action_startShopFragment_to_signInFragment)
            }
        }
    }
}