package com.ariabagas.storiaapp.ui.home

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.ariabagas.storiaapp.R
import com.ariabagas.storiaapp.data.local.datastore.UserPreference
import com.ariabagas.storiaapp.databinding.ActivityMapStoryBinding
import com.ariabagas.storiaapp.utils.NotifUtils
import com.ariabagas.storiaapp.utils.ResultState
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class MapStoryActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapStoryBinding
    private val prefs: UserPreference by inject()
    private val viewModel: MapViewModel by viewModel()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var token: String

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> getMyCurrentLocation()
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> getMyCurrentLocation()
        }
    }

    private val resolutionLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode != RESULT_OK) {
            NotifUtils.show(this, "Please enable your GPS", isError = true)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            prefs.tokenFlow().collectLatest { savedToken ->
                token = savedToken ?: ""
                if (token.isNotBlank()) {
                    viewModel.loadStoriesWithLocation(token)
                }
            }
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true

        setMyMapStyle()
        createLocationRequest()
        observeStories()
    }

    private fun setMyMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Toast.makeText(this, "Style parsing failed.", Toast.LENGTH_SHORT).show()
            }
        } catch (exception: Resources.NotFoundException) {
            Toast.makeText(this, "Can't find style.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeStories() {
        viewModel.storiesWithLocation.observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {

                }

                is ResultState.Success -> {
                    mMap.clear()

                    result.data.forEach { story ->
                        if (story.lat != null && story.lon != null) {
                            val position = LatLng(story.lat, story.lon)

                            loadCircularMarkerIcon(story.photoUrl) { icon ->
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(position)
                                        .title(story.name)
                                        .snippet(story.description)
                                        .icon(icon)
                                )
                            }
                        }
                    }

                    val indonesiaCenter = LatLng(-2.600028, 118.015778)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(indonesiaCenter, 3f))
                }

                is ResultState.Error -> {
                    NotifUtils.show(this, result.message, isError = true)

                }
            }
        }
    }

    private fun loadCircularMarkerIcon(imageUrl: String, callback: (BitmapDescriptor?) -> Unit) {
        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .circleCrop()
            .override(80, 80)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    callback(BitmapDescriptorFactory.fromBitmap(resource))
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            TimeUnit.SECONDS.toMillis(2)
        ).apply {
            setMaxUpdateDelayMillis(TimeUnit.SECONDS.toMillis(5))
        }.build()

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)

        client.checkLocationSettings(builder.build())
            .addOnSuccessListener { getMyCurrentLocation() }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        resolutionLauncher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        Toast.makeText(this, sendEx.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun getMyCurrentLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location == null) {
                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}