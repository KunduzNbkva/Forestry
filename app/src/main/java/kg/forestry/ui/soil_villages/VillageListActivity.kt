package kg.forestry.ui.soil_villages

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kg.forestry.R
import kg.core.base.BaseActivity
import kg.core.utils.Constants
import kg.forestry.localstorage.model.Village
import kotlinx.android.synthetic.main.activity_region_list.*


class VillageListActivity :
    BaseActivity<VillageListViewModel>(R.layout.activity_region_list, VillageListViewModel::class),
    VillageListAdapter.RegionListClickListener {

    private val adapter = VillageListAdapter(listener = this)
    private var isReport = false
    private var districtID: Int = 0

    var items: MutableList<Village?> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar.apply {
            title = getString(R.string.pasture_village)
            setNavigationOnClickListener { onBackPressed() }
        }
        isReport = intent.getBooleanExtra(Constants.TO_REPORT, false)
        districtID = intent.getIntExtra("districtID", 0)
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
                val filteredItems: MutableList<Village?> = mutableListOf()

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
        vm.fetchVillagesFromDB()
        vm.villagesListLiveData.observe(this, Observer {
            adapter.items = it.filter{it.districtId == districtID}.toMutableList()
            items = it.toMutableList()
        })
        vm.setProgress(false)
    }

    private fun finishActivityWithResultOK(region: String?) {
        val intent = Intent()
        intent.putExtra(Constants.Village, region)
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
        const val REQUEST_CODE = 103

        fun start(context: Activity, districtID: Int, isReport: Boolean = false) {
            val intent = Intent(context, VillageListActivity::class.java)
            intent.putExtra(Constants.TO_REPORT, isReport)
            intent.putExtra("districtID", districtID)
            context.startActivityForResult(intent, REQUEST_CODE)
        }
    }

    override fun onItemClick(regionInfo: Village?) {
        finishActivityWithResultOK(regionInfo!!.name)
    }
}
