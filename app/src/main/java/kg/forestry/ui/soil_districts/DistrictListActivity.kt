package kg.forestry.ui.soil_districts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import io.grpc.internal.LogExceptionRunnable
import kg.forestry.R
import kg.forestry.ui.core.base.BaseActivity
import kg.core.utils.Constants
import kg.core.utils.LocaleManager
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
                    var name = v?.name_ru!!.toLowerCase()
                    when (LocaleManager.getLanguagePref(this@DistrictListActivity)) {
                        LocaleManager.LANGUAGE_KEY_KYRGYZ -> name = v.name_ky.toLowerCase()
                        LocaleManager.LANGUAGE_KEy_ENGLISH -> name = v.name_ru.toLowerCase()
                    }
                    Log.e("TAG", "onQueryTextChange: $name" )
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
        vm.districtListLiveData.observe(this, Observer { it ->
            val districtsList = mutableListOf<District?>()
            it?.forEach { data ->
                var name = data.name_ru
                when (LocaleManager.getLanguagePref(this)) {
                    LocaleManager.LANGUAGE_KEY_KYRGYZ -> name = data.name_ky
                    LocaleManager.LANGUAGE_KEy_ENGLISH -> name = data.name_en
                }
                Log.e("TAG", "subscribeToLiveData: $name")
                if(data.regionId == regionID){
                districtsList.add(data)}
            }
            adapter.items = districtsList
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
    }


    companion object {
        const val REQUEST_CODE = 104

        fun start(context: Activity, isReport: Boolean = false, regionID: Int) {
            val intent = Intent(context, DistrictListActivity::class.java)
            intent.putExtra(Constants.TO_REPORT, isReport)
            intent.putExtra("regionID", regionID)
            context.startActivityForResult(intent, REQUEST_CODE)
        }
    }

    override fun onItemClick(districtInfo: District?) {
        finishActivityWithResultOK(districtInfo!!)
    }
}
