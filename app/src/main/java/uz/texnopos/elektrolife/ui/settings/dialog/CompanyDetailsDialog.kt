package uz.texnopos.elektrolife.ui.settings.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import org.koin.android.ext.android.inject
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.MaskWatcher
import uz.texnopos.elektrolife.core.extensions.isEmptyOrBlank
import uz.texnopos.elektrolife.core.extensions.isNotEmptyAndBlank
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.toPhoneNumber
import uz.texnopos.elektrolife.databinding.DialogCompanyDetailsBinding
import uz.texnopos.elektrolife.settings.Settings

class CompanyDetailsDialog : DialogFragment(R.layout.dialog_company_details) {
    private lateinit var binding: DialogCompanyDetailsBinding
    private val settings: Settings by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.shape_dialog)
        return inflater.inflate(R.layout.dialog_company_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DialogCompanyDetailsBinding.bind(view)

        binding.apply {
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

            etCompanyName.setText(settings.companyName)
            etAddress.setText(settings.companyAddress)
            etPhone.setText(settings.companyPhone.toPhoneNumber)

            btnSave.onClick {
                val name = etCompanyName.text.toString()
                val address = etAddress.text.toString()
                val phone = etPhone.text.toString().filter { c -> c.isDigit() }

                if (name.isNotEmptyAndBlank() && address.isNotEmptyAndBlank() && phone.isNotEmpty()) {
                    settings.companyName = name
                    settings.companyAddress = address
                    settings.companyPhone = phone
                    onSuccess()
                    dismiss()
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

            btnCancel.onClick { dismiss() }
        }
    }

    private var onSuccess: () -> Unit = {}
    fun setOnSuccessListener(onSuccess: () -> Unit) {
        this.onSuccess = onSuccess
    }
}
