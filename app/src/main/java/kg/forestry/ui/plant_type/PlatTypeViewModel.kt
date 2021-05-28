package kg.forestry.ui.plant_type

import kg.core.Event
import kg.core.base.BaseViewModel
import kg.forestry.localstorage.model.PlantType
import kg.forestry.repos.PlantsRepository

class PlatTypeViewModel(
    private val plantsRepository: PlantsRepository
): BaseViewModel<Event>() {


    fun fetchPlantsFromDb(): List<PlantType> {
         return plantsRepository.fetchPlantCatalogFromDbAsList()
    }

}
