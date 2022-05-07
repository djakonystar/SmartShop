package uz.texnopos.elektrolife.ui.qrscanner

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.google.gson.GsonBuilder
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.checkForPermissions
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.showError
import uz.texnopos.elektrolife.data.model.newproduct.Price
import uz.texnopos.elektrolife.data.model.newproduct.TransactionTransfer
import uz.texnopos.elektrolife.databinding.FragmentQrScannerBinding
import uz.texnopos.elektrolife.ui.dialog.TransactionDialog
import uz.texnopos.elektrolife.ui.warehouse.WarehouseViewModel

class QrScannerFragment : Fragment(R.layout.fragment_qr_scanner) {
    private lateinit var binding: FragmentQrScannerBinding
    private lateinit var codeScanner: CodeScanner
    private lateinit var navController: NavController
    private val viewModel: QrScannerViewModel by viewModel()
    private val args: QrScannerFragmentArgs by navArgs()
    private lateinit var type: String

    companion object {
        const val GET_BASKET = "basket"
        const val GET_PRODUCT = "product"
        const val POST_TRANSACTION = "transaction"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkForPermissions
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentQrScannerBinding.bind(view)
        navController = findNavController()
        type = args.type

        val activity = requireActivity()

        binding.apply {
            codeScanner = CodeScanner(activity, scannerView)
            codeScanner.decodeCallback = DecodeCallback {
                val result = it.text
                activity.runOnUiThread {
                    when (type) {
                        GET_BASKET -> {
                            if (result.startsWith(GET_BASKET)) {
                                val arguments = result.split("\n")
                                val type = arguments[0]
                                val uuid = arguments[1]
                                viewModel.getBasket(type, uuid)
                            } else {
                                showError("This is not a order code")
                                    .setOnDismissListener {
                                        codeScanner.startPreview()
                                    }
                            }
                        }
                        GET_PRODUCT -> {
                            if (result.startsWith(GET_PRODUCT)) {
                                val arguments = result.split("\n")
                                val type = arguments[0]
                                val uuid = arguments[1]
                                viewModel.getProduct(type, uuid)
                            } else {
                                showError("This is not a product code")
                                    .setOnDismissListener {
                                        codeScanner.startPreview()
                                    }
                            }
                        }
                        POST_TRANSACTION -> {
                            if (result.startsWith(GET_PRODUCT)) {
                                val arguments = result.split("\n")
                                val type = arguments[0]
                                val uuid = arguments[1]
                                viewModel.getProduct(type, uuid)
                            } else {
                                showError("This is not a product code")
                                    .setOnDismissListener {
                                        codeScanner.startPreview()
                                    }
                            }
                        }
                    }
                }
            }

            scannerView.onClick {
                codeScanner.startPreview()
            }
        }

        setUpObservers()
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            scannerView.isEnabled = !loading
        }
    }

    private fun setUpObservers() {
        viewModel.order.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    val basket = it.data!!
                    val basketString = GsonBuilder().setPrettyPrinting().create().toJson(basket)
                    navController.navigate(
                        QrScannerFragmentDirections.actionQrScannerFragmentToReturnOrderFragment(
                            basketString
                        )
                    )
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message).setOnDismissListener {
                        codeScanner.startPreview()
                    }
                }
            }
        }

        viewModel.product.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    val product = it.data!!
                    when (type) {
                        GET_PRODUCT -> {
                            val productString =
                                GsonBuilder().setPrettyPrinting().create().toJson(product)
                            navController.navigate(
                                QrScannerFragmentDirections.actionQrScannerFragmentToNewSaleFragment(
                                    productString
                                )
                            )
                        }
                        POST_TRANSACTION -> {
                            val transaction = TransactionTransfer(
                                product.id,
                                product.name,
                                product.count,
                                product.warehouse?.unit?.id ?: 1,
                                Price(product.costPrice.currencyId, product.costPrice.price)
                            )
                            val dialog = TransactionDialog(transaction)
                            dialog.show(requireActivity().supportFragmentManager, dialog.tag)
                            dialog.setOnDismissListener {
                                navController.popBackStack()
                            }
                        }
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message).setOnDismissListener {
                        codeScanner.startPreview()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}
