package uz.texnopos.elektrolife.ui.start

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.inject
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.databinding.FragmentStartUrlBinding
import uz.texnopos.elektrolife.settings.Settings

class StartUrlFragment : Fragment(R.layout.fragment_start_url) {
    private lateinit var binding: FragmentStartUrlBinding
    private lateinit var navController: NavController
    private val settings: Settings by inject()
    private var prefix: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentStartUrlBinding.bind(view)
        navController = findNavController()

        if (settings.shopSelected) {
            navController.navigate(R.id.action_startShopFragment_to_companyDetailsFragment)
        }

        binding.apply {
            etUrl.addTextChangedListener {
                prefix = it.toString()
                tilUrl.isErrorEnabled = false
            }

            btnContinue.onClick {
                if (prefix.isNotEmpty() && prefix.isNotBlank()) {
                    settings.prefix = prefix
                    settings.baseUrl = "https://$prefix.texnopos.uz"
                    settings.shopSelected = true
                    navController.navigate(R.id.action_startShopFragment_to_companyDetailsFragment)
                } else {
                    tilUrl.error = getString(R.string.required_field)
                }
            }

            btnLoginTest.onClick {
                settings.baseUrl = getString(R.string.test_base_url)
                settings.shopSelected = true
                settings.companyName = getString(R.string.test_company_name)
                settings.companyPhone = getString(R.string.test_company_phone)
                settings.companyConfigured = true
                navController.navigate(R.id.action_startShopFragment_to_companyDetailsFragment)
            }
        }
    }
}