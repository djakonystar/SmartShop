package uz.texnopos.elektrolife.ui.newcategory

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.*
import uz.texnopos.elektrolife.data.model.newcategory.CategoryPost
import uz.texnopos.elektrolife.data.model.newcategory.Percent
import uz.texnopos.elektrolife.databinding.ActionBarBinding
import uz.texnopos.elektrolife.databinding.FragmentCategoryNewBinding

class NewCategoryFragment : Fragment(R.layout.fragment_category_new) {
    private lateinit var binding: FragmentCategoryNewBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var navController: NavController
    private val viewModel: NewCategoryViewModel by viewModel()

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
            etWholesalePercent.filterForDouble
            etMinPercent.filterForDouble
            etMaxPercent.filterForDouble
            etMinQuantity.filterForDouble

            etCategoryName.addTextChangedListener {
                tilCategoryName.isErrorEnabled = false
            }

            btnAddCategory.onClick {
                val category = etCategoryName.text.toString()
                val wholesalePercent = etWholesalePercent.text.toString().toDouble
                val minPercent = etMinPercent.text.toString().toDouble
                val maxPercent = etMaxPercent.text.toString().toDouble
                val minQuantity = etMinQuantity.text.toString().toDouble

                if (category.isNotEmpty()) {
                    viewModel.createCategory(
                        CategoryPost(
                            category,
                            Percent(wholesalePercent, minPercent, maxPercent, minQuantity)
                        )
                    )
                    setUpObserver()
                } else {
                    if (category.isEmpty()) {
                        tilCategoryName.error = context?.getString(R.string.required_field)
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

    private fun setUpObserver() {
        viewModel.newCategory.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    showSuccess(getString(R.string.category_added_successfully))
                        .setOnPositiveButtonClickListener {
                            navController.popBackStack()
                        }
                    binding.apply {
                        etCategoryName.text!!.clear()
                        etWholesalePercent.text!!.clear()
                        etMinPercent.text!!.clear()
                        etMaxPercent.text!!.clear()
                        etMinQuantity.text!!.clear()
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }
    }
}
