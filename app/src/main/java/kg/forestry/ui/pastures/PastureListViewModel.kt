package kg.forestry.ui.pastures

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import kg.core.Event
import kg.core.base.BaseViewModel
import kg.forestry.repos.FirebaseQueryLiveData
import kg.forestry.repos.HarvestRepository

class PastureListViewModel(
    val harvestRepository: HarvestRepository
) : BaseViewModel<Event>() {

    val plotsListLiveData: MutableLiveData<List<String>> = MutableLiveData()

    private val liveData = FirebaseQueryLiveData(harvestRepository.getUserPasturesListFromServer())

    fun fetchPastures(): LiveData<DataSnapshot?> {
        return liveData
    }

    fun fetchPasturesFromDb() {
        harvestRepository.fetchPasturesFromDb()
            .map { it ->
                val listOfTitles = mutableListOf<String>()
                it.forEach { listOfTitles.add(it.title) }
                return@map listOfTitles
            }
            .observeOn(harvestRepository.scheduler.ui())
            .subscribe(plotsListLiveData::setValue)
    }


}
