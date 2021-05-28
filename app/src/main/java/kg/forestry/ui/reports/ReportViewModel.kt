package kg.forestry.ui.reports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import kg.core.Event
import kg.core.base.BaseViewModel
import kg.forestry.localstorage.model.Plant
import kg.forestry.repos.FirebaseQueryLiveData
import kg.forestry.repos.PlantsRepository

class ReportViewModel(val plantsRepository: PlantsRepository) : BaseViewModel<Event>(){

    private val liveData = FirebaseQueryLiveData(plantsRepository.getUserPlantsListFromServer())

    val plantInfo: MutableLiveData<Plant> = MutableLiveData()

    fun fetchPlantInfo(){

    }

    fun fetchPlantsFromServer(): LiveData<DataSnapshot?> {
        return liveData
    }

    fun fetchPlantsFromDb(date:String) {
        plantsRepository.fetchPlantByDate(date)
            .observeOn(plantsRepository.scheduler.ui())
            .subscribe(plantInfo::setValue)

    }

}