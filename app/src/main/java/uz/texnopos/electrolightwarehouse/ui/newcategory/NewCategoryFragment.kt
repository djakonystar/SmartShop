package uz.texnopos.electrolightwarehouse.ui.newcategory

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.MaskWatcherNothing
import uz.texnopos.electrolightwarehouse.core.MaskWatcherPercent
import uz.texnopos.electrolightwarehouse.core.ResourceState
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.core.extensions.showMessage
import uz.texnopos.electrolightwarehouse.data.newCategory.NewCategory
import uz.texnopos.electrolightwarehouse.data.newCategory.Percent
import uz.texnopos.electrolightwarehouse.databinding.ActionBarBinding
import uz.texnopos.electrolightwarehouse.databinding.FragmentCategoryNewBinding

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
        setupObserver()

        abBinding.apply {
            tvTitle.text = context?.getString(R.string.new_category)
            btnHome.onClick {
                navController.navigate(R.id.action_newCategoryFragment_to_newProductFragment)
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
                val wholesalePercent = etWholesalePercent.text.toString().filter { p->p.isDigit() }
                val minPercent = etMinPercent.text.toString().filter { p->p.isDigit() }
                val maxPercent = etMaxPercent.text.toString().filter { p->p.isDigit() }
                val minQuantity = etMinQuantity.text.toString().filter { i->i.isDigit() }

                if (category.isNotEmpty() && wholesalePercent.isNotEmpty() && minPercent.isNotEmpty()
                    && maxPercent.isNotEmpty() && minQuantity.isNotEmpty()) {
                    viewModel.createdNewCategory( NewCategory(category,minQuantity.toInt(),
                        Percent(wholesalePercent.toInt(),minPercent.toInt(),maxPercent.toInt())
                    ))
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
    private fun setupObserver(){
        viewModel.newCategory.observe(viewLifecycleOwner,{
            when(it.status){
                ResourceState.LOADING->{setLoading(true)}
                ResourceState.SUCCESS->{setLoading(false)
                    if (it.data!!.successful) {
                        val alertDialog = AlertDialog.Builder(requireContext())
                        alertDialog.setTitle("Muvaffaqiyatli!")
                        alertDialog.setMessage("Kategorya muvaffaqiyatli qoshildi!")
                        alertDialog.show()
                        binding.apply {
                            etCategoryName.text!!.clear()
                            etMaxPercent.text!!.clear()
                            etMinPercent.text!!.clear()
                            etMinQuantity.text!!.clear()
                            etWholesalePercent.text!!.clear()
                        }
                        navController.navigate(R.id.action_newCategoryFragment_to_newProductFragment)
                    } else {
                        showMessage(it.data.message)
                    }
                }
                ResourceState.ERROR->{setLoading(false)}
            }
        })
    }
}
