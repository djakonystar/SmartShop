package uz.texnopos.electrolightwarehouse.ui.newsale.order

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.MarginItemDecoration
import uz.texnopos.electrolightwarehouse.core.extensions.dp
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.data.model.Product
import uz.texnopos.electrolightwarehouse.databinding.FragmentOrderBinding
import uz.texnopos.electrolightwarehouse.ui.newsale.dialog.AddClientDialog

class OrderFragment:Fragment(R.layout.fragment_order) {

    private lateinit var binding: FragmentOrderBinding
    private val adapter: OrderAdapter by inject()
    private val gson = Gson()
    private lateinit var safe: Product
    private val safeArgs: OrderFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOrderBinding.bind(view)
        binding.apply {
//            safe = gson.fromJson(safeArgs.products,Product::class.java)
            rvOrder.adapter = adapter
            rvOrder.addItemDecoration(MarginItemDecoration(8.dp))
            btnBack.onClick {
                findNavController().popBackStack()
            }

            btnAddClient.onClick {
                val dialog = AddClientDialog()
                dialog.show(requireActivity().supportFragmentManager,"")

            }

            btnOrder.onClick {

            }

        }
    }
}