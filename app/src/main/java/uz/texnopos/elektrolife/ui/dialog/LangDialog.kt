package uz.texnopos.elektrolife.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import org.koin.android.ext.android.inject
import uz.texnopos.elektrolife.MainActivity
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.databinding.DialogLangBinding
import uz.texnopos.elektrolife.settings.Settings

class LangDialog : DialogFragment() {
    private lateinit var binding: DialogLangBinding
    private val settings: Settings by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.shape_dialog)
        return inflater.inflate(R.layout.dialog_lang, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DialogLangBinding.bind(view)

        binding.apply {
            val currentLang = settings.language

            ivCheckEng.isVisible = currentLang == "en"
            ivCheckRu.isVisible = currentLang == "ru"
            ivCheckUz.isVisible = currentLang == "uz"
            ivCheckKk.isVisible = currentLang == "kaa"

            tvRu.onClick {
                settings.language = "ru"
                dismiss()
                (requireActivity() as MainActivity).rerun()
            }
            tvUz.onClick {
                settings.language = "uz"
                dismiss()
                (requireActivity() as MainActivity).rerun()
            }
            tvKk.onClick {
                settings.language = "kaa"
                dismiss()
                (requireActivity() as MainActivity).rerun()
            }
            tvEng.onClick {
                settings.language = "en"
                dismiss()
                (requireActivity() as MainActivity).rerun()
            }
        }
    }
}
