package kg.forestry.repos

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import kg.core.utils.Constants
import kg.core.utils.Harvest
import kg.core.utils.PastureRecord
import kg.forestry.localstorage.Preferences
import kg.forestry.localstorage.db.AppDatabase
import kg.forestry.localstorage.db.DatabaseHelper
import kg.core.utils.PlotRecord
import kg.forestry.localstorage.model.*


class AccountRepository(val prefs: Preferences, private val databaseHelper: DatabaseHelper, private val appDatabase: AppDatabase) {

    private val userInfo = FirebaseDatabase.getInstance().getReference(Constants.USERS_REFERENCE).child(getUserToken())


    fun saveUserSessionToken(token: String?) {
        if (!token.isNullOrEmpty()) prefs.userToken = token
    }

    fun getUserToken(): String = prefs.userToken

    fun removeUserToken() {
        if (prefs.isUserAuthorized()){
            databaseHelper.clearDatabaseTables()
        }
        prefs.userToken = ""
        prefs.isAnonymous = false
    }

    fun getUserProfileInfoFromServer(userId:String) = FirebaseDatabase.getInstance().getReference(
        Constants.USERS_REFERENCE).child(userId)

    fun updateUserInfoInServer(user: User?) {
        this.userInfo.setValue(user)
    }

    fun isUserAnonymous(isAnonymous: Boolean) {
        prefs.isAnonymous = isAnonymous
    }

    fun saveTreesToLocalDB(trees: List<TreeType>){
        appDatabase.treeCatalog.insertTrees(trees)
    }
    fun savePlantCatalogToLocalDB(plants: List<PlantType>){
        appDatabase.plantCatalog.insertPlants(plants)
    }

    fun savePlantsToLocalDB(plants: List<Plant>){
        appDatabase.plantDao.insertPlants(plants)
    }

    fun savePlotsToLocalDB(plots: List<PlotRecord>) {
        appDatabase.plotsDao.insertPlots(plots)
    }

    fun savePasturesToLocalDB(pastures: List<PastureRecord>) {
        appDatabase.pasturesDao.insertPastures(pastures)
    }

    fun saveHarvestsToLocalDB(pastures: List<Harvest>) {
        appDatabase.harvestsDao.insertHarvests(pastures)
    }

    fun saveRegionsToLocalDB(regions: List<Region>) {
        appDatabase.regionDao.insertRegion(regions)
    }

    fun saveVillagesToLocalDB(villages: List<Village>) {
        appDatabase.village.insertRegion(villages)
    }

    fun saveDistrictsToLocalDB(districts: List<District>) {
        appDatabase.district.insertDistrict(districts)
    }
}
