package kg.forestry.ui.tree_type

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kg.forestry.R
import kg.forestry.ui.core.base.BaseActivity
import kg.core.utils.Constants
import kg.core.utils.LocaleManager
import kg.forestry.localstorage.Preferences
import kg.forestry.ui.adapter.TreeTypeAdapter
import kotlinx.android.synthetic.main.activity_tree_type.button
import kotlinx.android.synthetic.main.activity_tree_type.rv_list
import kotlinx.android.synthetic.main.activity_tree_type.search_view
import kotlinx.android.synthetic.main.activity_tree_type.toolbar

class TreeTypeActivity:
    BaseActivity<TreeTypeViewModel>(R.layout.activity_tree_type, TreeTypeViewModel::class),
    TreeTypeAdapter.SimpleListAdapterListener {

    private lateinit var adapter: TreeTypeAdapter
    private val multiValues: MutableList<String> by lazy { mutableListOf<String>() }
    var items: MutableList<Pair<String, Boolean>> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar.apply {
            title = context.getString(R.string.trees)
            setNavigationOnClickListener { onBackPressed() }
        }
        vm.fetchTreesFromDb()
        rv_list.layoutManager = LinearLayoutManager(this)
        adapter = TreeTypeAdapter(true, this)
        rv_list.adapter = adapter
        subscribeToLiveData()
        button.setOnClickListener {
            val treeType = multiValues.joinToString()
            finishActivityWithResultOK(treeType)
        }
        setupSearchView()
    }

    private fun subscribeToLiveData() {
        val types = mutableListOf<Pair<String, Boolean>>()
        val keyValue  = intent.getStringExtra(Constants.TREES_CATALOG)?:""

        if (Preferences(this).userToken.isNotEmpty()) {
            vm.treesCatalogLiveData.observe(this, Observer {
                val userPlantsList = mutableListOf<String>()
                it?.forEach { data ->
                    var name = data.name_ru
                    when (LocaleManager.getLanguagePref(this)){
                        LocaleManager.LANGUAGE_KEY_KYRGYZ -> name = data.name_ky
                        LocaleManager.LANGUAGE_KEy_ENGLISH -> name = data.name_en
                    }
                    userPlantsList.add(name)
                }

                types.addAll(userPlantsList
                    .map {
                        val isSelected = keyValue.contains(it)
                        if (isSelected && !multiValues.contains(it)) multiValues.add(it)
                        Pair(it, isSelected)
                    }
                    .toMutableList())
                items = types
                adapter.items = types
                vm.setProgress(false)
            })
        }
    }

    private fun setupSearchView(){
        search_view.isIconifiedByDefault = false
        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                val queryText = newText.toLowerCase()
                val filteredItems: MutableList<Pair<String, Boolean>> = mutableListOf()

                for (v in items) {
                    val name = v.first.toLowerCase()
                    if (name.contains(queryText)) {
                        filteredItems.add(v)
                    }
                }

                adapter.filterList(filteredItems)

                return true
            }
        })
    }

    override fun onItemClick(title: String, position: Int) {
        val isAdded = if (multiValues.contains(title)) {
            multiValues.removeAll { it.contentEquals(title) }
            false
        } else {
            multiValues.add(title)
            true
        }
        adapter.apply {
            items[position] = Pair(title, isAdded)
            notifyDataSetChanged()
        }
    }

    private fun finishActivityWithResultOK(soilColor: String?) {
        val intent = Intent()
        intent.putExtra(Constants.TREES_CATALOG, soilColor)
        setResult(RESULT_OK, intent)
        finish()
    }

    companion object {
        const val REQUEST_CODE = 15029

        fun start(context: Activity,chosenTrees: String) {
            val intent = Intent(context, TreeTypeActivity::class.java)
            intent.putExtra(Constants.TREES_CATALOG,chosenTrees)
            context.startActivityForResult(intent, REQUEST_CODE)
        }
    }


}
