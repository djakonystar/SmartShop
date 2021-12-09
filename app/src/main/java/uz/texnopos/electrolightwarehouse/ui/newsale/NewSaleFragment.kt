package uz.texnopos.electrolightwarehouse.ui.newsale

import android.os.Bundle
import android.util.Log
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
import uz.texnopos.electrolightwarehouse.data.model.CatalogCategory
import uz.texnopos.electrolightwarehouse.data.model.Product
import uz.texnopos.electrolightwarehouse.databinding.FragmentNewSaleBinding
import uz.texnopos.electrolightwarehouse.ui.newsale.dialog.AddToBasketDialog

class NewSaleFragment: Fragment(R.layout.fragment_new_sale) {

    private lateinit var binding: FragmentNewSaleBinding
    private val productNewSaleAdapter:NewSaleProductAdapter by inject()
    private val categoryNewSaleAdapter:CategoryNewSaleAdapter by inject()
    private val categoryViewModel:CategoriesViewModel by viewModel()
    private val basket = Basket()

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
                basket.addProduct(it){ p->
                    dialog.onItemClickListener { quantity, totalPrice ->
                        basket.setProduct(p,quantity, totalPrice.toInt())
                    }
                }
            }

            btnFab.onClick {
                val gsonPretty = GsonBuilder().setPrettyPrinting().create()
                val gsonString = gsonPretty.toJson(basket.products)
                Log.d("abc", basket.products.size.toString())
                findNavController().navigate(NewSaleFragmentDirections.actionNewSaleFragmentToOrderFragment(gsonString))
            }
            categoryNewSaleAdapter.onItemClickListener {
                categoryViewModel.getCategoryById(it)
            }
        }

        categoryNewSaleAdapter.models = listOf(
            CatalogCategory(1,"asd",2000,3000,4000,false),
            CatalogCategory(2,"asd",2000,3000,4000,false),
            CatalogCategory(3,"asd",2000,3000,4000,false),
            CatalogCategory(4,"asd",2000,3000,4000,false)
        )

        productNewSaleAdapter.models = listOf(
            Product(1,1,"asd","BMW","",2000,
                2000,2000,3000,0,1200),
            Product(2,1,"asd","BMW","",2000,
                2000,2000,3000,0,1200),
            Product(3,1,"asd","BMW","",2000,
                2000,2000,3000,0,1200),
            Product(4,1,"asd","BMW","",2000,
                2000,2000,3000,0,1200),
            Product(5,1,"asd","BMW","",2000,
                2000,2000,3000,0,1200),
            Product(6,1,"asd","BMW","",2000,
                2000,2000,3000,0,1200)
        )
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
                    productNewSaleAdapter.models = it.data!!
                }
                ResourceState.ERROR->{
                    binding.swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}