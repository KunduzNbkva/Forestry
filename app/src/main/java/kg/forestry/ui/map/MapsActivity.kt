package kg.forestry.ui.map

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
import kg.core.utils.Constants
import kg.core.utils.Location
import kg.forestry.localstorage.Preferences
import kg.forestry.ui.MapAllPointViewModel
import kotlinx.android.synthetic.main.activity_maps.*


class MapsActivity : BaseActivity<MapAllPointViewModel>(
    R.layout.activity_maps,
    MapAllPointViewModel::class), OnMapReadyCallback {

    private val REQUEST_LOCATION: Int = 1001
    private lateinit var mMap: GoogleMap
    private var moveToMyLocationOnce = false
    private lateinit var location: Location

    var isMain = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar.apply {
            title = getString(R.string.map)
            setNavigationOnClickListener { onBackPressed() }
        }
        location = Location(0.0, 0.0)
        loadMapWithPermission()
        btn_next.isEnabled = true
        btn_next.setOnClickListener {
            val target = mMap.cameraPosition.target
            location.apply {
                lat = target.latitude
                lon = target.longitude
            }
            finishActivityWithResultOK(location)
        }

        isMain = intent.getBooleanExtra("isMain", false)
        // set maps was loaded
        if (Preferences(this).isFirstLoad){
            if (vm.isNetworkConnected){
                Preferences(this).isFirstLoad = false
            }
        }
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

    fun finishActivityWithResultOK(location: Location){
        val intent = Intent()
        intent.putExtra(Constants.LOCATION,location)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                loadMapWithPermission()
            }
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
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mMap.isMyLocationEnabled = true

        if (location.lat <= 0 && location.lon <= 0) {
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
                btn_next.isEnabled = true
            }
        } else {
            val center = CameraUpdateFactory.newLatLng(
                LatLng(
                    location.lat,
                    location.lon
                )
            )
            val zoom = CameraUpdateFactory.zoomTo(18f)
            mMap.moveCamera(center)
            mMap.animateCamera(zoom)
        }

        if (isMain){
            addMarkers()
            btn_next.visibility = View.GONE
            imageView13.visibility = View.GONE

        }

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
        const val REQUEST_CODE = 10008

        fun start(context: Activity, isMain: Boolean = false) {
            val intent = Intent(context, MapsActivity::class.java)
            intent.putExtra("isMain", isMain)
            context.startActivityForResult(intent, REQUEST_CODE)
        }
    }
}

