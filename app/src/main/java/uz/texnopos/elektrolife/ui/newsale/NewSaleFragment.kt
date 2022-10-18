package uz.texnopos.elektrolife.ui.newsale

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.stfalcon.imageviewer.StfalconImageViewer
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.showError
import uz.texnopos.elektrolife.core.extensions.showMessage
import uz.texnopos.elektrolife.data.model.category.CategoryResponse
import uz.texnopos.elektrolife.data.model.newsale.Product
import uz.texnopos.elektrolife.databinding.ActionBarNewSaleBinding
import uz.texnopos.elektrolife.databinding.FragmentNewSaleBinding
import uz.texnopos.elektrolife.ui.newsale.dialog.AddToBasketDialog
import uz.texnopos.elektrolife.ui.qrscanner.QrScannerFragment

class NewSaleFragment : Fragment(R.layout.fragment_new_sale) {
    private lateinit var binding: FragmentNewSaleBinding
    private lateinit var abBinding: ActionBarNewSaleBinding
    private lateinit var navController: NavController
    private val viewModel: NewSaleViewModel by viewModel()
    private val categoryViewModel: CategoryViewModel by viewModel()
    private val adapter: NewSaleProductAdapter by inject()
    private var productsList = mutableListOf<Product>()
    private var allProductsList = mutableListOf<Product>()
    private var searchValue = ""
    private var selectedCategoryId = -1
    private var isLoading = false
    private var page = 1
    private var lastPage = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentNewSaleBinding.bind(view)
        abBinding = ActionBarNewSaleBinding.bind(view)
        navController = findNavController()

        val currentBackStackEntry = navController.currentBackStackEntry
        val currentSavedStateHandle = currentBackStackEntry?.savedStateHandle
        val previousBackStackEntry = navController.previousBackStackEntry
        val prevSavedStateHandle = previousBackStackEntry?.savedStateHandle
        observeQrCodeResult(currentSavedStateHandle)
        observeQrCodeResult(prevSavedStateHandle)

        abBinding.apply {
            btnHome.onClick {
                navController.popBackStack()
                hideSoftKeyboard()
            }

            etSearch.addTextChangedListener {
                searchValue = it.toString()
                searchValue.ifEmpty { page = 1 }
                adapter.models = listOf()
                viewModel.getProducts(page, selectedCategoryId, searchValue, 0)
            }

            etSearch.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    viewModel.getProducts(page, selectedCategoryId, searchValue, 0)
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

            btnScanner.onClick {
                navController.navigate(
                    NewSaleFragmentDirections.actionNewSaleFragmentToQrScannerFragment()
                )
            }
        }

