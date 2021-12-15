package uz.texnopos.electrolightwarehouse.ui.newsale.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.databinding.DialogAddClientBinding

class AddClientDialog:DialogFragment() {
    private lateinit var binding: DialogAddClientBinding
    private var type= MutableLiveData<String>()

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
        val list = arrayListOf("Jismoniy", "Yuridik")

        binding.apply {
            actSpinner.setAdapter(ArrayAdapter(requireContext(), R.layout.item_spinner, list))
            actSpinner.setOnFocusChangeListener { _, b ->
                if (b) {
                    actSpinner.showDropDown()
                }
                tilSpinner.isErrorEnabled = false
            }
            actSpinner.setOnItemClickListener { adapterView, view, i, l ->
                type.postValue(list[i])
            }

            binding.btnAdd.onClick {
                if (etName.text.toString().isNotEmpty() && etPhone.text.toString().isNotEmpty()){
                    val name = etName.text.toString()
                    val inn = etInn.text.toString()
                    val phone = etPhone.text.toString()
                    val typeOf: Int = if (type.value.toString() == "Jismoniy") 0 else 1
                    sendDate.invoke(name, inn, phone, typeOf)
                    dismiss()
                }else{
                    Toast.makeText(context, requireActivity().getString(R.string.fill_the_fields), Toast.LENGTH_SHORT).show()
                }
            }

            btnCancel.onClick {
                dismiss()
            }
        }
    }

    private fun observe(){
        type.observe(requireActivity(), {
            if (it == "Yuridik"){
                binding.apply {
                    tilInn.isVisible = true
                }
            }else{
                binding.apply {
                    tilInn.isVisible = false
                }
            }
        })
    }


    private var sendDate: (name:String, inn:String, phone:String, type:Int) -> Unit = { _:String,_:String,_:String, _: Int -> }
    fun setDate(sendDate: (name:String, inn:String, phone:String, type:Int) -> Unit) {
        this.sendDate = sendDate
    }
}