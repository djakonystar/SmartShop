package uz.texnopos.electrolightwarehouse.ui.sales.detail

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.ext.getOrCreateScope
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.data.model.ProductInfo
import uz.texnopos.electrolightwarehouse.data.model.Sales
import uz.texnopos.electrolightwarehouse.databinding.ActionBarBinding
import uz.texnopos.electrolightwarehouse.databinding.FragmentSalesDetailBinding
import java.util.*
import kotlin.collections.ArrayList

class SalesDetailFragment : Fragment(R.layout.fragment_sales_detail) {
    private lateinit var binding: FragmentSalesDetailBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var navController: NavController
    private val adapter: SalesDetailAdapter by inject()
    private val safeArgs:SalesDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSalesDetailBinding.bind(view)
        val gson = Gson()
        val list = gson.fromJson(safeArgs.detail, Sales::class.java)
        adapter.allModel = list.orders
        abBinding = ActionBarBinding.bind(view)
        navController = findNavController()
        abBinding.apply {
            tvTitle.text = context?.getString(R.string.sales)
            btnHome.onClick {
                navController.popBackStack()
            }
        }
        binding.apply {
            recyclerView.adapter = adapter
        }
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                p0?.let {
                    if (it.isEmpty()) {
                        adapter.models = adapter.allModel
                    } else {
                        filter(it.toString())
                    }
                }
            }
        })
    }

    private fun filter(text: String) {
        val filteredListName: ArrayList<ProductInfo> = ArrayList()
        for (eachName in adapter.allModel) {
            if (eachName.productName.lowercase(Locale.getDefault())
                    .contains(text.lowercase(Locale.getDefault()))
            ) {
                filteredListName.add(eachName)
            }
        }
        adapter.filterList(filteredListName)
    }

//    private fun setLoading(loading: Boolean) {
//        binding.apply {
//            progressBar.isVisible = loading
//            tilSearch.isEnabled = !loading
//            swipeRefresh.isEnabled = !loading
//        }
//    }
}