package uz.texnopos.elektrolife.ui.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.databinding.DialogSuccessOrderBinding

class SuccessOrderDialog(private val message: String) : DialogFragment(R.layout.dialog_success) {
    private lateinit var binding: DialogSuccessOrderBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.shape_dialog)
        return inflater.inflate(R.layout.dialog_success_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DialogSuccessOrderBinding.bind(view)

        binding.apply {
            tvMessage.text = message

            btnPrintReceipt.onClick {
                onPrintButtonClick.invoke()
                dismiss()
            }

            btnClose.onClick {
                onPositiveButtonClick.invoke()
                dismiss()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss.invoke()
    }

    private var onPrintButtonClick: () -> Unit = {}
    fun setOnPrintButtonClickListener(onPrintButtonClick: () -> Unit) {
        this.onPrintButtonClick = onPrintButtonClick
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
