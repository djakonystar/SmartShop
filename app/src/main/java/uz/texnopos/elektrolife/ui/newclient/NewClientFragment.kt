package uz.texnopos.elektrolife.ui.newclient

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.MaskWatcher
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.databinding.ActionBarBinding
import uz.texnopos.elektrolife.databinding.FragmentClientNewBinding

class NewClientFragment : Fragment(R.layout.fragment_client_new) {
    private lateinit var binding: FragmentClientNewBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentClientNewBinding.bind(view)
        abBinding = ActionBarBinding.bind(view)
        navController = findNavController()

        abBinding.apply {
            tvTitle.text = context?.getString(R.string.new_client)
            btnHome.onClick {
                navController.popBackStack()
            }
        }

        binding.apply {
            etName.addTextChangedListener {
                tilName.isErrorEnabled = false
            }
            etName.addTextChangedListener {
                tilPhone.isErrorEnabled = false
            }
            etPhone.addTextChangedListener(MaskWatcher.phoneNumberBySpaces())
            etTIN.addTextChangedListener {
                tilTIN.isErrorEnabled = false
            }
            etTIN.addTextChangedListener(MaskWatcher.taxIdentificationNumber())
            etAddress.addTextChangedListener {
                tilAddress.isErrorEnabled = false
            }
            checkboxLegal.setOnCheckedChangeListener { _, isChecked ->
                tilTIN.isVisible = isChecked
                tilAddress.isVisible = isChecked
            }

            btnAdd.onClick {
                hideSoftKeyboard()
                val name = etName.text.toString()
                val phone = etPhone.text.toString().filter { it.isDigit() }
                val comment = etComment.text.toString()
                var tin = ""
                var address = ""

                if (checkboxLegal.isChecked) {
                    tin = etTIN.text.toString().filter { it.isDigit() }
                    address = etAddress.text.toString()

                    if (name.isNotEmpty() && phone.length == 9 && tin.length == 9 && address.isNotEmpty()) {
                        Snackbar.make(
                            binding.btnAdd,
                            context?.getString(R.string.success)!!,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    } else {
                        if (name.isEmpty()) tilName.error = getString(R.string.required_field)
                        if (phone.length != 9) tilPhone.error = getString(R.string.required_field)
                        if (tin.length != 9) tilTIN.error = getString(R.string.required_field)
                        if (address.isEmpty()) tilAddress.error = getString(R.string.required_field)
                    }
                } else {
                    if (name.isNotEmpty() && phone.length == 9) {
                        Snackbar.make(
                            binding.btnAdd,
                            context?.getString(R.string.success)!!,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    } else {
                        if (name.isEmpty()) tilName.error = getString(R.string.required_field)
                        if (phone.length != 9) tilPhone.error = getString(R.string.required_field)
                    }
                }
            }
        }
    }

    private fun hideSoftKeyboard() {
        val view = requireActivity().currentFocus
        if (view != null) {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}