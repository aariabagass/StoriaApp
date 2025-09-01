package com.ariabagas.storiaapp.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.ariabagas.storiaapp.databinding.ActivityAddStoryBinding
import com.ariabagas.storiaapp.data.local.datastore.UserPreference
import com.ariabagas.storiaapp.utils.NotifUtils
import com.ariabagas.storiaapp.utils.ResultState
import com.ariabagas.storiaapp.utils.reduceFileImage
import com.ariabagas.storiaapp.utils.uriToFile
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class AddStoryActivity : ComponentActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private val viewModel: AddStoryViewModel by viewModel()
    private val prefs: UserPreference by inject()

    private var currentPhotoPath: String? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCameraActivity()
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val file = uriToFile(it, this)
            currentPhotoPath = file.path
            binding.ivPreview.setImageURI(uri)
        }
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CameraActivity.CAMERAX_RESULT) {
            val uri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)
            uri?.let {
                currentPhotoPath = uriToFile(Uri.parse(uri), this).path
                binding.ivPreview.setImageURI(Uri.parse(uri))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
        
        binding.btnGallery.setOnClickListener {
            launcherGallery.launch("image/*")
        }

        binding.btnCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                startCameraActivity()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        binding.btnUpload.setOnClickListener { uploadStory() }
        observeViewModel()
    }

    private fun startCameraActivity() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherCamera.launch(intent)
    }

    private fun uploadStory() {
        val photoPath = currentPhotoPath
        if (photoPath.isNullOrBlank()) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val description = binding.edtDesc.text.toString()
        if (description.isBlank()) {
            Toast.makeText(this, "Description cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val file = File(photoPath).reduceFileImage()

        lifecycleScope.launch {
            prefs.tokenFlow().collect { token ->
                if (!token.isNullOrBlank()) {
                    binding.progressIndicator.visibility = View.VISIBLE
                    viewModel.uploadStory(token, description, file)
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.uploadResult.observe(this) { result ->
            when (result) {
                is ResultState.Loading -> binding.progressIndicator.visibility = View.VISIBLE

                is ResultState.Success -> {
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(this, "Upload Success!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }

                is ResultState.Error -> {
                    binding.progressIndicator.visibility = View.GONE
                    NotifUtils.show(this, result.message, isError = true)
                }
            }
        }
    }
}
