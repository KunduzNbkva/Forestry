package kg.forestry.ui.main

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import kg.forestry.R
import kg.core.Event
import kg.forestry.ui.core.base.BaseActivity
import kg.core.utils.Harvest
import kg.core.utils.PastureRecord
import kg.core.utils.PlotRecord
import kg.core.utils.visible
import kg.forestry.localstorage.Preferences
import kg.forestry.localstorage.model.*
import kg.forestry.ui.about.AboutAppActivity
import kg.forestry.ui.auth.AuthActivity
import kg.forestry.ui.harvest.HarvestListActivity
import kg.forestry.ui.map.MapsActivity
import kg.forestry.ui.plant.PlantsListActivity
import kg.forestry.ui.reports.report_types.ReportTypesActivity
import kg.forestry.ui.user_profile.EditProfileActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class MainActivity : BaseActivity<MainViewModel>(R.layout.activity_main, MainViewModel::class) {

    private val REQUEST_LOCATION: Int = 1001

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions()

        //vm.setUserPlant("00-00-00")
        //vm.deleteUserPlant()
        vm.setProgress(true)
        vm.fetchUserData()
        subscribeToLiveData()
        setupViews()

        if (Preferences(this).isFirstLoad){
            showMessageForLoadMaps(this)
        }
    }


    override fun onResume() {
        super.onResume()
        vm.saveLocalPlantsHarvests()
        if(!isLocationEnabled(this)){
            buildAlertMessageNoGps(this)
            return
        }
    }

    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun requestPermissions(){

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
        }
    }

    fun buildAlertMessageNoGps(context: Context) {
        val builder = AlertDialog.Builder(context);
        builder.setMessage(getString(R.string.gps_disabled_message))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.enable)) { _, _ -> context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel(); }
        val alert = builder.create();
        alert.show()
    }

    fun showMessageForLoadMaps(context: Context) {
        val builder = AlertDialog.Builder(context);
        builder.setMessage(getString(R.string.podskazka_about_app))
            .setCancelable(false)
            .setPositiveButton(R.string.open) { _, _ -> MapsActivity.start(this, true) }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel(); }
        val alert = builder.create();
        alert.show();
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION) {
            requestPermissions()
        }
    }
    
    private fun subscribeToLiveData() {
        vm.event.observe(this, Observer {
            when (it) {
                is Event.RefreshData -> {
                    vm.plots.observe(this, Observer {
                        val plotCatalog = mutableListOf<PlotRecord>()
                        it?.children?.forEach { data ->
                            val value = data.getValue(PlotRecord::class.java)
                            if (value?.userId == Preferences(this).userToken) {
                                plotCatalog.add(value)
                            }
                        }
                        vm.savePlotsToLocalDb(plotCatalog)
                    })
                    vm.pastures.observe(this, Observer {
                        val pastureCatalog = mutableListOf<PastureRecord>()
                        it?.children?.forEach { data ->
                            val value = data.getValue(PastureRecord::class.java)
                            if (value?.userId == Preferences(this).userToken) {
                                pastureCatalog.add(value)
                            }
                        }
                        vm.savePasturesToLocalDb(pastureCatalog)
                    })
                    vm.harvests.observe(this, Observer {
                        val harvests = mutableListOf<Harvest>()
                        it?.children?.forEach { data ->
                            val value = data.getValue(Harvest::class.java)
                            if (value?.userId == Preferences(this).userToken) {
                                if (value != null) {
                                    harvests.add(value)
                                }
                            }
                        }
                        vm.saveHarvestsToLocalDb(harvests.sortedByDescending { it.date })
                    })

                    vm.plants.observe(this, Observer {list ->
                        val plants = mutableListOf<Plant>()
                        list?.children?.forEach { data ->
                            val value = data.getValue(Plant::class.java)
                            if (value?.userId == Preferences(this).userToken) {
                                plants.add(value)
                                          }
                        }
                       vm.savePlantsToLocalDb(plants.sortedByDescending { it.date })
                    })


                    vm.regions.observe(this, Observer {
                        val regions = mutableListOf<Region>()
                        it?.children?.forEach { data ->
                            val value = data.getValue(Region::class.java)
                            if (value != null) {
                                regions.add(value)
                            }
                        }
                        vm.saveRegionsToLocalDb(regions)
                    })

                    vm.villages.observe(this, Observer {
                        val villages = mutableListOf<Village>()
                        it?.children?.forEach { data ->
                            val value = data.getValue(Village::class.java)
                            if (value != null) {
                                villages.add(value)
                            }
                        }

                        vm.saveVillagesToLocalDb(villages)
                    })

                    vm.districts.observe(this, Observer {
                        val districts = mutableListOf<District>()
                        it?.children?.forEach { data ->
                            val value = data.getValue(District::class.java)
                            if (value != null) {
                                districts.add(value)
                            }
                        }
                        vm.saveDisctricsToLocalDb(districts)
                    })
                }
            }
        })

        vm.trees?.observe(this, Observer {
            val treesCatalog = mutableListOf<TreeType>()
            it?.children?.forEach { data ->
                val value = data.getValue(TreeType::class.java)
                treesCatalog.add(value!!)
            }
            vm.saveTreesToDB(treesCatalog)
        })
        vm.test()
    }

    private fun setupViews() {
        if (Preferences(this).isUserAuthorized()) {
            mmv_profile.visible()
        }
        btn_exit.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            vm.removeUserToken()
            AuthActivity.start(this)
        }
        mmv_profile.setOnClickListener { EditProfileActivity.start(this) }
        mmv_harvest.setOnClickListener { HarvestListActivity.start(this) }
        mmv_plant.setOnClickListener { PlantsListActivity.start(this) }
        mmv_about.setOnClickListener { AboutAppActivity.start(this) }
        mmv_reports.setOnClickListener { ReportTypesActivity.start(this) }
        mmv_map.setOnClickListener { MapsActivity.start(this, true) }
        mmv_instruction.setOnClickListener { readFromAssets() }
    }

    private fun readFromAssets() {
        val assetManager: AssetManager = this.assets
        var `in`: InputStream? = null
        var `out`: OutputStream? = null
        val fileName = getString(R.string.pdf_instruction)
        val file = File(this.filesDir, fileName)
        try {
            `in` = assetManager.open("instructions/$fileName")
            out = this.openFileOutput(file.name, Context.MODE_PRIVATE)
            copyFile(`in`, out!!)
            `in`.close()
            `in` = null
            out.flush()
            out.close()
            out = null
        } catch (e: Exception) {
            Log.e("tag", e.message)
        }
        val intent = Intent(Intent.ACTION_VIEW)
        val contentUri =
            FileProvider.getUriForFile(this, "kg.forestry.fileprovider", file)
        val mimeType = this.contentResolver.getType(contentUri)

        intent.setDataAndType(contentUri, mimeType)
        intent.flags =
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.d("error", e.message)
            Toast.makeText(
                this,
                getString(R.string.load_pdf_reader),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @Throws(IOException::class)
    private fun copyFile(`in`: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int
        while (`in`.read(buffer).also { read = it } != -1) {
            out.write(buffer, 0, read)
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

}
