package kg.forestry.localstorage.db.dao

import androidx.room.*
import kg.core.utils.Harvest
import io.reactivex.Flowable

@Dao
interface HarvestDao {

    @Query("SELECT * FROM harvests")
    fun getAllHarvests(): Flowable<List<Harvest>>

    @Query("SELECT * FROM harvests")
    fun getAllHarvestsAsList(): List<Harvest>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(harvest: Harvest?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHarvests(harvest: List<Harvest>)

    @Delete()
    fun delete(harvest : Harvest)

    @Query("DELETE FROM harvests")
    fun deleteAll()
}