package kg.forestry.repos

import com.google.firebase.database.FirebaseDatabase
import kg.core.utils.Constants
import kg.forestry.localstorage.Scheduler
import kg.forestry.localstorage.Preferences
import kg.forestry.localstorage.db.AppDatabase
import kg.core.utils.Harvest
import kg.forestry.localstorage.model.District
import kg.forestry.localstorage.model.Region
import kg.forestry.localstorage.model.Village
import io.reactivex.Flowable

class HarvestRepository(
    private val prefs: Preferences,
    private val db: AppDatabase,
    val scheduler: Scheduler
) {

    private val userHarvest =
        FirebaseDatabase.getInstance().getReference(Constants.HARVEST_REFERENCE)

    private val userPlots =
        FirebaseDatabase.getInstance().getReference(Constants.PLOTS)

    private val userPastures =
        FirebaseDatabase.getInstance().getReference(Constants.PASTURES)

    private val districts =
        FirebaseDatabase.getInstance().getReference(Constants.DISTRICTS)

    private val regions =
        FirebaseDatabase.getInstance().getReference(Constants.REGIONS)

    private val villages =
        FirebaseDatabase.getInstance().getReference(Constants.VILLAGES)

    fun fetchRegionsFromDb(): Flowable<List<Region>> {
        return db.regionDao.getAllRegions()
    }

    fun fetchVillagesFromDb(): Flowable<List<Village>> {
        return db.village.getAllVillages()
    }

    fun fetchDistrictsFromDb(): Flowable<List<District>> {
        return db.district.getAllDistricts()
    }

    fun isRegionsDbEmpty() = db.regionDao.getAllRegionsAsList().isEmpty()

    fun isVillagesDbEmpty() = db.village.getAllRegionsAsList().isEmpty()

    fun isDistrictsDbEmpty() = db.district.getAllDistrictsAsList().isEmpty()


    fun fetchHarvestsFromDb() = db.harvestsDao.getAllHarvests()

    fun fetchHarvestsFromDbAsList() = db.harvestsDao.getAllHarvestsAsList()

    fun getUserToken(): String = prefs.userToken

    fun getUserPlotsListFromServer() = this.userPlots

    fun getUserPasturesListFromServer() = this.userPastures

    fun getUserHarvestListFromServer() = this.userHarvest

    fun updateUserHarvestInDB(harvest: Harvest?) = db.harvestsDao.insert(harvest)

    fun fetchPlotsFromDb() = db.plotsDao.getAllPlots()

    fun fetchPlotsFromDbAsList() = db.plotsDao.getAllPlotsAsList()

    fun fetchPasturesFromDbAsList() = db.pasturesDao.getAllPasturesAsList()

    fun fetchPasturesFromDb() = db.pasturesDao.getAllPastures()

    fun removeHarvestFromServer(id: String) = this.userHarvest.child(id).removeValue()

    fun removeHarvestFromDB(harvest: Harvest) = db.harvestsDao.delete(harvest)

    fun getRegions() = this.regions

    fun getVillages() = this.villages

    fun getDistricts() = this.districts

}
