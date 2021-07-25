package kg.forestry.ui.plant_type

import kg.core.Event
import kg.forestry.localstorage.model.PlantType
import kg.forestry.repos.PlantsRepository
import kg.forestry.ui.core.base.BaseViewModel

class PlatTypeViewModel(private val plantsRepository: PlantsRepository) : BaseViewModel<Event>() {
    fun fetchPlantsFromDb(): List<PlantType> {
        return plantsRepository.fetchPlantCatalogFromDbAsList()
    }
}
