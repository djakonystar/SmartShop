package uz.texnopos.elektrolife.ui.start

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.inject
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.MaskWatcher
import uz.texnopos.elektrolife.core.extensions.isEmptyOrBlank
import uz.texnopos.elektrolife.core.extensions.isNotEmptyAndBlank
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.databinding.FragmentCompanyDetailsBinding
import uz.texnopos.elektrolife.settings.Settings

class CompanyDetailsFragment: Fragment(R.layout.fragment_company_details) {
    private lateinit var binding: FragmentCompanyDetailsBinding
    private lateinit var navController: NavController
    private val settings: Settings by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCompanyDetailsBinding.bind(view)
        navController = findNavController()

        binding.apply {
            if (settings.companyConfigured) {
                navController.navigate(R.id.action_companyDetailsFragment_to_signInFragment)
            }

            etPhone.addTextChangedListener(MaskWatcher.phoneNumberBySpaces())

            etCompanyName.addTextChangedListener {
                tilCompanyName.isErrorEnabled = false
            }

            etAddress.addTextChangedListener {
                tilAddress.isErrorEnabled = false
            }

            etPhone.addTextChangedListener {
                tilPhone.isErrorEnabled = false
            }

            btnSave.onClick {
                val name = etCompanyName.text.toString()
                val address = etAddress.text.toString()
                val phone = etPhone.text.toString().filter { c -> c.isDigit() }

                if (name.isNotEmptyAndBlank() && address.isNotEmptyAndBlank() && phone.isNotEmpty()) {
                    settings.companyName = name
                    settings.companyAddress = address
                    settings.companyPhone = phone
                    settings.companyConfigured = true
                    navController.navigate(R.id.action_companyDetailsFragment_to_signInFragment)
                } else {
                    if (name.isEmptyOrBlank()) {
                        tilCompanyName.error = getString(R.string.required_field)
                    }
                    if (address.isEmptyOrBlank()) {
                        tilAddress.error = getString(R.string.required_field)
                    }
                    if (phone.isEmpty()) {
                        tilPhone.error = getString(R.string.required_field)
                    }
                }
            }
        }
    }
}
