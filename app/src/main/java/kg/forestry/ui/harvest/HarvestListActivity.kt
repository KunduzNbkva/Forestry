package kg.forestry.ui.harvest

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kg.core.base.BaseActivity
import kg.core.utils.Constants
import kg.core.utils.Harvest
import kg.forestry.R
import kg.forestry.localstorage.Preferences
import kg.forestry.ui.extensions.toDate
import kg.forestry.ui.harvest.harvest_info.AddHarvestActivity
import kg.forestry.ui.reports.harvest_report.HarvestReportActivity
import kotlinx.android.synthetic.main.activity_harvest_list.*

class HarvestListActivity : BaseActivity<HarvestListViewModel>(R.layout.activity_harvest_list, HarvestListViewModel::class), HarvestListAdapter.HarvestListClickListener {

    private val adapter = HarvestListAdapter(listener = this)
    private val REQUEST_LOCATION: Int = 1001
    private var isReport = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar.apply {
            title = getString(R.string.harvest)
            setNavigationOnClickListener { onBackPressed() }
        }
        isReport = intent.getBooleanExtra(Constants.TO_REPORT, false)
        if (isReport) {
            fab_add.hide()
        }
        requestPermissions()
        subscribeToLiveData()
        fab_add.setOnClickListener { AddHarvestActivity.start(this) }
        rv_list.layoutManager = LinearLayoutManager(this)
        rv_list.adapter = adapter
    }

    private fun subscribeToLiveData() {
        vm.fetchHarvestsFromDb()
        vm.harvestListLiveData.observe(this, Observer { list ->
            adapter.items = list.sortedByDescending { it.date.toDate() }.toMutableList()
        })
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

    override fun onItemClick(harvestInfo: Harvest?) {
        if (isReport) {
            HarvestReportActivity.start(this, harvestInfo)
        } else {
            AddHarvestActivity.start(this, harvestInfo)
        }
    }

    override fun onBackPressed() {
        if (vm.isNetworkConnected && Preferences(this).isUserAuthorized()) {
            Toast.makeText(this, getString(R.string.synchronization), Toast.LENGTH_LONG).show()
        }
        finish()
        //MainActivity.start(this)
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 22
        fun start(context: Context, isReport: Boolean? = null) {
            val intent = Intent(context, HarvestListActivity::class.java)
            intent.putExtra(Constants.TO_REPORT, isReport)
            context.startActivity(intent)
        }
    }

}