package uz.texnopos.elektrolife.ui.qrscanner

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.extensions.checkForPermissions
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.databinding.FragmentQrScannerBinding

class QrScannerFragment : Fragment(R.layout.fragment_qr_scanner) {
    private lateinit var binding: FragmentQrScannerBinding
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkForPermissions
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentQrScannerBinding.bind(view)

        val activity = requireActivity()

        binding.apply {
            codeScanner = CodeScanner(activity, scannerView)
            codeScanner.decodeCallback = DecodeCallback {
                activity.runOnUiThread {
                    findNavController().navigate(
                        QrScannerFragmentDirections.actionQrScannerFragmentToNewSaleFragment(
                            it.text
                        )
                    )
                }
            }

            scannerView.onClick {
                codeScanner.startPreview()
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
