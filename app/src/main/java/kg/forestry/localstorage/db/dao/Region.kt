package kg.forestry.localstorage.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kg.forestry.localstorage.model.Region
import io.reactivex.Flowable

@Dao
interface RegionDao {
    @Query("SELECT * FROM regions")
    fun getAllRegions(): Flowable<List<Region>>

    @Query("SELECT * FROM regions")
    fun getAllRegionsAsList(): List<Region>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(region: Region?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRegion(region: List<Region>)

    @Query("DELETE FROM regions")
    fun deleteAll()
}