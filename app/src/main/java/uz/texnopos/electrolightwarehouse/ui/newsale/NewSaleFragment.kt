package uz.texnopos.electrolightwarehouse.ui.newsale

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.databinding.FragmentNewSaleBinding

class NewSaleFragment: Fragment(R.layout.fragment_new_sale) {

    private lateinit var binding: FragmentNewSaleBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNewSaleBinding.bind(view)
        binding.apply {
            actionBar.actionBar.title = getString(R.string.new_sale)
            actionBar.actionBar.
        }
    }
}