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
import uz.texnopos.elektrolife.core.MaskWatcherNothing
import uz.texnopos.elektrolife.core.MaskWatcherPercent
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.showError
import uz.texnopos.elektrolife.core.extensions.showSuccess
import uz.texnopos.elektrolife.data.model.newcategory.NewCategory
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
                val wholesalePercent =
                    etWholesalePercent.text.toString().filter { p -> p.isDigit() }
                val minPercent = etMinPercent.text.toString().filter { p -> p.isDigit() }
                val maxPercent = etMaxPercent.text.toString().filter { p -> p.isDigit() }
                val minQuantity = etMinQuantity.text.toString().filter { i -> i.isDigit() }

                if (category.isNotEmpty() && wholesalePercent.isNotEmpty() && minPercent.isNotEmpty()
                    && maxPercent.isNotEmpty() && minQuantity.isNotEmpty()
                ) {
                    viewModel.createdNewCategory(
                        NewCategory(
                            category, minQuantity.toInt(),
                            Percent(
                                wholesalePercent.toInt(),
                                minPercent.toInt(),
                                maxPercent.toInt()
                            )
                        )
                    )
                    setupObserver()
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

    private fun setupObserver() {
        viewModel.newCategory.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> {
                    setLoading(true)
                }
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    if (it.data!!.successful) {
                        showSuccess(getString(R.string.category_added_successfully))
                            .setOnPositiveButtonClickListener {
                                navController.popBackStack()
                            }
                        binding.apply {
                            etCategoryName.text!!.clear()
                            etMaxPercent.text!!.clear()
                            etMinPercent.text!!.clear()
                            etMinQuantity.text!!.clear()
                            etWholesalePercent.text!!.clear()
                        }
                    } else {
                        showError(it.data.message)
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
