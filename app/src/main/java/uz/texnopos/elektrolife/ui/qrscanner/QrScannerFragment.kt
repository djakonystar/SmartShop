package uz.texnopos.elektrolife.ui.qrscanner

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.extensions.checkForPermissions
import uz.texnopos.elektrolife.databinding.FragmentQrScannerBinding

class QrScannerFragment : Fragment(R.layout.fragment_qr_scanner) {
    private lateinit var binding: FragmentQrScannerBinding
    private lateinit var codeScanner: CodeScanner
    private lateinit var navController: NavController

    companion object {
        const val RESULT_TEXT = "resultCode"
        const val BASKET = "basket"
        const val PRODUCT = "product"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkForPermissions(Manifest.permission.CAMERA)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentQrScannerBinding.bind(view)
        navController = findNavController()

        val activity = requireActivity()

        binding.apply {
            codeScanner = CodeScanner(activity, scannerView)
            codeScanner.decodeCallback = DecodeCallback { res ->
                val result = res.text
                activity.runOnUiThread {
                    val previousBackStackEntry = navController.previousBackStackEntry
                    val savedStateHandle = previousBackStackEntry?.savedStateHandle
                    savedStateHandle?.set(RESULT_TEXT, result)
                    navController.navigateUp()
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
