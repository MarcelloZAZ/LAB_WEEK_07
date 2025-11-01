package com.example.lab_week_07

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.lab_week_07.databinding.ActivityMapsBinding
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.location.Location // Import Location class
import com.google.android.gms.location.LocationServices // Import LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.CameraUpdateFactory // Import CameraUpdateFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    // Google Play Location service (Modul 7, Langkah 13)
    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        requestPermissionLauncher =
            registerForActivityResult (ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    getLastLocation()
                } else {
                    showPermissionRationale {
                        requestPermissionLauncher.launch (ACCESS_FINE_LOCATION)
                    }
                }
            }
    }

    private fun hasLocationPermission() =
        ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED


    private fun showPermissionRationale (positiveAction: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Location permission")
            .setMessage("This app will not work without knowing your current location")
            .setPositiveButton (android.R.string.ok) { _, _ -> positiveAction() }
            .setNegativeButton (android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .create().show()
    }

    // Fungsi untuk memindahkan kamera peta (Modul 7, Langkah 14)
    private fun updateMapLocation (location: LatLng) {
        mMap.moveCamera (CameraUpdateFactory.newLatLngZoom(
            location, 7f))
    }

    // Fungsi untuk menambahkan marker di lokasi (Modul 7, Langkah 14)
    private fun addMarkerAtLocation (location: LatLng, title: String) {
        mMap.addMarker (MarkerOptions().title(title)
            .position(location))
    }

    // Fungsi getLastLocation() yang diperbarui untuk mengambil dan menampilkan lokasi (Modul 7, Langkah 15)
    private fun getLastLocation() {
        Log.d("MapsActivity", "getLastLocation() called.")

        if (hasLocationPermission()) {
            try {
                fusedLocationProviderClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        location?.let {
                            val userLocation = LatLng(it.latitude, it.longitude)
                            updateMapLocation (userLocation)
                            addMarkerAtLocation (userLocation, "You")
                        }
                    }
            } catch (e: SecurityException) {
                // Seharusnya tidak terjadi karena sudah dicek dengan hasLocationPermission()
                Log.e("MapsActivity", "Security Exception: ${e.message}")
                requestPermissionLauncher.launch (ACCESS_FINE_LOCATION)
            }
        } else {
            // Jika izin entah bagaimana hilang saat fungsi dipanggil, minta lagi.
            requestPermissionLauncher.launch (ACCESS_FINE_LOCATION)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Logika pemeriksaan izin (dari Commit 1)
        when {
            hasLocationPermission() -> getLastLocation()
            shouldShowRequestPermissionRationale (ACCESS_FINE_LOCATION) -> {
                showPermissionRationale {
                    requestPermissionLauncher
                        .launch (ACCESS_FINE_LOCATION)
                }
            }
            else -> requestPermissionLauncher
                .launch (ACCESS_FINE_LOCATION)
        }
    }
}