package kg.forestry.ui.soil_villages

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import kg.core.Event
import kg.core.base.BaseViewModel
import kg.forestry.localstorage.model.Village
import kg.forestry.repos.FirebaseQueryLiveData
import kg.forestry.repos.PlantsRepository

class VillageListViewModel(
    private val plantsRepository: PlantsRepository
) : BaseViewModel<Event>() {

    private val liveData = FirebaseQueryLiveData(plantsRepository.getVillages())

    val villagesListLiveData: MutableLiveData<List<Village>> = MutableLiveData()

    fun fetchRegionsFromServer(): LiveData<DataSnapshot?> {
        Log.d("Village",liveData.value.toString())
        return liveData
    }

    @SuppressLint("CheckResult")
    fun fetchVillagesFromDB() {
            plantsRepository.fetchVillagesFromDb()
                .observeOn(plantsRepository.scheduler.ui())
                .subscribe(villagesListLiveData::setValue)

    }


}
