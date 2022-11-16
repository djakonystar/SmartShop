package uz.texnopos.elektrolife.ui.start

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.showError
import uz.texnopos.elektrolife.databinding.FragmentStartUrlBinding
import uz.texnopos.elektrolife.settings.Settings
import uz.texnopos.elektrolife.ui.settings.dialog.PrefixViewModel

class StartUrlFragment : Fragment(R.layout.fragment_start_url) {
    private lateinit var binding: FragmentStartUrlBinding
    private lateinit var navController: NavController
    private val prefixViewModel: PrefixViewModel by viewModel()
    private val settings: Settings by inject()
    private var prefix: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentStartUrlBinding.bind(view)
        navController = findNavController()

        if (settings.shopSelected) {
            navController.navigate(R.id.action_startShopFragment_to_signInFragment)
        }

        binding.apply {
            etUrl.addTextChangedListener {
                prefix = it.toString()
                tilUrl.isErrorEnabled = false
            }

            btnContinue.onClick {
                if (prefix.isNotEmpty() && prefix.isNotBlank()) {
                    prefixViewModel.checkPrefix(prefix)
                } else {
                    tilUrl.error = getString(R.string.required_field)
                }
            }

            btnLoginTest.onClick {
                settings.baseUrl = getString(R.string.test_base_url)
                settings.shopSelected = true
                navController.navigate(R.id.action_startShopFragment_to_signInFragment)
            }
        }

        setUpObservers()
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            tilUrl.isEnabled = !loading
            etUrl.isEnabled = !loading
            btnContinue.isEnabled = !loading
            btnLoginTest.isEnabled = !loading
        }
    }

    private fun setUpObservers() {
        prefixViewModel.prefix.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    settings.prefix = prefix
                    settings.baseUrl = "https://$prefix.texnopos.uz"
                    settings.shopSelected = true
                    navController.navigate(R.id.action_startShopFragment_to_signInFragment)
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }
    }
}
