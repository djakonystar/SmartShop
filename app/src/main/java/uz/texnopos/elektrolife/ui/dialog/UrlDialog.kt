package uz.texnopos.elektrolife.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import org.koin.android.ext.android.inject
import uz.texnopos.elektrolife.MainActivity
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.extensions.Constants
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.scope
import uz.texnopos.elektrolife.databinding.DialogUrlBinding
import uz.texnopos.elektrolife.settings.Settings

class UrlDialog: DialogFragment() {
    private lateinit var binding: DialogUrlBinding
    private lateinit var baseUrls: List<String>
    private val settings: Settings by inject()
    private lateinit var selectedShop: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.shape_dialog)
        return inflater.inflate(R.layout.dialog_url, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DialogUrlBinding.bind(view)
        baseUrls = Constants.provideBaseUrls().values.toList()
        selectedShop = settings.baseUrl

        binding.apply {
            etUrl.setText(selectedShop.substringAfter("https://"))

            etUrl.addTextChangedListener {
                "https://${it.toString()}".scope { url ->
                    btnContinue.isEnabled = baseUrls.contains(url)
                    selectedShop = url
                }
            }

            btnContinue.onClick {
                settings.baseUrl = "https://${etUrl.text.toString()}"
                settings.shopSelected = true
                dismiss()
                (requireActivity() as MainActivity).rerun()
            }
        }
    }
}
