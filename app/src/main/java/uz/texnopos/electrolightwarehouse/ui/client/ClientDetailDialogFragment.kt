package uz.texnopos.electrolightwarehouse.ui.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.core.extensions.toPhoneNumber
import uz.texnopos.electrolightwarehouse.core.extensions.toSumFormat
import uz.texnopos.electrolightwarehouse.data.model.ClientResponse
import uz.texnopos.electrolightwarehouse.databinding.DialogClientDetailBinding

class ClientDetailDialogFragment(private val client: ClientResponse) : DialogFragment() {
    private lateinit var binding: DialogClientDetailBinding

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
                "-${(-1 * client.balance).toString().toSumFormat} UZS"
            } else {
                "${client.balance.toString().toSumFormat} UZS"
            }
            tvPhone.text = client.phone.toPhoneNumber
            tvComment.text = client.comment
            if (client.type == 1) {
                tvTIN.text = client.tin.toSumFormat
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
