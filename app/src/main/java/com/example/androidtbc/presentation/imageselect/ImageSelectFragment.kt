package com.example.androidtbc.presentation.imageselect

import android.app.Activity
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.androidtbc.databinding.FragmentImageSelectBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.presentation.extension.launchLatest
import com.example.androidtbc.presentation.extension.loadImage
import com.example.androidtbc.presentation.imageaction.ImageAction
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ImageSelectFragment : BaseFragment<FragmentImageSelectBinding>(FragmentImageSelectBinding::inflate) {
    private val viewModel: ImageSelectViewModel by viewModels()

    private val imageResultListener =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let {
                    viewModel.onEvent(ImageSelectEvent.UriCreated(it))
                }
                viewModel.onEvent(ImageSelectEvent.ProcessImage)
            }
        }


    override fun start() {
        setUpListeners()
        setUpObserver()
    }

    private fun setUpListeners() {
        binding.btnSelectImage.setOnClickListener {
            val action = ImageSelectFragmentDirections.actionImageSelectorFragmentToImageActionBottomSheetFragment()
            findNavController().navigate(action)
        }
        binding.btnClearImage.setOnClickListener {
            viewModel.onEvent(ImageSelectEvent.ClearImage)
        }

        setFragmentResultListener("request_key") { _, bundle ->
            val itemName = bundle.getString("selected_item_key")
            itemName?.let {
                val item = ImageAction.valueOf(itemName)
                handleItemAction(item)
            }

        }
    }

    private fun setUpObserver() {
        launchLatest(viewModel.state) {
            it.compressedImage?.let {
                binding.ivSelectedImage.loadImage(it)
                binding.btnClearImage.visibility = android.view.View.VISIBLE
            } ?: run{
                binding.ivSelectedImage.setImageDrawable(null)
                binding.btnClearImage.visibility = android.view.View.GONE
            }
        }
    }

    private fun handleItemAction(item: ImageAction) {
        when (item) {
            ImageAction.GALLERY -> {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                imageResultListener.launch(intent)
            }

            ImageAction.CAMERA -> {
                openCamera()
            }
        }
    }

    private fun openCamera() {
        val photoFile = createImageFile()
        val photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            photoFile
        )

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }
        viewModel.onEvent(ImageSelectEvent.UriCreated(photoUri))
        imageResultListener.launch(cameraIntent)
    }

    private fun createImageFile(): File {
        val timeStamp = System.currentTimeMillis()
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "IMAGE_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

}