        binding.apply {
            val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = layoutManager
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!isLoading && adapter.models.isNotEmpty() && page < lastPage &&
                        layoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount - 1
                    ) {
                        page++
                        viewModel.getProducts(page, selectedCategoryId, searchValue, 0)
                    }
                }
            })

            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = false
                setLoading(false)
                chipGroup.removeAllViews()
                productsList = mutableListOf()
                categoryViewModel.getCategories()
                page = 1
                adapter.models = listOf()
                viewModel.getProducts(page, searchValue, 0)
            }

            adapter.onItemClickListener { product ->
                val dialog = AddToBasketDialog(product)
                dialog.show(requireActivity().supportFragmentManager, dialog.tag)
                dialog.setOnItemAddedListener { quantity, salePrice ->
                    Basket.setProduct(product, quantity, salePrice)
                }
                dialog.setOnDismissListener {
                    hideSoftKeyboard()
                }
            }

            adapter.onImageClickListener { product, imageView ->
                product.image?.let { imageUrl ->
                    StfalconImageViewer.Builder(context, arrayOf(imageUrl)) { view, url ->
                        Glide.with(requireContext())
                            .load(url)
                            .placeholder(R.drawable.image_placeholder)
                            .into(view)
                    }
                        .allowSwipeToDismiss(true)
                        .withTransitionFrom(imageView)
                        .show()
                }
            }

            btnFab.onClick {
                if (Basket.products.isNotEmpty()) {
                    findNavController().navigate(
                        NewSaleFragmentDirections.actionNewSaleFragmentToOrderFragment()
                    )
                } else {
                    context?.getString(R.string.basket_empty_warning)?.let { text ->
                        Snackbar.make(btnFab, text, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }

//            root.viewTreeObserver.addOnGlobalLayoutListener {
//                val r = Rect()
//                root.getWindowVisibleDisplayFrame(r)
//                val screenHeight = root.rootView.height
//                val keypadHeight = screenHeight - r.bottom
//
//                if (keypadHeight <= screenHeight * 0.15) {
//                    btnFab.show()
//                }
//            }
        }

        categoryViewModel.getCategories()
        viewModel.getProducts(page, searchValue, 0)
        setUpObservers()
    }

    override fun onDetach() {
        adapter.models = listOf()
        super.onDetach()
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            btnFab.isEnabled = !loading
        }
    }

    private fun showLottieAnimation(show: Boolean) {
        binding.apply {
            lottieAnimation.isVisible = show
            recyclerView.isVisible = !show
            lottieAnimation.playAnimation()
        }
    }

    private fun setUpObservers() {
        categoryViewModel.categories.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    it.data!!.forEach { category ->
                        addNewChip(category)
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }

        viewModel.products.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    binding.btnFab.show()
                    lastPage = it.data!!.lastPage
                    allProductsList = it.data.data as MutableList<Product>
                    if (adapter.models.isEmpty()) {
                        adapter.models = allProductsList
                        productsList = allProductsList
                    } else {
                        allProductsList.forEach { product ->
                            if (!productsList.contains(product)) {
                                productsList.add(product)
                            }
                        }
                        adapter.models = productsList
                    }
                    showLottieAnimation(productsList.isEmpty())
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }
    }

    private fun observeQrCodeResult(savedStateHandle: SavedStateHandle?) {
        savedStateHandle?.getLiveData<String>(QrScannerFragment.RESULT_TEXT)
            ?.observe(viewLifecycleOwner) { result ->
                if (result.startsWith(QrScannerFragment.PRODUCT)) {
                    val arguments = result.split("\n")
                    val type = arguments[0]
                    val uuid = arguments[1]
                    viewModel.getProduct(type, uuid)
                    viewModel.product.observe(viewLifecycleOwner) {
                        when (it.status) {
                            ResourceState.LOADING -> setLoading(true)
                            ResourceState.SUCCESS -> {
                                setLoading(false)
                                val product = it.data!!
                                val dialog = AddToBasketDialog(product)
                                dialog.show(requireActivity().supportFragmentManager, dialog.tag)
                                dialog.setOnItemAddedListener { quantity, salePrice ->
                                    Basket.setProduct(product, quantity, salePrice)
                                }
                                dialog.setOnDismissListener {
                                    hideSoftKeyboard()
                                }
                            }
                            ResourceState.ERROR -> {
                                setLoading(false)
                                showError(it.message)
                            }
                        }
                    }
                    savedStateHandle.remove(QrScannerFragment.RESULT_TEXT)
                } else {
                    showError(getString(R.string.product_code_error))
                }
            }
    }

    private fun addNewChip(category: CategoryResponse) {
        try {
            binding.apply {
                val inflater = LayoutInflater.from(requireContext())

                val newChip =
                    inflater.inflate(R.layout.item_chip_choice, chipGroup, false) as Chip
                newChip.text = category.name

                chipGroup.addView(newChip)

                newChip.setOnCheckedChangeListener { buttonView, isChecked ->
                    selectedCategoryId = if (isChecked) {
                        chipGroup.check((buttonView as Chip).id)
                        category.id
                    } else {
                        -1
                    }

                    page = 1
                    adapter.models = listOf()
                    viewModel.getProducts(page, selectedCategoryId, searchValue, 0)
                }
            }
        } catch (e: Exception) {
            showError(e.localizedMessage)
        }
    }

    private fun hideSoftKeyboard() {
        val imm =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var v = requireActivity().currentFocus
        if (v == null) {
            v = View(activity)
        }
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }
}
