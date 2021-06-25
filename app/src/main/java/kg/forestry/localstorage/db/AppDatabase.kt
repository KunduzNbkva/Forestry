package kg.forestry.localstorage.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kg.forestry.localstorage.model.District
import kg.forestry.localstorage.model.Plant
import kg.core.utils.Harvest
import kg.core.utils.PastureRecord
import kg.core.utils.PlotRecord
import kg.forestry.localstorage.db.dao.*
import kg.forestry.localstorage.model.*

@Database(entities = [Harvest::class, Plant::class,
    Region::class, Village::class, District::class,
    PlotRecord::class, PastureRecord::class,
    TreeType::class,PlantType::class], version = 41, exportSchema = false)

@TypeConverters(Converter::class)

abstract class AppDatabase : RoomDatabase() {

    abstract val harvestsDao: HarvestDao
    abstract val plantDao: PlantDao
    abstract val pasturesDao: PasturesDao
    abstract val treeCatalog: TreeTypeDao
    abstract val plantCatalog: PlantTypeDao
    abstract val plotsDao: PlotsDao
    abstract val regionDao: RegionDao
    abstract val village: VillageDao
    abstract val district: DistrictDao


    companion object {

        val DB_NAME = "forestry.db"
        private var instance: AppDatabase? = null


    }
}