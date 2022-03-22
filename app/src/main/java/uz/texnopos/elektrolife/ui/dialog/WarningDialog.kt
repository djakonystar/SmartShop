package uz.texnopos.elektrolife.ui.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.databinding.DialogWarningBinding

class WarningDialog(private val message: String) : DialogFragment(R.layout.dialog_warning) {
    private lateinit var binding: DialogWarningBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.shape_dialog)
        return inflater.inflate(R.layout.dialog_warning, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DialogWarningBinding.bind(view)

        binding.apply {
            tvMessage.text = message

            btnYes.onClick {
                onPositiveButtonClick.invoke()
                dismiss()
            }

            btnCancel.onClick {
                dismiss()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss.invoke()
    }

    private var onPositiveButtonClick: () -> Unit = {}
    fun setOnPositiveButtonClickListener(onPositiveButtonClick: () -> Unit) {
        this.onPositiveButtonClick = onPositiveButtonClick
    }

    private var onDismiss: () -> Unit = {}
    fun setOnDismissListener(onDismiss: () -> Unit) {
        this.onDismiss = onDismiss
    }
}