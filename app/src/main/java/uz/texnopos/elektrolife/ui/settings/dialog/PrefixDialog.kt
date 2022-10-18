package uz.texnopos.elektrolife.ui.settings.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.showError
import uz.texnopos.elektrolife.core.extensions.showSuccess
import uz.texnopos.elektrolife.databinding.DialogPrefixBinding
import uz.texnopos.elektrolife.settings.Settings

class PrefixDialog : DialogFragment(R.layout.dialog_prefix) {
    private lateinit var binding: DialogPrefixBinding
    private val settings: Settings by inject()
    private val viewModel: PrefixViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.shape_dialog)
        return inflater.inflate(R.layout.dialog_prefix, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DialogPrefixBinding.bind(view)

        binding.apply {
            etPrefix.setText(settings.prefix)

            etPrefix.addTextChangedListener {
                tilPrefix.isErrorEnabled = false
            }

            btnSave.onClick {
                val prefix = etPrefix.text.toString()

                if (prefix.isNotEmpty() && prefix.isNotBlank()) {
                    viewModel.checkPrefix(prefix)
                } else {
                    tilPrefix.error = getString(R.string.required_field)
                }
            }

            btnCancel.onClick { dismiss() }
        }

        setUpObservers()
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            btnSave.isEnabled = !loading
        }
    }

    private fun setUpObservers() {
        viewModel.prefix.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    it.data?.let { prefixResponse ->
                        if (prefixResponse.ok) {
                            val prefix = binding.etPrefix.text.toString()
                            settings.baseUrl = "https://$prefix.texnopos.uz"
                            settings.prefix = prefix
                            settings.companyConfigured = false
                            showSuccess(getString(R.string.prefix_changed_successfully))
                                .setOnDismissListener {
                                    val intent = requireActivity().intent
                                    requireActivity().finish()
                                    requireActivity().startActivity(intent)
                                }
                        }
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }
    }
}
