package kg.forestry.ui.records

import kg.core.Event
import kg.forestry.localstorage.model.ListType
import kg.core.base.BaseViewModel
import kg.core.utils.PastureRecord
import kg.forestry.localstorage.model.PlantType
import kg.core.utils.PlotRecord
import kg.forestry.repos.RecordsRepository

class NewRecordViewModel(private val recordsRepository: RecordsRepository): BaseViewModel<Event>() {

    var recordType : String?= null

    fun saveRecord(title: String) {
        if (!recordType.isNullOrEmpty()) {
            when(recordType){
                ListType.PLOT.value -> { recordsRepository.saveNewPlotsRecord(
                    PlotRecord(
                        title = title
                    )
                ) }
                ListType.PASTURE.value -> recordsRepository.saveNewPasturesRecord(
                    PastureRecord(
                        title = title
                    )
                )
                ListType.PLANT.value -> recordsRepository.saveNewPlantRecord(PlantType(name_ru = title))
            }
        }
    }

}
