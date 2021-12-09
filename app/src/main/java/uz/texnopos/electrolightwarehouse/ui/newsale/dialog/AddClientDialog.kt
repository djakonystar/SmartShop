package uz.texnopos.electrolightwarehouse.ui.newsale.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.core.extensions.visibility
import uz.texnopos.electrolightwarehouse.databinding.DialogAddClientBinding

class AddClientDialog:DialogFragment() {
    private var _binding:DialogAddClientBinding? = null
    private val binding get() = _binding!!
    private var type= MutableLiveData<String>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
            _binding = DialogAddClientBinding.inflate(layoutInflater,container,false)
            return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        val list = arrayListOf("Jismoniy","Yuridik")
        binding.spinner2.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, list)
        binding.spinner2.setPopupBackgroundResource(R.drawable.background_textview)
        binding.spinner2.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    p2: Int,
                    p3: Long
                ) {
                    type.postValue(list[p2])
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }

        binding.apply {
            binding.btnAdd.onClick {
                if (etName.text.isNotEmpty() && etPhone.text.isNotEmpty()){
                    val name = etName.text.toString()
                    val inn = etInn.text.toString()
                    val phone = etPhone.text.toString()
                    val typeOf: Int = if (type.value.toString() == "Jismoniy"){
                        0
                    }else{
                        1
                    }
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
                    etInn.visibility(true)
                }
            }else{
                binding.apply {
                    etInn.visibility(false)
                }
            }
        })
    }


    private var sendDate: (name:String, inn:String, phone:String, type:Int) -> Unit = { _:String,_:String,_:String, _: Int -> }
    fun setDate(sendDate: (name:String, inn:String, phone:String, type:Int) -> Unit) {
        this.sendDate = sendDate
    }
}