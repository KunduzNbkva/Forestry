package kg.forestry.ui.plots

import androidx.lifecycle.MutableLiveData
import kg.core.Event
import kg.forestry.ui.core.base.BaseViewModel
import kg.forestry.repos.HarvestRepository

class PlotListViewModel (val harvestRepository: HarvestRepository
): BaseViewModel<Event>() {

    val plotsListLiveData: MutableLiveData<List<String>> = MutableLiveData()

    fun fetchPlotsFromDb() {
        harvestRepository.fetchPlotsFromDb()
            .map { it ->
                val listOfTitles = mutableListOf<String>()
                it.forEach { listOfTitles.add(it.title) }
                return@map listOfTitles
            }
            .observeOn(harvestRepository.scheduler.ui())
            .subscribe(plotsListLiveData::setValue)
    }


}