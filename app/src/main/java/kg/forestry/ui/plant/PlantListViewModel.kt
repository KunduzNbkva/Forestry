package kg.forestry.ui.plant

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import kg.core.Event
import kg.core.base.BaseViewModel
import kg.forestry.localstorage.model.Plant
import kg.forestry.repos.FirebaseQueryLiveData
import kg.forestry.repos.PlantsRepository

class PlantListViewModel(
    private val plantsRepository: PlantsRepository
) : BaseViewModel<Event>() {

    private val liveData = FirebaseQueryLiveData(plantsRepository.getUserPlantsListFromServer())

    val plantListLiveData: MutableLiveData<List<Plant>> = MutableLiveData()

    fun fetchPlantsFromServer(): LiveData<DataSnapshot?> {
        Log.d("ISRAIL",liveData.value.toString())
        return liveData
    }

    @SuppressLint("CheckResult")
    fun fetchPlantsFromDb() {
            plantsRepository.fetchPlantsFromDb()
                .observeOn(plantsRepository.scheduler.ui())
                .subscribe(plantListLiveData::setValue)

    }


}
