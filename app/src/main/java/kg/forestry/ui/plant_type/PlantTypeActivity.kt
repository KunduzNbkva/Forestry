package kg.forestry.ui.plant_type

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import kg.forestry.R
import kg.forestry.ui.core.base.BaseActivity
import kg.core.utils.Constants
import kg.core.utils.LocaleManager
import kg.core.utils.gone
import kg.core.utils.visible
import kg.forestry.localstorage.Preferences
import kg.forestry.localstorage.model.ListType
import kg.forestry.localstorage.model.PlantType
import kg.forestry.ui.adapter.PlantCatalogAdapter
import kg.forestry.ui.records.NewRecordActivity
import kotlinx.android.synthetic.main.activity_plant_type.*
import kotlinx.android.synthetic.main.activity_plant_type.rv_list
import kotlinx.android.synthetic.main.activity_plant_type.search_view
import kotlinx.android.synthetic.main.activity_plant_type.toolbar
import kotlinx.android.synthetic.main.activity_region_list.*
import java.util.*

class PlantTypeActivity :
    BaseActivity<PlatTypeViewModel>(R.layout.activity_plant_type, PlatTypeViewModel::class),
    PlantCatalogAdapter.SimpleListAdapterListener {

    private lateinit var adapter: PlantCatalogAdapter
    private lateinit var imgAdapter: SimpleImageGridAdapter
    private val multiValues: MutableList<PlantType> by lazy { mutableListOf<PlantType>() }
    private val selectedPlantNames: MutableList<String> by lazy { mutableListOf<String>() }

    var items: MutableList<Pair<PlantType, Boolean>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar.apply {
            title = context.getString(R.string.plants)
            setNavigationOnClickListener { onBackPressed() }
        }
        vm.fetchPlantsFromDb()
        button.setOnClickListener {
            multiValues.forEach {
                selectedPlantNames.add(it.name_ru)
            }
            finishActivityWithResultOK(selectedPlantNames.joinToString())
        }
        fab_add.setOnClickListener {
            NewRecordActivity.start(this, ListType.PLANT.value)
        }
        vm.setProgress(true)
        tabLayout.addOnTabSelectedListener(object :
            TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
                fab_add.hide()
                search_view.visibility = View.GONE
                rv_list.gone(); rv_image_list.apply {
                    adapter?.notifyDataSetChanged()
                    visible()
                }
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                when (p0?.position) {
                    0 -> {
                        fab_add.hide()
                        search_view.visibility = View.GONE
                        rv_list.gone(); rv_image_list.apply {
                            adapter?.notifyDataSetChanged()
                            visible()
                        }
                    }
                    1 -> {
                        fab_add.show()
                        search_view.visibility = View.VISIBLE
                        rv_list.apply {
                            adapter?.notifyDataSetChanged()
                            visible()
                        }; rv_image_list.gone()
                    }
                }
            }
        })

        rv_list.layoutManager = LinearLayoutManager(this)
        rv_image_list.layoutManager = GridLayoutManager(this, 2)
        updateViews()
        subscribeToLiveData()
        setupSearchView()

    }

    private fun updateViews() {
        adapter = PlantCatalogAdapter(true, this)
        imgAdapter = SimpleImageGridAdapter(true, this)
        rv_list.adapter = adapter
        rv_image_list.adapter = imgAdapter
    }

    private fun subscribeToLiveData() {
        val types = mutableListOf<Pair<PlantType, Boolean>>()
        val keyValue  = intent.getStringExtra(Constants.PLANT_CATALOG)?:""

        if (Preferences(this).userToken.isNotEmpty()) {
            val userPlantsList = mutableListOf<PlantType>()
            vm.fetchPlantsFromDb().forEach { plantType ->
                userPlantsList.add(plantType)
            }
            sortListPairDesc(userPlantsList)
            types.addAll(userPlantsList
                .map {

                    var name = it.name_ru
                    when (LocaleManager.getLanguagePref(this)){
                        LocaleManager.LANGUAGE_KEY_KYRGYZ -> name = it.name_ky
                        LocaleManager.LANGUAGE_KEy_ENGLISH -> name = it.name_en
                    }

                    val isSelected = keyValue.contains(name)
                    if (isSelected && !selectedPlantNames.contains(name)) selectedPlantNames.add(name)
                    Pair(it, isSelected)
                }
                .toMutableList())

            adapter.items = types
            imgAdapter.items = types
            items = types
            vm.setProgress(false)
        }
    }

    private fun sortListPairDesc(list: MutableList<PlantType>): MutableList<PlantType> {
        when(LocaleManager.getLanguagePref(this)){
            LocaleManager.LANGUAGE_KEY_KYRGYZ ->  list.sortBy { it.name_ky }
            LocaleManager.LANGUAGE_KEy_ENGLISH -> list.sortBy { it.name_en }
            LocaleManager.LANGUAGE_KEY_RUSSIAN -> list.sortBy { it.name_ru }
        }
        return list
    }

    private fun setupSearchView(){
        search_view.isIconifiedByDefault = false
        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                val queryText = newText.toLowerCase()
                val filteredItems: MutableList<Pair<PlantType, Boolean>> = mutableListOf()

                for (v in items) {
                    var name =  v.first.name_ru
                    when (LocaleManager.getLanguagePref(this@PlantTypeActivity)){
                        LocaleManager.LANGUAGE_KEY_KYRGYZ -> name =  v.first.name_ky
                        LocaleManager.LANGUAGE_KEy_ENGLISH -> name =  v.first.name_en
                    }
                    if (name.toLowerCase().contains(queryText)) {
                        filteredItems.add(v)
                    }
                }
                adapter.filterList(filteredItems)
                return true
            }
        })
    }

    private fun finishActivityWithResultOK(listOfPlants: String) {
        val intent = Intent()
        intent.putExtra(Constants.PLANT_CATALOG, listOfPlants)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onItemClick(title: PlantType, position: Int) {
        var name =  title.name_ru
        when (LocaleManager.getLanguagePref(this@PlantTypeActivity)){
            LocaleManager.LANGUAGE_KEY_KYRGYZ -> name = title.name_ky
            LocaleManager.LANGUAGE_KEy_ENGLISH -> name = title.name_en
        }

        val isAdded = if (selectedPlantNames.contains(name)) {
            selectedPlantNames.removeAll { it.contentEquals(name) }
            false
        } else {
            selectedPlantNames.add(name)
            true
        }
        adapter.apply {
            items[position] = Pair(title, isAdded)
            notifyDataSetChanged()
        }
        imgAdapter.apply {
            items[position] = Pair(title, isAdded)
            notifyDataSetChanged()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                NewRecordActivity.REQUEST_CODE -> {
                    subscribeToLiveData()
                }
            }
        }
    }

    companion object {
        const val REQUEST_CODE = 10569

        fun start(context: Activity,chosenPlants: String) {
            val intent = Intent(context, PlantTypeActivity::class.java)
            intent.putExtra(Constants.PLANT_CATALOG,chosenPlants)
            context.startActivityForResult(intent, REQUEST_CODE)
        }
    }
}
