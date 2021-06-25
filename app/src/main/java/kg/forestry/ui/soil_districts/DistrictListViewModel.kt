package kg.forestry.ui.soil_districts

import androidx.lifecycle.MutableLiveData
import kg.core.Event
import kg.forestry.ui.core.base.BaseViewModel
import kg.forestry.localstorage.model.District
import kg.forestry.repos.FirebaseQueryLiveData
import kg.forestry.repos.PlantsRepository

class DistrictListViewModel(
    private val plantsRepository: PlantsRepository
) : BaseViewModel<Event>() {

    private val liveData = FirebaseQueryLiveData(plantsRepository.getDistricts())

    val districtListLiveData: MutableLiveData<List<District>> = MutableLiveData()

//    fun fetchRegionsFromServer(): LiveData<DataSnapshot?> {
//        Log.d("Village",liveData.value.toString())
//        return liveData
//    }

    fun fetchDistrictsFromDB() {
            plantsRepository.fetchDistrictsFromDb()
                .observeOn(plantsRepository.scheduler.ui())
                .subscribe(districtListLiveData::setValue)

    }


}
