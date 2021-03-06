package kg.forestry.ui.plant

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kg.forestry.ui.core.base.BaseActivity
import kg.core.utils.Constants
import kg.forestry.R
import kg.forestry.localstorage.Preferences
import kg.forestry.localstorage.model.Plant
import kg.forestry.ui.extensions.toDate
import kg.forestry.ui.plant.plant_info.AddPlantActivity
import kg.forestry.ui.reports.ReportActivity
import kotlinx.android.synthetic.main.activity_harvest_list.fab_add
import kotlinx.android.synthetic.main.activity_harvest_list.null_view
import kotlinx.android.synthetic.main.activity_harvest_list.rv_list
import kotlinx.android.synthetic.main.activity_harvest_list.toolbar
import java.lang.Exception


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
    }


    private fun visible(){
        null_view.visibility = View.VISIBLE
        rv_list.visibility = View.GONE
    }

    private fun invisible(){
        null_view.visibility = View.GONE
        rv_list.visibility = View.VISIBLE
    }
    private fun subscribeToLiveData() {
        vm.fetchPlantsFromDb()
        vm.plantListLiveData.observe(this, Observer { list ->
            if (list.isNullOrEmpty()){
                visible()
            } else {
                adapter.items = list.sortedByDescending { it.date.toDate() }.toMutableList()
                invisible()
            }
        })
        vm.setProgress(false)
    }

    override fun onBackPressed() {
        if (vm.isNetworkConnected && Preferences(this).isUserAuthorized()) {
            Toast.makeText(this, getString(R.string.synchronization), Toast.LENGTH_LONG).show()
        }
        finish()
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
        grantResults: IntArray) {
        if (requestCode == REQUEST_LOCATION) {
            requestPermissions()
        }
    }


    override fun onItemClick(plantInfo: Plant?) {
        if (isReport) {
            ReportActivity.start(this, plantInfo)
        } else {
            try {
                AddPlantActivity.start(this, plantInfo)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    companion object {
        fun start(context: Context, isReport: Boolean = false) {
            val intent = Intent(context, PlantsListActivity::class.java)
            intent.putExtra(Constants.TO_REPORT, isReport)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
        }
    }
}
