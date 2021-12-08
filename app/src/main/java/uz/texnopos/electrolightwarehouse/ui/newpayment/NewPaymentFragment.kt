package uz.texnopos.electrolightwarehouse.ui.newpayment

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.CalendarHelper
import uz.texnopos.electrolightwarehouse.core.MaskWatcherPayment
import uz.texnopos.electrolightwarehouse.core.ResourceState
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.core.extensions.visibility
import uz.texnopos.electrolightwarehouse.data.newPayment.NewPayment
import uz.texnopos.electrolightwarehouse.databinding.ActionBarBinding
import uz.texnopos.electrolightwarehouse.databinding.FragmentPaymentNewBinding
import uz.texnopos.electrolightwarehouse.ui.newpayment.dialog.CalendarDialog

class NewPaymentFragment: Fragment(R.layout.fragment_payment_new) {

    private var _binding: FragmentPaymentNewBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var abBinding: ActionBarBinding
    private val calendarHelper = CalendarHelper()
    private lateinit var selectedDate: String
    private var method: String = ""
    private var cash = ""
    private var card = ""
    private var clientId: Int = 0
    private val delay: Long = 1000
    private var lastTextEdit: Long = 0
    private var comment: String = ""
    private val handler = Handler(Looper.getMainLooper())
    private val viewModel: NewPaymentViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentNewBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        abBinding = ActionBarBinding.bind(view)
        selectedDate = calendarHelper.currentDate
        abBinding.apply {
                tvTitle.text = context?.getString(R.string.new_payment)
                btnHome.onClick {
                    navController.popBackStack()
                }
        }
        binding.apply {
            etPaymentCard.addTextChangedListener(MaskWatcherPayment(etPaymentCard))
            etPaymentCash.addTextChangedListener(MaskWatcherPayment(etPaymentCash))
            tvMethodStr.text = view.context.getString(R.string.method_uzb)
            rb1.text = view.context.getString(R.string.cash_uzb)
            rb2.text = view.context.getString(R.string.uzcard)
            rb3.text = view.context.getString(R.string.mix)
            etDate.setText(selectedDate)
            etDate.onClick {
                val dialog = CalendarDialog(requireContext())
                dialog.show()
                dialog.onDateSelectedListener { date ->
                    selectedDate = date
                    etDate.setText(date)
                }
            }
                rb1.onClick {
                    etPaymentCash.text = null
                    etPaymentCard.text = null
                    method = view.context.getString(R.string.cash_uzb)
                    tilPaymentCash.visibility(true)
                    tilPaymentCard.visibility(false)
                }
                rb2.onClick {
                    etPaymentCash.text = null
                    etPaymentCard.text = null
                    method = view.context.getString(R.string.uzcard)
                    tilPaymentCash.visibility(false)
                    tilPaymentCard.visibility(true)
                }
                rb3.onClick {
                    etPaymentCash.text = null
                    etPaymentCard.text = null
                    method = view.context.getString(R.string.mix)
                    tilPaymentCash.visibility(true)
                    tilPaymentCard.visibility(true)
            }

            val inputFinishChecker = Runnable {
                    if (System.currentTimeMillis()>(lastTextEdit+delay-500)){
                        //TODO: do what you need here
                    }
            }
            etSearchClient.addTextChangedListener(object: TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    handler.removeCallbacks(inputFinishChecker)
                }

                override fun afterTextChanged(p0: Editable?) {
                    if (p0?.length!! >0){
                        lastTextEdit = System.currentTimeMillis()
                        handler.postDelayed(inputFinishChecker,delay)
                    }
                }

            })

            btnSend.onClick {
                if (method.isEmpty()) {
                    Toast.makeText(requireContext(), "Tolash turini saylayng", Toast.LENGTH_SHORT)
                        .show()
                } else if (etPaymentCard.text!!.isEmpty() && etPaymentCash.text!!.isEmpty() &&etSearchClient.text!!.isEmpty()) {
                    Toast.makeText(requireContext(), "iltimos mijozni saylayng va tolovni toliq kiriting", Toast.LENGTH_SHORT)
                        .show()
                } else if (rb1.isChecked && etPaymentCash.text!!.isNotEmpty() && etSearchClient.text!!.isNotEmpty()){
                    cash = etPaymentCash.text.toString()
                    cash = cash.replace("\\s".toRegex(),"")
                    cash = cash.substring(0,cash.length-3)
                }  else if(rb2.isChecked && etPaymentCard.text!!.isNotEmpty() && etSearchClient.text!!.isNotEmpty()){
                    card = etPaymentCard.text.toString()
                    card = card.replace("\\s".toRegex(),"")
                    card = card.substring(0,card.length-3)
                }else if (rb3.isChecked && etPaymentCash.text!!.isNotEmpty() && etPaymentCard.text!!.isNotEmpty() && etSearchClient.text!!.isNotEmpty() ){
                    cash = etPaymentCash.text.toString()
                    cash = cash.replace("\\s".toRegex(),"")
                    cash = cash.substring(0,cash.length-3)
                    card = etPaymentCard.text.toString()
                    card = card.replace("\\s".toRegex(),"")
                    card = card.substring(0,cash.length-3)
                    comment = etComment.text.toString()
                    viewModel.newPayment("", NewPayment(clientId,cash, card,selectedDate,comment))
                }
                else
                {Toast.makeText(requireContext(), "Mijozni saylayng va tolovni toliq kiriting ", Toast.LENGTH_SHORT).show()}
            }
        }
    }
    private fun setupObserver(){
        viewModel.newPayment.observe(viewLifecycleOwner,{
            when(it.status){
                ResourceState.LOADING->{binding.progressBar.visibility(true)}
                ResourceState.SUCCESS->{binding.progressBar.visibility = View.GONE
                 if (it.data!!.successful){
                     val alertDialog = AlertDialog.Builder(requireContext())
                     alertDialog.setTitle("To'lov amalga oshirldi")
                     alertDialog.setMessage(it.data.message)
                     alertDialog.show()
                 }else{
                     Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
                 }
                }
                ResourceState.ERROR->{binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}