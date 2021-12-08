package uz.texnopos.electrolightwarehouse.ui.newcategory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.databinding.ActionBarBinding
import uz.texnopos.electrolightwarehouse.databinding.FragmentCategoryNewBinding

class NewCategoryFragment: Fragment(R.layout.fragment_category_new) {
    private var _binding: FragmentCategoryNewBinding?=null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var abBinding: ActionBarBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryNewBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        abBinding = ActionBarBinding.bind(view)
        abBinding.apply {
            tvTitle.text = context?.getString(R.string.new_category)
            btnHome.onClick {
                navController.popBackStack()
            }
        }
            binding.apply {
                btnAddCategory.onClick {
                    if (etCategoryName.text!!.isNotEmpty() && etWholesalePrice.text!!.isNotEmpty()&&etMinPrice.text!!.isNotEmpty() && etMaxPrice.text!!.isNotEmpty()&&etMinQuantity.text!!.isNotEmpty()){

                    }else{
                        Toast.makeText(requireContext(), "Bosh joylarni toltiring", Toast.LENGTH_SHORT).show()
                    }

                }
            }

    }
}