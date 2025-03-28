package com.example.androidtbc.presentation.imageselect

import android.app.Activity
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.androidtbc.R
import com.example.androidtbc.databinding.FragmentImageSelectBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.presentation.extension.launchLatest
import com.example.androidtbc.presentation.extension.loadImage
import com.example.androidtbc.presentation.extension.showSnackbar
import com.example.androidtbc.presentation.imageaction.ImageAction
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ImageSelectFragment : BaseFragment<FragmentImageSelectBinding>(FragmentImageSelectBinding::inflate) {
    private val viewModel: ImageSelectViewModel by viewModels()
    private var hasShowSuccessSnackbar = false

    private val imageResultListener =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let {
                    viewModel.onAction(ImageSelectAction.UriCreated(it))
                }
                viewModel.onAction(ImageSelectAction.ProcessImage)
            }
        }

    override fun start() {
        setUpListeners()
        observeStates()
        observeEvents()
    }

    private fun setUpListeners() {
        binding.btnSelectImage.setOnClickListener {
            val action = ImageSelectFragmentDirections.actionImageSelectorFragmentToImageActionBottomSheetFragment()
            findNavController().navigate(action)
        }

        binding.btnClearImage.setOnClickListener {
            viewModel.onAction(ImageSelectAction.ClearImage)
        }

        binding.btnUploadImage.setOnClickListener {
            viewModel.onAction(ImageSelectAction.UploadImage)
        }

        setFragmentResultListener("request_key") { _, bundle ->
            val itemName = bundle.getString("selected_item_key")
            itemName?.let {
                val item = ImageAction.valueOf(itemName)
                handleItemAction(item)
            }
        }
    }

    private fun observeStates() {
        launchLatest(viewModel.state) { state ->
            updateUiFromState(state)

        }
    }

    private fun observeEvents() {
        launchLatest(viewModel.eventChannel) { event ->
            handleEvent(event)
        }
    }

    private fun updateUiFromState(state: ImageSelectState){
        with(binding) {
            state.compressedImage?.let { bitmap ->
                ivSelectedImage.loadImage(bitmap)
                btnClearImage.visibility = View.VISIBLE
                btnUploadImage.visibility = View.VISIBLE
            } ?: run {
                ivSelectedImage.setImageDrawable(null)
                btnClearImage.visibility = View.GONE
                btnUploadImage.visibility = View.GONE
            }

            progressOverlay.visibility = if (state.isUploading) View.VISIBLE else View.GONE

            btnUploadImage.isEnabled = !state.isUploading
            btnSelectImage.isEnabled = !state.isUploading
            btnClearImage.isEnabled = !state.isUploading
        }

    }

    private fun handleEvent(event: ImageSelectEvent) {
        when (event) {
            is ImageSelectEvent.UriCreated -> {
                binding.root.showSnackbar(getString(R.string.image_selected))
            }
            is ImageSelectEvent.ImageCompressed -> {
                binding.root.showSnackbar(getString(R.string.image_compressed))
            }
            is ImageSelectEvent.ImageCleared -> {
                binding.root.showSnackbar(getString(R.string.image_cleared))
            }
            is ImageSelectEvent.UploadStarted -> {
                binding.root.showSnackbar(getString(R.string.upload_started))
            }
            is ImageSelectEvent.UploadCompleted -> {
                if (!hasShowSuccessSnackbar) {
                    binding.root.showSnackbar(getString(R.string.upload_success))
                    hasShowSuccessSnackbar = true
                }
            }
            is ImageSelectEvent.UploadFailed -> {
                binding.root.showSnackbar("${getString(R.string.upload_failure)}: ${event.error}")
            }
        }
    }



    private fun handleItemAction(item: ImageAction) {
        when (item) {
            ImageAction.GALLERY -> selectImageFromGallery()
            ImageAction.CAMERA -> takePictureWithCamera()
        }
    }
    private fun selectImageFromGallery(){
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        imageResultListener.launch(intent)
    }

    private fun takePictureWithCamera() {
        val photoFile = createImageFile()
        val photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            photoFile
        )

        viewModel.onAction(ImageSelectAction.UriCreated(photoUri))
        imageResultListener.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        })
    }

    private fun createImageFile(): File {
        val timeStamp = System.currentTimeMillis()
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMAGE_${timeStamp}_", ".jpg", storageDir)
    }
}
