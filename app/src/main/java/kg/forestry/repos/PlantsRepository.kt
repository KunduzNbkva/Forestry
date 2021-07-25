package kg.forestry.repos

import com.google.firebase.database.FirebaseDatabase
import io.reactivex.Flowable
import kg.core.utils.Constants
import kg.forestry.localstorage.Preferences
import kg.forestry.localstorage.Scheduler
import kg.forestry.localstorage.db.AppDatabase
import kg.forestry.localstorage.model.District
import kg.forestry.localstorage.model.Plant
import kg.forestry.localstorage.model.Region
import kg.forestry.localstorage.model.Village

class PlantsRepository(
    private val prefs: Preferences,
    private val db: AppDatabase,
    val scheduler: Scheduler
) {
    val userPlants =
        FirebaseDatabase.getInstance().getReference(Constants.PLANTS_REFERENCE)

    private val regions =
        FirebaseDatabase.getInstance().getReference(Constants.REGIONS)

    private val villages =
        FirebaseDatabase.getInstance().getReference(Constants.VILLAGES)

    private val districts =
        FirebaseDatabase.getInstance().getReference(Constants.DISTRICTS)

    private val userPlantCatalog =
        FirebaseDatabase.getInstance().getReference(Constants.PLANT_CATALOG)
            .child(Constants.IMAGESTEST)

    private val userTreesCatalog =
        FirebaseDatabase.getInstance().getReference(Constants.TREES_CATALOG).child(Constants.IMAGES)


    fun getUserPlantsListFromServer() = this.userPlants

    fun getUserToken(): String = prefs.userToken

    fun updateUserPlantInDB(plant: Plant?) = db.plantDao.insert(plant)

    fun fetchPlantsFromDb(): Flowable<List<Plant>> {
        return db.plantDao.getAllPlants()
    }

    fun fetchPlantByDate(date: String): Flowable<Plant>{
        return db.plantDao.fetchPlantByDate(date)
    }

    fun fetchRegionsFromDb(): Flowable<List<Region>> {
        return db.regionDao.getAllRegions()
    }

    fun fetchVillagesFromDb(): Flowable<List<Village>> {
        return db.village.getAllVillages()
    }

    fun fetchDistrictsFromDb(): Flowable<List<District>> {
        return db.district.getAllDistricts()
    }

    fun fetchPlantsFromDbAsList() = db.plantDao.getAllPlantsAsList()

    fun getUserPlantsCatalogFromServer() =  this.userPlantCatalog

    fun getUserTreesCatalogFromServer() =  this.userTreesCatalog

    fun removePlantFromServer(id: String) = this.userPlants.child(id).removeValue()

    fun removePlantFromDB(plant: Plant) = db.plantDao.delete(plant)

    fun fetchTreeCatalogFromDb() = db.treeCatalog.getAllTrees()

    fun isTreesDbEmpty() = db.treeCatalog.getAllTreesList().isEmpty()

    fun isPlantsDbEmpty() = db.plantCatalog.getAllPlantsList().isEmpty()

    fun isRegionsDbEmpty() = db.regionDao.getAllRegionsAsList().isEmpty()

    fun isVillagesDbEmpty() = db.village.getAllRegionsAsList().isEmpty()

    fun isDistrictsDbEmpty() = db.district.getAllDistrictsAsList().isEmpty()

    fun fetchPlantCatalogFromDbAsList() = db.plantCatalog.getAllPlantsList()

    fun getRegions() = this.regions

    fun getVillages() = this.villages

    fun getDistricts() = this.districts

}