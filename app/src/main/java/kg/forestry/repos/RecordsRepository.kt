package kg.forestry.repos

import com.google.firebase.database.FirebaseDatabase
import kg.core.utils.Constants
import kg.core.utils.Helper.getRandomLongId
import kg.core.utils.Helper.getRandomString
import kg.forestry.localstorage.Preferences
import kg.forestry.localstorage.db.AppDatabase
import kg.core.utils.PastureRecord
import kg.forestry.localstorage.model.PlantType
import kg.core.utils.PlotRecord

class RecordsRepository(private val prefs: Preferences, private val db: AppDatabase) {

    private val userPlants =
        FirebaseDatabase.getInstance().getReference(Constants.PLANT_CATALOG).child(Constants.IMAGES)


    fun saveNewPlotsRecord(plotRecord: PlotRecord?) {
        plotRecord?.id = getRandomString(4)
        saveRecordInDB(plotRecord)
    }

    fun saveRecordInDB(plotRecord: PlotRecord?) {
        db.plotsDao.insert(plotRecord)
    }

    fun saveNewPasturesRecord(record: PastureRecord?) {
        record?.id = getRandomString(4)
        savePastureRecordInDB(record)
    }

    private fun savePastureRecordInDB(record: PastureRecord?) {
        db.pasturesDao.insert(record)
    }

    fun saveNewPlantRecord(plant: PlantType) {
        plant.id = getRandomLongId()
        db.plantCatalog.insert(plant)
    }
}
