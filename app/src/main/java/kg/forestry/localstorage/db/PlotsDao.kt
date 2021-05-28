package kg.forestry.localstorage.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kg.core.utils.PlotRecord
import io.reactivex.Flowable

@Dao
interface PlotsDao {

    @Query("SELECT * FROM plots")
    fun getAllPlots(): Flowable<List<PlotRecord>>

    @Query("SELECT * FROM plots")
    fun getAllPlotsAsList(): List<PlotRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(plot: PlotRecord?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlots(plants: List<PlotRecord>)

    @Query("DELETE FROM plots")
    fun deleteAll()
}