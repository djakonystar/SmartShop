package uz.texnopos.elektrolife.ui.company

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.shaon2016.propicker.pro_image_picker.ProPicker
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.MaskWatcher
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.*
import uz.texnopos.elektrolife.data.model.company.CompanyDetail
import uz.texnopos.elektrolife.databinding.ActionBarBinding
import uz.texnopos.elektrolife.databinding.FragmentCompanyDetailsBinding
import uz.texnopos.elektrolife.settings.Settings
import uz.texnopos.elektrolife.ui.newproduct.ImageViewModel
import java.io.File

class CompanyDetailsFragment : Fragment(R.layout.fragment_company_details) {
    private lateinit var binding: FragmentCompanyDetailsBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var navController: NavController
    private val settings: Settings by inject()
    private val viewModel: CompanyDetailsViewModel by viewModel()
    private val imageViewModel: ImageViewModel by viewModel()
    private var imagePart: MultipartBody.Part = MultipartBody.Part.createFormData("file", "")
    private var presetPart: MultipartBody.Part =
        MultipartBody.Part.createFormData("upload_preset", "smart-shop")
    private var imageSelected = false
    private var imageUrl = ""

    private var name: String = ""
    private var address = ""
    private var phone = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCompanyDetailsBinding.bind(view)
        abBinding = ActionBarBinding.bind(view)
        navController = findNavController()

        checkForPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)

        abBinding.apply {
            tvTitle.text = getString(R.string.company_details)
            btnHome.onClick {
                navController.popBackStack()
            }
        }

        binding.apply {
            etPhone.addTextChangedListener(MaskWatcher.phoneNumberBySpaces())

            etCompanyName.addTextChangedListener {
                tilCompanyName.isErrorEnabled = false
            }

            etAddress.addTextChangedListener {
                tilAddress.isErrorEnabled = false
            }

            etPhone.addTextChangedListener {
                tilPhone.isErrorEnabled = false
            }

            cvImage.onClick {
                ProPicker.with(this@CompanyDetailsFragment)
                    .compressImage()
                    .galleryOnly()
                    .maxResultSize(720, 720)
                    .crop(800f, 300f)
                    .onlyImage()
                    .start { resultCode, data ->
                        if (resultCode == Activity.RESULT_OK && data != null) {
                            val picker = ProPicker.getPickerData(data)
                            val fileUri = picker?.uri
                            binding.ivLogotype.setImageURI(fileUri)

                            val file = File(fileUri?.path!!)
                            val image = file.asRequestBody("image/*".toMediaTypeOrNull())

                            imagePart =
                                MultipartBody.Part.createFormData("file", file.name, image)
                            imageSelected = true
                        }
                    }
            }

            btnSave.onClick {
                name = etCompanyName.text.toString()
                address = etAddress.text.toString()
                phone = etPhone.text.toString().filter { c -> c.isDigit() }

                if (name.isNotEmptyAndBlank() && address.isNotEmptyAndBlank() && phone.isNotEmpty()) {
                    if (imageSelected) {
                        imageViewModel.uploadImage(Constants.CLOUD_NAME, imagePart, presetPart)
                    } else {
                        viewModel.updateCompanyDetails(
                            CompanyDetail(name, address, phone, imageUrl)
                        )
                    }
                } else {
                    if (name.isEmptyOrBlank()) {
                        tilCompanyName.error = getString(R.string.required_field)
                    }
                    if (address.isEmptyOrBlank()) {
                        tilAddress.error = getString(R.string.required_field)
                    }
                    if (phone.isEmpty()) {
                        tilPhone.error = getString(R.string.required_field)
                    }
                }
            }
        }

        viewModel.getDetails()
        setUpObservers()
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            tilCompanyName.isEnabled = !loading
            tilAddress.isEnabled = !loading
            tilPhone.isEnabled = !loading
            btnSave.isEnabled = !loading
        }
    }

    private fun setUpObservers() {
        imageViewModel.image.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    imageUrl = it.data?.secureUrl ?: ""
                    viewModel.updateCompanyDetails(
                        CompanyDetail(name, address, phone, imageUrl)
                    )
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }

        viewModel.details.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    it.data?.let { details ->
                        settings.companyName = details.name
                        settings.companyAddress = details.address
                        settings.companyPhone = details.phone
                        settings.logotypeUrl = details.image ?: ""
                        imageUrl = details.image ?: ""

                        binding.apply {
                            etCompanyName.setText(settings.companyName)
                            etAddress.setText(settings.companyAddress)
                            etPhone.setText(settings.companyPhone.toPhoneNumber)

                            if (settings.logotypeUrl.isNotEmpty()) {
                                Glide.with(requireContext())
                                    .load(settings.logotypeUrl)
                                    .into(binding.ivLogotype)
                            } else {
                                binding.ivLogotype.setImageResource(R.drawable.logotype)
                            }
                        }
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }

        viewModel.updateDetails.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    showSuccess(getString(R.string.details_success_msg))
                    viewModel.getDetails()
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }
    }
}
