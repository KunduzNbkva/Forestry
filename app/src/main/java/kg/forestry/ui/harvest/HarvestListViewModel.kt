package kg.forestry.ui.harvest

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import kg.core.Event
import kg.forestry.ui.core.base.BaseViewModel
import kg.core.utils.Harvest
import kg.forestry.repos.HarvestRepository


class HarvestListViewModel(private val harvestRepository: HarvestRepository): BaseViewModel<Event>() {

    val harvestListLiveData: MutableLiveData<List<Harvest>> = MutableLiveData()

    @SuppressLint("CheckResult")
    fun fetchHarvestsFromDb(){
        harvestRepository.fetchHarvestsFromDb()
            .observeOn(harvestRepository.scheduler.ui())
            .subscribe(harvestListLiveData::setValue)
    }


}