package uz.texnopos.elektrolife.ui.client

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import org.koin.android.ext.android.inject
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.extensions.toPhoneFormat
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.sumFormat
import uz.texnopos.elektrolife.core.extensions.toSumFormat
import uz.texnopos.elektrolife.data.model.clients.Client
import uz.texnopos.elektrolife.databinding.DialogClientDetailBinding
import uz.texnopos.elektrolife.settings.Settings

class ClientDetailDialogFragment(private val client: Client) : DialogFragment() {
    private lateinit var binding: DialogClientDetailBinding
    private val settings: Settings by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.shape_dialog)
        return inflater.inflate(R.layout.dialog_client_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DialogClientDetailBinding.bind(view)

        binding.apply {
            tvName.text = client.name
            tvBalance.text = if (client.balance < 0) {
                tvBalance.setTextColor(ContextCompat.getColor(root.context, R.color.error_color))
                "-${(-1 * client.balance).toSumFormat} ${settings.currency}"
            } else {
                tvBalance.setTextColor(ContextCompat.getColor(root.context, R.color.app_main_color))
                "${client.balance.toSumFormat} ${settings.currency}"
            }
            tvPhone.text = client.phone.toPhoneFormat
            tvComment.text = client.comment ?: ""
            Log.d("clientType", client.type.toString())
            if (client.type == "Y") {
                tvTINTitle.isVisible = true
                tvTIN.isVisible = true
                tvTIN.text = client.tin!!.sumFormat
            } else {
                tvTINTitle.isVisible = false
                tvTIN.isVisible = false
                dividerTIN.isVisible = false
            }

            btnClose.onClick {
                dismiss()
            }
        }
    }
}
