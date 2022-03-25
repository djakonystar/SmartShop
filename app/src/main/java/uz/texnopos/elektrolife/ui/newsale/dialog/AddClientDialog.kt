package uz.texnopos.elektrolife.ui.newsale.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import uz.texnopos.elektrolife.core.extensions.onClick
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.MaskWatcher
import uz.texnopos.elektrolife.databinding.DialogAddClientBinding

class AddClientDialog : DialogFragment() {
    private lateinit var binding: DialogAddClientBinding
    private var type = MutableLiveData<String>()
    private var userType: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.shape_dialog)
        return inflater.inflate(R.layout.dialog_add_client, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DialogAddClientBinding.bind(view)

        observe()
        val list = arrayListOf(
            context?.getString(R.string.legal_person),
            context?.getString(R.string.natural_person)
        )

        binding.apply {
            actSpinner.setAdapter(ArrayAdapter(requireContext(), R.layout.item_spinner, list))
            actSpinner.setOnFocusChangeListener { _, b ->
                if (b) {
                    actSpinner.showDropDown()
                }
                tilSpinner.isErrorEnabled = false
            }
            actSpinner.setOnItemClickListener { _, _, i, _ ->
                type.postValue(list[i])
                userType = list[i]
            }

            etPhone.addTextChangedListener(MaskWatcher.phoneNumberBySpaces())
            etInn.addTextChangedListener(MaskWatcher.taxIdentificationNumber())

            etName.addTextChangedListener {
                tilName.isErrorEnabled = false
            }
            etPhone.addTextChangedListener {
                tilPhone.isErrorEnabled = false
            }
            etInn.addTextChangedListener {
                tilInn.isErrorEnabled = false
            }

            btnAdd.onClick {
                val name = etName.text.toString()
                val phone = etPhone.text.toString().filter { c -> c.isDigit() }
                val inn = etInn.text.toString().filter { c -> c.isDigit() }
                val typeOf = when (userType) {
                    context?.getString(R.string.legal_person) -> 1
                    context?.getString(R.string.natural_person) -> 0
                    else -> -1
                }
                val comment = etComment.text.toString()

                if (typeOf != -1 && name.isNotEmpty() && phone.isNotEmpty() && phone.length == 9) {
                    if (typeOf == 1) {
                        if (inn.isNotEmpty() && inn.length == 9) {
                            sendData.invoke(name, inn, phone, typeOf, comment)
                            dismiss()
                        } else {
                            tilInn.error =
                                if (inn.isNotEmpty()) context?.getString(R.string.required_field)
                                else context?.getString(R.string.input_correct_info)
                        }
                    } else {
                        sendData.invoke(name, inn, phone, typeOf, comment)
                        dismiss()
                    }
                } else {
                    if (typeOf == -1) tilSpinner.error = context?.getString(R.string.required_field)
                    if (name.isEmpty()) tilName.error = context?.getString(R.string.required_field)
                    if (phone.isEmpty()) {
                        tilPhone.error = context?.getString(R.string.required_field)
                    } else tilPhone.error = context?.getString(R.string.input_correct_info)
                }
            }

            btnCancel.onClick {
                dismiss()
            }
        }
    }

    private fun observe() {
        type.observe(requireActivity()) {
            if (it == context?.getString(R.string.legal_person)) {
                binding.apply {
                    tilName.isVisible = true
                    tilPhone.isVisible = true
                    tilInn.isVisible = true
                    tilComment.isVisible = true
                }
            } else {
                binding.apply {
                    tilName.isVisible = true
                    tilPhone.isVisible = true
                    tilInn.isVisible = false
                    tilComment.isVisible = true
                }
            }
        }
    }


    private var sendData: (name: String, inn: String, phone: String, type: Int, comment: String) -> Unit =
        { _: String, _: String, _: String, _: Int, _: String -> }

    fun setData(sendData: (name: String, inn: String, phone: String, type: Int, comment: String) -> Unit) {
        this.sendData = sendData
    }
}