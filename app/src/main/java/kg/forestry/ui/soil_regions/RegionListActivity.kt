package kg.forestry.ui.soil_regions

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kg.forestry.R
import kg.forestry.ui.core.base.BaseActivity
import kg.core.utils.Constants
import kg.core.utils.LocaleManager
import kg.forestry.localstorage.Preferences
import kg.forestry.localstorage.model.Region
import kotlinx.android.synthetic.main.activity_region_list.*


class RegionListActivity :
    BaseActivity<RegionListViewModel>(R.layout.activity_region_list, RegionListViewModel::class),
    RegionListAdapter.RegionListClickListener {

    private val adapter = RegionListAdapter(listener = this)
    private var isReport = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar.apply {
            title = getString(R.string.pasture_region)
            setNavigationOnClickListener { onBackPressed() }
        }
        isReport = intent.getBooleanExtra(Constants.TO_REPORT, false)
        vm.setProgress(true)
        subscribeToLiveData()

        search_view.visibility = View.GONE
        rv_list.layoutManager = LinearLayoutManager(this)
        rv_list.adapter = adapter
    }

    private fun subscribeToLiveData() {
        vm.fetchRegionsFromDb()
        vm.regionListLiveData.observe(this, Observer {
            val regionsList = mutableListOf<Region?>()
            it?.forEach { data ->
                var name = data.name_ru
                when (LocaleManager.getLanguagePref(this)) {
                    LocaleManager.LANGUAGE_KEY_KYRGYZ -> name = data.name_ky
                    LocaleManager.LANGUAGE_KEy_ENGLISH -> name = data.name_en
                }
                Log.e("TAG", "subscribeToLiveData: $name")
                regionsList.add(data)
            }
            adapter.items = regionsList
        })
        vm.setProgress(false)
    }

    private fun finishActivityWithResultOK(region: Region?) {
        val intent = Intent()
        intent.putExtra(Constants.Region, region)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onBackPressed() {
        finish()
    }

    companion object {
        const val REQUEST_CODE = 100

        fun start(context: Activity, isReport: Boolean = false) {
            val intent = Intent(context, RegionListActivity::class.java)
            intent.putExtra(Constants.TO_REPORT, isReport)
            context.startActivityForResult(intent, REQUEST_CODE)
        }
    }

    override fun onItemClick(regionInfo: Region?) {
        finishActivityWithResultOK(regionInfo)
    }
}
