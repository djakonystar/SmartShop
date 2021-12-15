package uz.texnopos.electrolightwarehouse.ui.newsale

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.GsonBuilder
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.MarginItemDecoration
import uz.texnopos.electrolightwarehouse.core.ResourceState
import uz.texnopos.electrolightwarehouse.core.extensions.dp
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.databinding.FragmentNewSaleBinding
import uz.texnopos.electrolightwarehouse.ui.newsale.dialog.AddToBasketDialog

class NewSaleFragment: Fragment(R.layout.fragment_new_sale) {

    private lateinit var binding: FragmentNewSaleBinding
    private val productNewSaleAdapter:NewSaleProductAdapter by inject()
    private val categoryNewSaleAdapter:CategoryNewSaleAdapter by inject()
    private val categoryViewModel:CategoriesViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNewSaleBinding.bind(view)
        categoryViewModel.getCategories()
        setObservers()
        binding.apply {
            actionBar.actionBar.title = getString(R.string.new_sale)
            rvHorizontalCategories.adapter = categoryNewSaleAdapter
            rvNewSale.adapter = productNewSaleAdapter
            rvNewSale.addItemDecoration(MarginItemDecoration(4.dp))
            swipeRefreshLayout.isRefreshing = false
            swipeRefreshLayout.setOnRefreshListener {
                categoryViewModel.getCategories()
            }
            productNewSaleAdapter.onItemClickListener {
                val dialog = AddToBasketDialog(it)
                dialog.show(requireActivity().supportFragmentManager,"")
                Basket.addProduct(it){ p ->
                    dialog.onItemClickListener { quantity, totalPrice ->
                        Basket.setProduct(p, quantity, totalPrice.toInt())
                    }
                }
            }

            actionBar.btnBack.onClick {
                findNavController().popBackStack()
                Basket.mutableProducts.clear()
            }

            actionBar.etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    p0?.let {
                        if (it.isEmpty()) {
                            productNewSaleAdapter.models = productNewSaleAdapter.allModel
                        } else {
                            categoryViewModel.getProductByName(it.toString())
                        }
                    }
                }

            })

            btnFab.onClick {
                if (Basket.products.isNotEmpty()){
                    val gsonPretty = GsonBuilder().setPrettyPrinting().create()
                    val gsonString = gsonPretty.toJson(Basket.products)
                    findNavController().navigate(NewSaleFragmentDirections.actionNewSaleFragmentToOrderFragment(gsonString))

                    }else{
                    Toast.makeText(requireContext(), "Tovarni qoshing!", Toast.LENGTH_SHORT).show()
                }
                }
            categoryNewSaleAdapter.onItemClickListener {
                categoryViewModel.getCategoryById(it)
            }
        }
    }

    private fun setObservers(){
        categoryViewModel.categories.observe(viewLifecycleOwner,{
            when(it.status){
                ResourceState.LOADING->{
                    binding.swipeRefreshLayout.isRefreshing = true
                }
                ResourceState.SUCCESS->{
                    binding.swipeRefreshLayout.isRefreshing = false
                    categoryNewSaleAdapter.models = it.data!!
                }
                ResourceState.ERROR->{
                    binding.swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
        categoryViewModel.products.observe(viewLifecycleOwner,{
            when(it.status){
                ResourceState.LOADING->{
                    binding.swipeRefreshLayout.isRefreshing = true
                }
                ResourceState.SUCCESS->{
                    binding.swipeRefreshLayout.isRefreshing = false
                    productNewSaleAdapter.allModel = it.data!!.products
                }
                ResourceState.ERROR->{
                    binding.swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}