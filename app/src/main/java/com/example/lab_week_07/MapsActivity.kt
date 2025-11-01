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

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

     private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

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
                    // Jika diberikan oleh pengguna, jalankan fungsi yang diperlukan (Modul 7, Langkah 15) [cite: 192]
                    getLastLocation()
                } else {
                    // Jika tidak diberikan, tampilkan dialog rasional (Modul 7, Langkah 15) [cite: 194, 195, 198]
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
        // Buat pop up alert dialog
        AlertDialog.Builder(this)
            .setTitle("Location permission") // [cite: 212]
            .setMessage("This app will not work without knowing your current location") // [cite: 213]
            .setPositiveButton (android.R.string.ok) { _, _ -> positiveAction() } // [cite: 213]
            .setNegativeButton (android.R.string.cancel) { dialog, _ -> dialog.dismiss() } // [cite: 213, 214]
            .create().show() // [cite: 215]
    }

    // Fungsi yang akan dipanggil ketika izin lokasi telah diberikan (Modul 7, Langkah 12) [cite: 156, 158]
    private fun getLastLocation() {
        Log.d("MapsActivity", "getLastLocation() called.")
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        when {
            hasLocationPermission() -> getLastLocation() // [cite: 234]

             shouldShowRequestPermissionRationale (ACCESS_FINE_LOCATION) -> {
                showPermissionRationale {
                    requestPermissionLauncher
                        .launch (ACCESS_FINE_LOCATION) // [cite: 239, 240, 241, 242]
                }
            }
            else -> requestPermissionLauncher
                .launch (ACCESS_FINE_LOCATION)
        }
    }
}