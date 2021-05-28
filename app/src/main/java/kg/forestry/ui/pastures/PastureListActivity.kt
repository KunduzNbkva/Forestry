package kg.forestry.ui.pastures

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kg.forestry.R
import kg.core.base.BaseActivity
import kg.core.utils.Constants
import kg.forestry.localstorage.model.ListType
import kg.forestry.ui.adapter.SimpleListAdapter
import kg.forestry.ui.records.NewRecordActivity
import kotlinx.android.synthetic.main.activity_plot_list.*

class PastureListActivity :
    BaseActivity<PastureListViewModel>(R.layout.activity_plot_list, PastureListViewModel::class),
    SimpleListAdapter.SimpleListAdapterListener {

    private val adapter = SimpleListAdapter(false, listener = this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeToLiveData()
        toolbar.apply {
            title = getString(R.string.pasture_name)
            setNavigationOnClickListener { onBackPressed() }
        }
        fab_add.setOnClickListener {
            NewRecordActivity.start(
                this,
                ListType.PASTURE.value
            )
        }
        rv_list.layoutManager = LinearLayoutManager(this)
        rv_list.adapter = adapter

    }

    private fun subscribeToLiveData() {
        vm.fetchPasturesFromDb()
        vm.plotsListLiveData.observe(this, Observer {
            adapter.items = it.toMutableList()
        })
    }

    override fun onItemClick(title: String, position: Int) {
        finishActivityWithResultOK(title, ListType.PASTURE)
    }

    fun finishActivityWithResultOK(item: String, type: ListType) {
        val intent = Intent()
        intent.putExtra(Constants.LIST_ITEM_TYPE, type)
        intent.putExtra(Constants.LIST_ITEM, item)
        setResult(RESULT_OK, intent)
        finish()
    }

    companion object {
        const val REQUEST_CODE = 10001

        fun start(context: Activity) {
            val intent = Intent(context, PastureListActivity::class.java)
            context.startActivityForResult(intent, REQUEST_CODE)

        }
    }

}
