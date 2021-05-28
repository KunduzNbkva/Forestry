package kg.forestry.ui.tree_type

import androidx.lifecycle.MutableLiveData
import kg.core.Event
import kg.core.base.BaseViewModel
import kg.forestry.localstorage.model.TreeType
import kg.forestry.repos.PlantsRepository

class TreeTypeViewModel(
        private val plantsRepository: PlantsRepository
    ): BaseViewModel<Event>() {

        val treesCatalogLiveData: MutableLiveData<List<TreeType>> = MutableLiveData()

        fun fetchTreesFromDb() {
            plantsRepository.fetchTreeCatalogFromDb()
                .observeOn(plantsRepository.scheduler.ui())
                .subscribe(treesCatalogLiveData::setValue)
        }

    }
