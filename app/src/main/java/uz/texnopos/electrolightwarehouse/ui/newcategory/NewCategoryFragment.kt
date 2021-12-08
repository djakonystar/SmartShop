package uz.texnopos.electrolightwarehouse.ui.newcategory

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.MaskWatcherNothing
import uz.texnopos.electrolightwarehouse.core.MaskWatcherPercent
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.databinding.ActionBarBinding
import uz.texnopos.electrolightwarehouse.databinding.FragmentCategoryNewBinding

class NewCategoryFragment : Fragment(R.layout.fragment_category_new) {
    private lateinit var binding: FragmentCategoryNewBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCategoryNewBinding.bind(view)
        abBinding = ActionBarBinding.bind(view)
        navController = findNavController()

        abBinding.apply {
            tvTitle.text = context?.getString(R.string.new_category)
            btnHome.onClick {
                navController.popBackStack()
            }
        }

        binding.apply {
            etWholesalePercent.addTextChangedListener(MaskWatcherPercent(etWholesalePercent))
            etMinPercent.addTextChangedListener(MaskWatcherPercent(etMinPercent))
            etMaxPercent.addTextChangedListener(MaskWatcherPercent(etMaxPercent))
            etMinQuantity.addTextChangedListener(MaskWatcherNothing(etMinQuantity))

            etCategoryName.addTextChangedListener {
                tilCategoryName.isErrorEnabled = false
            }
            etWholesalePercent.addTextChangedListener {
                tilWholesalePercent.isErrorEnabled = false
            }
            etMinPercent.addTextChangedListener {
                tilMinPercent.isErrorEnabled = false
            }
            etMaxPercent.addTextChangedListener {
                tilMaxPercent.isErrorEnabled = false
            }
            etMinQuantity.addTextChangedListener {
                tilMinQuantity.isErrorEnabled = false
            }

            btnAddCategory.onClick {
                val category = etCategoryName.text.toString()
                val wholesalePercent = etWholesalePercent.text.toString()
                val minPercent = etMinPercent.text.toString()
                val maxPercent = etMaxPercent.text.toString()
                val minQuantity = etMinQuantity.text.toString()

                if (category.isNotEmpty() && wholesalePercent.isNotEmpty() && minPercent.isNotEmpty()
                    && maxPercent.isNotEmpty() && minQuantity.isNotEmpty()) {
                    // todo send post request for add new category
                } else {
                    if (category.isEmpty()) {
                        tilCategoryName.error = context?.getString(R.string.required_field)
                    }
                    if (wholesalePercent.isEmpty()) {
                        tilWholesalePercent.error = context?.getString(R.string.required_field)
                    }
                    if (minPercent.isEmpty()) {
                        tilMinPercent.error = context?.getString(R.string.required_field)
                    }
                    if (maxPercent.isEmpty()) {
                        tilMaxPercent.error = context?.getString(R.string.required_field)
                    }
                    if (minQuantity.isEmpty()) {
                        tilMinQuantity.error = context?.getString(R.string.required_field)
                    }
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
}
