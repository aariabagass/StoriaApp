package com.ariabagas.storiaapp.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class AddStoryActivity : ComponentActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private val viewModel: AddStoryViewModel by viewModel()
    private val prefs: UserPreference by inject()

    private var currentPhotoPath: String? = null
    private var currentLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCameraActivity()
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
                NotifUtils.show(this, "Please enable your camera", isError = true)
            }
        }

    private val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (fineGranted || coarseGranted) {
                getMyCurrentLocation()
            } else {
                showLocationLoading(false)
                binding.cbShareLocation.isEnabled = true
                binding.cbShareLocation.isChecked = false

                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                NotifUtils.show(this, "Please enable your location", isError = true)
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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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

        binding.cbShareLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.cbShareLocation.isEnabled = false
                showLocationLoading(true)

                if (hasLocationPermission()) {
                    getMyCurrentLocation()
                } else {
                    requestLocationPermission.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            } else {
                currentLocation = null
            }
        }

        binding.btnUpload.setOnClickListener { uploadStory() }
        observeViewModel()
    }

    private fun startCameraActivity() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherCamera.launch(intent)
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyCurrentLocation() {
        if (hasLocationPermission()) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    showLocationLoading(false)
                    binding.cbShareLocation.isEnabled = true
                    if (location != null) {
                        currentLocation = location
                    } else {
                        binding.cbShareLocation.isChecked = false
                        Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: SecurityException) {
                showLocationLoading(false)
                binding.cbShareLocation.isEnabled = true
                binding.cbShareLocation.isChecked = false
                Toast.makeText(this, "Location access error", Toast.LENGTH_SHORT).show()
            }
        } else {
            showLocationLoading(false)
            binding.cbShareLocation.isEnabled = true
            binding.cbShareLocation.isChecked = false
        }
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
                    if (binding.cbShareLocation.isChecked && currentLocation != null) {
                        viewModel.uploadStoryWithLocation(
                            token,
                            description,
                            file,
                            currentLocation!!.latitude,
                            currentLocation!!.longitude
                        )
                    } else {
                        viewModel.uploadStory(token, description, file)
                    }
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

    private fun showLocationLoading(show: Boolean) {
        binding.locationProgress.visibility = if (show) View.VISIBLE else View.GONE
    }
}
