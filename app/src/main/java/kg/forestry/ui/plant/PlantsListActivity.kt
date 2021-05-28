package kg.forestry.ui.plant

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kg.core.base.BaseActivity
import kg.core.utils.Constants
import kg.forestry.R
import kg.forestry.localstorage.Preferences
import kg.forestry.localstorage.model.Plant
import kg.forestry.ui.extensions.toDate
import kg.forestry.ui.plant.plant_info.AddPlantActivity
import kg.forestry.ui.reports.ReportActivity
import kotlinx.android.synthetic.main.activity_harvest_list.*


class PlantsListActivity :
    BaseActivity<PlantListViewModel>(R.layout.activity_harvest_list, PlantListViewModel::class),
    PlantListAdapter.PlantListClickListener {
    private val REQUEST_LOCATION: Int = 1001
    private val adapter = PlantListAdapter(listener = this)
    private var isReport = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar.apply {
            title = getString(R.string.plant_label)
            setNavigationOnClickListener { onBackPressed() }
        }
        isReport = intent.getBooleanExtra(Constants.TO_REPORT, false)
        requestPermissions()
        vm.setProgress(true)
        subscribeToLiveData()
        if (isReport) {
            fab_add.hide()
        }
        fab_add.setOnClickListener { AddPlantActivity.start(this) }
        rv_list.layoutManager = LinearLayoutManager(this)
        rv_list.adapter = adapter
        if (adapter.items != null) {
            null_view.visibility = View.VISIBLE
            rv_list.visibility = View.GONE
        } else {
            null_view.visibility = View.GONE
            rv_list.visibility = View.VISIBLE
        }
    }

    private fun subscribeToLiveData() {
        vm.fetchPlantsFromDb()
        vm.plantListLiveData.observe(this, Observer { list ->
//            val items = it.toMutableList()
            adapter.items = list.sortedByDescending { it.date.toDate() }.toMutableList()
        })
        vm.setProgress(false)
    }

    override fun onBackPressed() {
        if (vm.isNetworkConnected && Preferences(this).isUserAuthorized()) {
            Toast.makeText(this, getString(R.string.synchronization), Toast.LENGTH_LONG).show()
        }
        finish()
        // MainActivity.start(this)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun requestPermissions() {
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


    override fun onItemClick(plantInfo: Plant?) {
        if (isReport) {
            ReportActivity.start(this, plantInfo)
        } else {
            AddPlantActivity.start(this, plantInfo)
        }
    }

    companion object {
        fun start(context: Context, isReport: Boolean = false) {
            val intent = Intent(context, PlantsListActivity::class.java)
            intent.putExtra(Constants.TO_REPORT, isReport)
            context.startActivity(intent)
        }
    }
}
