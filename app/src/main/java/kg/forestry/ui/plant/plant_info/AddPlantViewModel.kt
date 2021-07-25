package kg.forestry.ui.plant.plant_info

import kg.core.Event
import kg.core.utils.Erosion
import kg.core.utils.Helper
import kg.forestry.localstorage.Preferences
import kg.forestry.localstorage.model.Plant
import kg.forestry.localstorage.model.SoilTexture
import kg.forestry.repos.PlantsRepository
import kg.forestry.ui.core.base.BaseViewModel

class AddPlantViewModel(
    private val plantsRepository: PlantsRepository,
    private val prefs: Preferences
) : BaseViewModel<Event>() {

    var plantInfo: Plant? = null
    var soilTexture = SoilTexture()
    var soilErossion = Erosion()
    var photoPath = ""

    fun isEditMode() = plantInfo != null

    fun getUserId() = plantsRepository.getUserToken()

    fun removePlant(plantInfo: Plant) {
        setProgress(true)
        if (prefs.isUserAuthorized()) {
            plantsRepository.removePlantFromServer(plantInfo.id).addOnCompleteListener {
                plantsRepository.removePlantFromDB(plantInfo)
                setProgress(false)
            }
        } else {
            plantsRepository.removePlantFromDB(plantInfo)
        }
    }


    fun saveDraftPlant() {
        plantInfo!!.id = Helper.getRandomString(10)
        plantsRepository.updateUserPlantInDB(plantInfo)
    }


}
