package kg.forestry.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kg.forestry.R
import kg.core.base.BaseActivity


class MapAllPointsActivity : BaseActivity<MapAllPointViewModel>(R.layout.activity_map_all_points,MapAllPointViewModel::class), OnMapReadyCallback {

    private val REQUEST_LOCATION: Int = 1001
    private var moveToMyLocationOnce = false
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadMapWithPermission()
    }

    private fun loadMapWithPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                REQUEST_LOCATION
            )
        } else {
            loadMap()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION) {
            loadMapWithPermission()
        }
    }

    private fun loadMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.isMyLocationEnabled = true

        mMap.setOnMyLocationChangeListener { location ->
            if (moveToMyLocationOnce) {
                mMap.setOnMyLocationChangeListener(null)
                return@setOnMyLocationChangeListener
            }
            moveToMyLocationOnce = true
            val center = CameraUpdateFactory.newLatLng(
                LatLng(
                    location.latitude,
                    location.longitude
                )
            )
            val zoom = CameraUpdateFactory.zoomTo(18f)
            mMap.moveCamera(center)
            mMap.animateCamera(zoom)
        }

        addMarkers()
    }

    private fun addMarkers() {
        vm.getAllLocations().forEachIndexed { index, location ->
            val latLng = LatLng(location.lat, location.lon)
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            markerOptions.title("Точка на карте ${index + 1}")
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker))
            mMap.addMarker(markerOptions)
        }
    }

    companion object {
        fun start(context: Activity) {
            val intent = Intent(context, MapAllPointsActivity::class.java)
            context.startActivity(intent)
        }
    }
}
