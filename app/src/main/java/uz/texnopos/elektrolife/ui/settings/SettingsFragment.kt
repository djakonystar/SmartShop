package uz.texnopos.elektrolife.ui.settings

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import devit951.github.magictip.tip.AutoCloseMagicTip
import org.koin.android.ext.android.inject
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.extensions.Constants.ROLE_CEO
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.showSuccess
import uz.texnopos.elektrolife.databinding.ActionBarBinding
import uz.texnopos.elektrolife.databinding.FragmentSettingsBinding
import uz.texnopos.elektrolife.settings.Settings
import uz.texnopos.elektrolife.ui.dialog.LangDialog
import uz.texnopos.elektrolife.ui.dialog.WarningDialog
import uz.texnopos.elektrolife.ui.settings.dialog.CompanyDetailsDialog
import uz.texnopos.elektrolife.ui.settings.dialog.PrefixDialog

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var navController: NavController
    private val settings: Settings by inject()

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

        binding.apply {
            cardDetails.isVisible = settings.role == ROLE_CEO
            cardPrefix.isVisible = settings.role == ROLE_CEO

            ivWarningLanguage.onClick {
                showWarning(it)
            }

            ivWarningPrefix.onClick {
                showWarning(it)
            }

            cardLanguage.onClick {
                val langDialog = LangDialog()
                langDialog.show(requireActivity().supportFragmentManager, langDialog.tag)
            }

            cardDetails.onClick {
                navController.navigate(R.id.action_settingsFragment_to_companyDetailsFragment)
//                val detailsDialog = CompanyDetailsDialog()
//                detailsDialog.setOnSuccessListener {
//                    showSuccess(getString(R.string.details_success_msg))
//                }
//                detailsDialog.show(requireActivity().supportFragmentManager, detailsDialog.tag)
            }

            cardPrefix.onClick {
                val prefixDialog = PrefixDialog()
                prefixDialog.show(requireActivity().supportFragmentManager, prefixDialog.tag)
            }

            cardLogout.onClick {
                val warningDialog = WarningDialog(getString(R.string.warning_logout))
                warningDialog.setOnPositiveButtonClickListener {
                    navController.navigate(R.id.action_settingsFragment_to_signInFragment)
                }
                warningDialog.show(requireActivity().supportFragmentManager, warningDialog.tag)
            }
        }
    }

    private fun showWarning(anchorView: View) {
        AutoCloseMagicTip(anchorView, 2000L)
            .settings {
                text = getString(R.string.required_restart)
                bgColor = ContextCompat.getColor(requireContext(), R.color.error_color)
            }
            .show()
    }
}
