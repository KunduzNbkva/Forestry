package kg.forestry.localstorage.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kg.forestry.localstorage.model.Village
import io.reactivex.Flowable

@Dao
interface VillageDao {
    @Query("SELECT * FROM villages")
    fun getAllVillages(): Flowable<List<Village>>

    @Query("SELECT * FROM villages")
    fun getAllRegionsAsList(): List<Village>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(village: Village?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRegion(village: List<Village>)

    @Query("DELETE FROM villages")
    fun deleteAll()
}