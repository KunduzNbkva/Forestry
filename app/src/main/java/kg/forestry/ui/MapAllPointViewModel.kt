package kg.forestry.ui

import kg.core.Event
import kg.core.base.BaseViewModel
import kg.core.utils.Location
import kg.forestry.repos.HarvestRepository
import kg.forestry.repos.PlantsRepository

class MapAllPointViewModel(private val harvestRepository: HarvestRepository, private val plantsRepository: PlantsRepository):
    BaseViewModel<Event>() {

    fun getAllLocations():List<Location>{
        val locationList = mutableListOf<Location>()
        harvestRepository.fetchHarvestsFromDbAsList().forEach { locationList.add(it.harvLocation) }
        plantsRepository.fetchPlantsFromDbAsList().forEach { locationList.add(it.plantLocation) }
        return locationList
    }
}
