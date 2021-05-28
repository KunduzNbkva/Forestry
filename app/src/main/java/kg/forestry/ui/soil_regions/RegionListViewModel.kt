package kg.forestry.ui.soil_regions

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import kg.core.Event
import kg.core.base.BaseViewModel
import kg.forestry.localstorage.model.Region
import kg.forestry.repos.FirebaseQueryLiveData
import kg.forestry.repos.PlantsRepository

class RegionListViewModel(
    private val plantsRepository: PlantsRepository
) : BaseViewModel<Event>() {

    private val liveData = FirebaseQueryLiveData(plantsRepository.getRegions())

    val regionListLiveData: MutableLiveData<List<Region>> = MutableLiveData()

    fun fetchRegionsFromServer(): LiveData<DataSnapshot?> {
        Log.d("Region",liveData.value.toString())
        return liveData
    }

    @SuppressLint("CheckResult")
    fun fetchRegionsFromDb() {
            plantsRepository.fetchRegionsFromDb()
                .observeOn(plantsRepository.scheduler.ui())
                .subscribe(regionListLiveData::setValue)

    }


}
