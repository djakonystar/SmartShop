package uz.texnopos.electrolightwarehouse.ui.newsale.dialog

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.MaskWatcherPrice
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.core.extensions.showMessage
import uz.texnopos.electrolightwarehouse.core.extensions.visibility
import uz.texnopos.electrolightwarehouse.databinding.DialogAddPaymentBinding
import java.util.*

class AddPaymentDialog(private val totalPrice: Long):DialogFragment() {
    private var _binding:DialogAddPaymentBinding? = null
    private val binding get() = _binding!!
    private var type= MutableLiveData<String>()
    private var data=""
    private lateinit var setListener: DatePickerDialog.OnDateSetListener
    private val plusDay = 86399999

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
            _binding = DialogAddPaymentBinding.inflate(layoutInflater,container,false)
            return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        val list = arrayListOf("Naqd pul","Karta","Qarz","Aralash")
        binding.apply {
            etDebt.addTextChangedListener(MaskWatcherPrice(etDebt))
            etCash.addTextChangedListener(MaskWatcherPrice(etCash))
            etCard.addTextChangedListener(MaskWatcherPrice(etCard))
        }


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
                check()
            }

            btnCalender.onClick {
                val calendar = Calendar.getInstance()
                val year = calendar.get((Calendar.YEAR))
                var month = calendar.get((Calendar.MONTH))
                val day = calendar.get((Calendar.DAY_OF_MONTH))

                setListener = DatePickerDialog.OnDateSetListener { _, year1, month1, dayOfMonth1 ->
                    month = month1+1
                    data = "$dayOfMonth1.$month.$year1"
                    etData.text = data
                }
                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    setListener
                    ,year, month, day)

                datePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                datePickerDialog.show()
            }

            btnCancel.onClick {
                dismiss()
            }
        }
    }

    private fun String.getOnlyDigits(): String {
        var s = ""
        this.forEach { if (it.isDigit()) s += it }
        return if (s.isEmpty()) "0" else s
    }

    private fun observe(){
        type.observe(requireActivity(), {
            binding.apply {
                when(it){
                    "Karta"->{
                        etCard.visibility(true)
                        etCash.visibility(false)
                        etDebt.visibility(false)
                        etData.visibility(false)
                        btnCalender.visibility(false)
                    }
                    "Naqd pul"->{
                        etDebt.visibility(false)
                        etCard.visibility(false)
                        etData.visibility(false)
                        etCash.visibility(true)
                        btnCalender.visibility(false)
                    }
                    "Aralash"->{
                        etDebt.visibility(false)
                        etCard.visibility(true)
                        etCash.visibility(true)
                        btnCalender.visibility(false)
                    }
                    else->{
                        etDebt.visibility(true)
                        etCard.visibility(false)
                        etData.visibility(true)
                        etCash.visibility(false)
                        btnCalender.visibility(true)
                    }
                }
            }
        })
    }

    private fun check(){
        type.observe(requireActivity(), {
            binding.apply {
                when(it){
                    "Karta"->{
                        etCard.visibility(true)
                        etCash.visibility(false)
                        etDebt.visibility(false)
                        etData.visibility(false)
                        if (etCard.text.isNotEmpty()){
                            val card = etCard.text.toString().getOnlyDigits().toLong()
                            val debt = totalPrice - card
                            val description = etDescription.text.toString()
                            sendDate.invoke(card,0,debt,description,data)
                            dismiss()
                        }else{
                            showMessage(context?.getString(R.string.input_sum))
                        }
                    }
                    "Naqd pul"->{
                        etDebt.visibility(false)
                        etCard.visibility(false)
                        etCash.visibility(true)
                        etData.visibility(false)
                        if (etCash.text.isNotEmpty()){
                            val cash = etCash.text.toString().getOnlyDigits().toLong()
                            val debt = totalPrice - cash
                            val description = etDescription.text.toString()
                            sendDate.invoke(0,cash,debt,description,data)
                            dismiss()
                        }else{
                            showMessage(context?.getString(R.string.input_sum))
                        }
                    }
                    "Aralash"->{
                        etDebt.visibility(false)
                        etCard.visibility(true)
                        etData.visibility(false)
                        etCash.visibility(true)
                        if (etCash.text.isNotEmpty() && etCard.text.isNotEmpty()){
                            val cash = etCash.text.toString().getOnlyDigits().toLong()
                            val card = etCard.text.toString().getOnlyDigits().toLong()
                            val debt = totalPrice - cash - card
                            val description = etDescription.text.toString()
                            sendDate.invoke(card,cash,debt,description,data)
                            dismiss()
                        }else{
                            showMessage(context?.getString(R.string.input_sum))
                        }
                    }
                    else->{
                        etDebt.visibility(true)
                        etCard.visibility(false)
                        etCash.visibility(false)
                        etData.visibility(true)
                        btnCalender.visibility(true)
                        if (etDebt.text.isNotEmpty() && data.isNotEmpty()){
                            val debt = etDebt.text.toString().getOnlyDigits().toLong()
                            val description = etDescription.text.toString()
                            sendDate.invoke(0,0,debt,description,data)
                            dismiss()
                        }else{
                            showMessage(context?.getString(R.string.input_sum_and_date))
                        }
                    }
                }
            }
        })
    }

    private var sendDate: (card:Long, cash:Long, debt:Long, description:String,data:String) -> Unit = { _:Long,_:Long,_:Long,_:String, _: String -> }
    fun setDate(sendDate: (card:Long, cash:Long, debt:Long, description:String,data:String) -> Unit) {
        this.sendDate = sendDate
    }
}