package uz.texnopos.electrolightwarehouse.ui.newpayment

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.MaskWatcherPayment
import uz.texnopos.electrolightwarehouse.core.ResourceState
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.core.extensions.showMessage
import uz.texnopos.electrolightwarehouse.core.extensions.visibility
import uz.texnopos.electrolightwarehouse.data.model.Client
import uz.texnopos.electrolightwarehouse.data.newPayment.NewPayment
import uz.texnopos.electrolightwarehouse.databinding.ActionBarBinding
import uz.texnopos.electrolightwarehouse.databinding.FragmentPaymentNewBinding

class NewPaymentFragment : Fragment(R.layout.fragment_payment_new) {
    private lateinit var binding: FragmentPaymentNewBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var navController: NavController
    private val viewModel: NewPaymentViewModel by viewModel()
    private val args: NewPaymentFragmentArgs by navArgs()
    private var method: String = ""
    private var cash = ""
    private var card = ""
    private var clientId: Int = 0
    private var comment: String = ""
    private var searchValue: String = ""
    private var clientName = ""
    private var list: MutableSet<String> = mutableSetOf()
    private var listIds: MutableMap<String, Int> = mutableMapOf()
    private lateinit var adapter: ArrayAdapter<String>


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPaymentNewBinding.bind(view)
        abBinding = ActionBarBinding.bind(view)
        navController = findNavController()
        setUpObservers()

        val fromClientFragment = args.client != "null"

        abBinding.apply {
            tvTitle.text = context?.getString(R.string.new_payment)
            btnHome.onClick {
                navController.popBackStack()
            }
        }

        binding.apply {
            if (fromClientFragment) {
                val client = Gson().fromJson(args.client, Client::class.java)
                tilClient.isEnabled = false
                etSearchClient.setText(client.name)
                clientId = client.id
            }

            etPaymentCard.addTextChangedListener(MaskWatcherPayment(etPaymentCard))
            etPaymentCash.addTextChangedListener(MaskWatcherPayment(etPaymentCash))

            tvMethodStr.text = view.context.getString(R.string.method_uzb)
            rb1.text = view.context.getString(R.string.cash_uzb)
            rb2.text = view.context.getString(R.string.uzcard)
            rb3.text = view.context.getString(R.string.mix)

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
            etSearchClient.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                clientName = parent.getItemAtPosition(position).toString()
                clientId = listIds.getValue(clientName)
            }
            etSearchClient.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    if (p0?.length!! > 0) {
                        list.clear()
                        searchValue = p0.toString()
                        viewModel.searchClient(searchValue)
                    }
                }

            })

            btnAddPayment.onClick {
                if (method.isEmpty()) {
                    showMessage("To'lov turini tanlang")
                } else if (etPaymentCard.text!!.isEmpty() && etPaymentCash.text!!.isEmpty() && etSearchClient.text!!.isEmpty()) {
                    showMessage("Iltimos mijozni tanlang va to'lovni to'liq kiriting")
                } else if (rb1.isChecked && etPaymentCash.text!!.isNotEmpty() && etSearchClient.text!!.isNotEmpty()) {
                    cash = etPaymentCash.text.toString()
                    cash = cash.replace("\\s".toRegex(), "")
                    cash = cash.substring(0, cash.length - 3)
                    viewModel.newPayment(NewPayment(clientId, cash.toInt(), 0, comment))
                } else if (rb2.isChecked && etPaymentCard.text!!.isNotEmpty() && etSearchClient.text!!.isNotEmpty()) {
                    card = etPaymentCard.text.toString()
                    card = card.replace("\\s".toRegex(), "")
                    card = card.substring(0, card.length - 3)
                    viewModel.newPayment(NewPayment(clientId, 0, card.toInt(), comment))
                } else if (rb3.isChecked && etPaymentCash.text!!.isNotEmpty() && etPaymentCard.text!!.isNotEmpty() && etSearchClient.text!!.isNotEmpty()) {
                    cash = etPaymentCash.text.toString()
                    cash = cash.replace("\\s".toRegex(), "")
                    cash = cash.substring(0, cash.length - 3)
                    card = etPaymentCard.text.toString()
                    card = card.replace("\\s".toRegex(), "")
                    card = card.substring(0, card.length - 3)
                    comment = etComment.text.toString()
                    viewModel.newPayment(NewPayment(clientId, cash.toInt(), card.toInt(), comment))
                } else {
                    showMessage("Iltimos mijozni tanlang va to'lovni to'liq kiriting")
                }
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            scrollView.isEnabled = !loading
        }
    }

    private fun setUpObservers() {
        viewModel.newPayment.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    if (it.data!!.successful) {
                        val alertDialog = AlertDialog.Builder(requireContext())
                        alertDialog.setTitle("Muvaffaqiyatli!")
                        alertDialog.setMessage("To'lov muvaffaqiyatli amalga oshirildi!")
                        alertDialog.show()
                        binding.apply {
                            etSearchClient.text.clear()
                            etPaymentCard.text!!.clear()
                            etPaymentCash.text!!.clear()
                            etComment.text!!.clear()
                        }
                    } else {
                        showMessage(it.data.message)
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showMessage(it.message)
                }
            }
        }

        viewModel.searchClient.observe(viewLifecycleOwner,{
            when(it.status){
                ResourceState.LOADING->setLoading(true)
                ResourceState.SUCCESS->{setLoading(false)
                if (it.data!!.successful)
                {
                    it.data.payload.forEach {data->
                        list.add("${data.name}, ${data.phone}")
                        if (!listIds.contains("${data.name}, ${data.phone}")) listIds["${data.name}, ${data.phone}"]=data.clientId
                        adapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,list.toMutableList())
                        binding.etSearchClient.setAdapter(adapter)
                 }
                }else{
                    Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()}
                }
                ResourceState.ERROR->{
                    setLoading(false)
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()}
                }
        })
    }
}
