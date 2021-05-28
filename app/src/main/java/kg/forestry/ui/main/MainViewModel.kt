package kg.forestry.ui.main


import android.util.Log
import com.google.firebase.database.*
import kg.core.Event
import kg.core.base.BaseViewModel
import kg.core.utils.Constants
import kg.core.utils.Harvest
import kg.core.utils.Helper.getRandomString
import kg.core.utils.PastureRecord
import kg.core.utils.PlotRecord
import kg.forestry.localstorage.Preferences
import kg.forestry.localstorage.db.AppDatabase
import kg.forestry.localstorage.model.*
import kg.forestry.repos.AccountRepository
import kg.forestry.repos.FirebaseQueryLiveData
import kg.forestry.repos.HarvestRepository
import kg.forestry.repos.PlantsRepository


class MainViewModel(
    private val accountRepository: AccountRepository,
    private val plantsRepository: PlantsRepository,
    private val harvestRepository: HarvestRepository,
    private val db: AppDatabase,
    private val prefs: Preferences
) : BaseViewModel<Event>() {

    var mDatabase: DatabaseReference? = null

    var trees: FirebaseQueryLiveData? = null
    var plantCatalog: FirebaseQueryLiveData? = null
    var plots = FirebaseQueryLiveData(harvestRepository.getUserPlotsListFromServer())
    var pastures = FirebaseQueryLiveData(harvestRepository.getUserPasturesListFromServer())
    var harvests = FirebaseQueryLiveData(harvestRepository.getUserHarvestListFromServer())
    var plants = FirebaseQueryLiveData(plantsRepository.getUserPlantsListFromServer())
    var regions = FirebaseQueryLiveData(plantsRepository.getRegions())
    var villages = FirebaseQueryLiveData(plantsRepository.getVillages())
    var districts = FirebaseQueryLiveData(plantsRepository.getDistricts())

    private val userHarvest =
        FirebaseDatabase.getInstance().getReference(Constants.HARVEST_REFERENCE)
    private val userPlants = FirebaseDatabase.getInstance().getReference(Constants.PLANTS_REFERENCE)
    private val userPlots = FirebaseDatabase.getInstance().getReference(Constants.PLOTS)
    private val userPastures = FirebaseDatabase.getInstance().getReference(Constants.PASTURES)

//    private val userRegions = FirebaseDatabase.getInstance().getReference(Constants.REGIONS)

    fun removeUserToken() {
        accountRepository.removeUserToken()
    }

    fun fetchUserData() {
        fetchTreesFromServer()
        fetchRegionsFromServer()
        fetchVillagesFromServer()
        fetchDistrictsFromServer()

        saveLocalHarvestsToServer()
        saveLocalPlantsToServer()
        saveLocalPlotsToServer()
        saveLocalPasturesToServer()

    }

    fun saveLocalPlantsHarvests(){

    }

    private fun fetchTreesFromServer() {
        if (plantsRepository.isTreesDbEmpty()) {
            when (isNetworkConnected) {
                true -> this.trees =
                    FirebaseQueryLiveData(plantsRepository.getUserTreesCatalogFromServer())
//                false -> event.value = Event.Message("Проверьте интернет соединение")
            }
        }
    }

    private fun fetchRegionsFromServer() {
        if (plantsRepository.isRegionsDbEmpty()) {
            when (isNetworkConnected) {
                true -> {
                    this.regions =
                        FirebaseQueryLiveData(plantsRepository.getRegions())
                    event.value = Event.RefreshData()

                }
//                false -> event.value = Event.Message("Проверьте интернет соединение")
            }
        }
    }

    private fun fetchVillagesFromServer() {
        if (plantsRepository.isRegionsDbEmpty()) {
            when (isNetworkConnected) {
                true -> {
                    this.villages = FirebaseQueryLiveData(plantsRepository.getVillages())
                    event.value = Event.RefreshData()
                }
//                false -> event.value = Event.Message("Проверьте интернет соединение")
            }
        }
    }

    private fun fetchDistrictsFromServer() {
        if (plantsRepository.isDistrictsDbEmpty()) {
            when (isNetworkConnected) {
                true -> this.districts =
                    FirebaseQueryLiveData(plantsRepository.getDistricts())
//                false -> event.value = Event.Message("Проверьте интернет соединение")
            }
        }
    }

    fun fetchPlantsFromServer(): FirebaseQueryLiveData {
        return FirebaseQueryLiveData(plantsRepository.getUserPlantsCatalogFromServer())
    }

    private fun saveLocalHarvestsToServer() {
        if (isNetworkConnected) {
            if (prefs.isUserAuthorized()) {
                setProgress(true)
                val harvests = harvestRepository.fetchHarvestsFromDbAsList()
                harvests.forEach { harvest ->
                    if (!harvest.isInServer) {
                        harvest.userId = prefs.userToken
                        harvest.isInServer = true
                        db.harvestsDao.insert(harvest)

                        this.userHarvest.child(harvest.id).setValue(harvest)
                    }
                }
                event.value = Event.RefreshData()

            }

        } else {
//            event.value = Event.Message("Проверьте интернет соединение")
        }

        setProgress(false)

    }

    private fun saveLocalPlantsToServer() {
        if (isNetworkConnected) {
            if (prefs.isUserAuthorized()) {
                setProgress(true)
                val plants = plantsRepository.fetchPlantsFromDbAsList()
                plants.forEach { plant ->
                    if (!plant.isInServer) {
                        plant.userId = prefs.userToken
                        plant.isInServer = true
                        db.plantDao.insert(plant)
                        this.userPlants.child(plant.id).setValue(plant)
                    }
                }
                event.value = Event.RefreshData()

            }

        } else {
//            event.value = Event.Message("Проверьте интернет соединение")
        }
        setProgress(false)
    }

    private fun saveLocalPlotsToServer() {
        if (isNetworkConnected) {
            if (prefs.isUserAuthorized()) {
                setProgress(true)
                val localPlots = harvestRepository.fetchPlotsFromDbAsList()
                localPlots.forEach { plotRecord ->
                    if (!plotRecord.isInServer) {
                        plotRecord.userId = prefs.userToken
                        plotRecord.isInServer = true
                        db.plotsDao.insert(plotRecord)
                        this.userPlots.child(getRandomString(5)).setValue(plotRecord)
                    }
                }
                event.value = Event.RefreshData()
            }

        } else {
//            event.value = Event.Message("Проверьте интернет соединение")
        }
        setProgress(false)
    }

    fun savePlotsToLocalDb(plots: List<PlotRecord>) {
        Log.d("PLOTS", plots.toString())
        accountRepository.savePlotsToLocalDB(plots)
    }

    private fun saveLocalPasturesToServer() {
        if (isNetworkConnected) {
            if (prefs.isUserAuthorized()) {
                setProgress(true)
                val localPastures = harvestRepository.fetchPasturesFromDbAsList()
                localPastures.forEach { pastureRecord ->
                    if (!pastureRecord.isInServer) {
                        pastureRecord.userId = prefs.userToken
                        pastureRecord.isInServer = true
                        db.pasturesDao.insert(pastureRecord)
                        this.userPastures.child(getRandomString(5)).setValue(pastureRecord)
                    }
                }
                event.value = Event.RefreshData()
            }

        } else {
//            event.value = Event.Message("Проверьте интернет соединение")
        }
        setProgress(false)
    }

    fun savePasturesToLocalDb(pastures: List<PastureRecord>) {
        accountRepository.savePasturesToLocalDB(pastures)
    }

    fun saveHarvestsToLocalDb(harvests: List<Harvest>) {
        accountRepository.saveHarvestsToLocalDB(harvests)
    }

    fun saveRegionsToLocalDb(regions: List<Region>) {
        accountRepository.saveRegionsToLocalDB(regions)
    }

    fun saveVillagesToLocalDb(village: List<Village>) {
        accountRepository.saveVillagesToLocalDB(village)
    }

    fun saveDisctricsToLocalDb(district: List<District>) {
        accountRepository.saveDistrictsToLocalDB(district)
    }


    fun saveTreesToDB(trees: List<TreeType>) {
        accountRepository.saveTreesToLocalDB(trees)
    }

    fun savePlantCatalogsToDB(plants: List<PlantType>) {
        Log.d("LIST", plants.toString())
        accountRepository.savePlantCatalogToLocalDB(plants)
    }

    fun test() {
        if (isPlantsDbEmpty()) {
            when (isNetworkConnected) {
                true -> {
                    setProgress(true)
                    mDatabase = FirebaseDatabase.getInstance().getReference(Constants.PLANT_CATALOG)
                        .child(Constants.IMAGESTEST)
                    mDatabase!!.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val plantCatalog = mutableListOf<PlantType>()
                            for (ds in dataSnapshot.children) {
                                val value = ds.getValue(PlantType::class.java)
                                plantCatalog.add(value!!)
                            }
                            savePlantCatalogsToDB(plantCatalog)
                            setProgress(false)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            setProgress(false)
                        }
                    })
                }
            }
        }

    }

    fun savePlantsToLocalDb(plants: List<Plant>) {
        accountRepository.savePlantsToLocalDB(plants)
    }

    fun isPlantsDbEmpty() = plantsRepository.isPlantsDbEmpty()


}
