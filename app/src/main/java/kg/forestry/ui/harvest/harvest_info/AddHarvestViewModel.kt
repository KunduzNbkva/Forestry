package kg.forestry.ui.harvest.harvest_info

import kg.core.Event
import kg.forestry.ui.core.base.BaseViewModel
import kg.core.utils.Biomass
import kg.core.utils.Helper.getRandomString
import kg.forestry.localstorage.Preferences
import kg.core.utils.Harvest
import kg.forestry.repos.HarvestRepository

class AddHarvestViewModel(private val harvestRepository: HarvestRepository, private val prefs: Preferences) : BaseViewModel<Event>() {
    var harvestInfo: Harvest? = null
    var photoPath = ""
    var wetBiomassValue = Biomass(0,0)
    var dryBiomassValue = Biomass(0,0)

    fun isEditMode() = harvestInfo != null

    fun getUserId() = harvestRepository.getUserToken()

    fun saveHarvest(harvest: Harvest) {
        if (isEditMode()){
            setProgress(true)
            harvest.isInServer = false
            harvestRepository.updateUserHarvestInDB(harvest)
        } else {
            harvest.id = getRandomString(10)
            harvestRepository.updateUserHarvestInDB(harvest)
        }
    }

    fun removeHarvest(harvest: Harvest) {
        setProgress(true)
        if (prefs.isUserAuthorized()) {
            harvestRepository.removeHarvestFromServer(harvest.id).addOnCompleteListener {
                setProgress(false)
            }
            harvestRepository.removeHarvestFromDB(harvest)
        }else{
            harvestRepository.removeHarvestFromDB(harvest)
        }
    }
}