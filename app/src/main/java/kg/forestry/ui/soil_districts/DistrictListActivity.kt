package kg.forestry.ui.soil_districts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kg.forestry.R
import kg.core.base.BaseActivity
import kg.core.utils.Constants
import kg.forestry.localstorage.model.District
import kotlinx.android.synthetic.main.activity_region_list.*


class DistrictListActivity :
    BaseActivity<DistrictListViewModel>(R.layout.activity_region_list, DistrictListViewModel::class),
    DistrictListAdapter.RegionListClickListener {

    private val adapter = DistrictListAdapter(listener = this)
    private var isReport = false

    private var regionID: Int = 0

    var items: MutableList<District?> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar.apply {
            title = getString(R.string.pasture_district)
            setNavigationOnClickListener { onBackPressed() }
        }
        isReport = intent.getBooleanExtra(Constants.TO_REPORT, false)
        regionID = intent.getIntExtra("regionID", 0)
        vm.setProgress(true)
        subscribeToLiveData()

        rv_list.layoutManager = LinearLayoutManager(this)
        rv_list.adapter = adapter

        setupSearchView()
    }

    private fun setupSearchView(){
        search_view.isIconifiedByDefault = false
        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                val queryText = newText.toLowerCase()
                val filteredItems: MutableList<District?> = mutableListOf()

                for (v in items) {
                    val name = v?.name!!.toLowerCase()
                    if (name.contains(queryText)) {
                        filteredItems.add(v)
                    }
                }

                adapter.filterList(filteredItems)

                return true
            }
        })
    }

    private fun subscribeToLiveData() {
        vm.fetchDistrictsFromDB()
        vm.districtListLiveData.observe(this, Observer {
            adapter.items = it.filter { it.regionId == regionID }.toMutableList()
            items = it.toMutableList()
        })
        vm.setProgress(false)
    }

    private fun finishActivityWithResultOK(region: District) {
        val intent = Intent()
        intent.putExtra(Constants.DISTRICTS, region)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onBackPressed() {
        finish()
//        MainActivity.start(this)
////        if (vm.isNetworkConnected && Preferences(this).isUserAuthorized()) {
////            Toast.makeText(this, "Синхронизация данных", Toast.LENGTH_SHORT).show()
////        }
    }


//    override fun onItemClick(regionInfo: Region?) {
//        if (isReport) {
//            ReportActivity.start(this, plantInfo)
//        } else {
//            AddPlantActivity.start(this, plantInfo)
//        }
//    }

    companion object {
        const val REQUEST_CODE = 104

        fun start(context: Activity, isReport: Boolean = false, regionID: Int) {
            val intent = Intent(context, DistrictListActivity::class.java)
            intent.putExtra(Constants.TO_REPORT, isReport)
            intent.putExtra("regionID", regionID)
            context.startActivityForResult(intent, REQUEST_CODE)
        }
    }

    override fun onItemClick(regionInfo: District?) {
        finishActivityWithResultOK(regionInfo!!)
    }
}
