package kg.forestry.ui.plots

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kg.forestry.R
import kg.core.base.BaseActivity
import kg.core.utils.Constants
import kg.forestry.localstorage.model.ListType
import kg.forestry.ui.adapter.SimpleListAdapter
import kg.forestry.ui.records.NewRecordActivity
import kotlinx.android.synthetic.main.activity_plot_list.*

class PlotListActivity :
    BaseActivity<PlotListViewModel>(R.layout.activity_plot_list, PlotListViewModel::class),
    SimpleListAdapter.SimpleListAdapterListener {

    private val adapter = SimpleListAdapter(false, listener = this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeToLiveData()
        toolbar.apply {
            title = getString(R.string.plot_name)
            setNavigationOnClickListener { onBackPressed() }
        }
        fab_add.setOnClickListener { NewRecordActivity.start(this, ListType.PLOT.value) }
        rv_list.layoutManager = LinearLayoutManager(this)
        rv_list.adapter = adapter
        checkForNull()
    }

    private fun checkForNull(){
        if(adapter.items.isEmpty()){
            visible()
        } else {
            invisible()
        }
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
        vm.fetchPlotsFromDb()
        vm.plotsListLiveData.observe(this, Observer {
            if (it.isEmpty()){
                visible()
            } else{
                adapter.items = it.toMutableList()
                invisible()
            }
        })
    }



    override fun onItemClick(title: String, position: Int) {
        finishActivityWithResultOK(title, ListType.PLOT)
    }

    fun finishActivityWithResultOK(
        item: String,
        type: ListType
    ) {
        val intent = Intent()
        intent.putExtra(Constants.LIST_ITEM_TYPE, type)
        intent.putExtra(Constants.LIST_ITEM, item)
        setResult(RESULT_OK, intent)
        finish()
    }

    companion object {
        const val REQUEST_CODE = 10002

        fun start(context: Activity) {
            val intent = Intent(context, PlotListActivity::class.java)
            context.startActivityForResult(intent, REQUEST_CODE)

        }
    }

}
