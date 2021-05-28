package kg.forestry.localstorage.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kg.core.utils.PastureRecord
import io.reactivex.Flowable

@Dao
interface PasturesDao {

    @Query("SELECT * FROM pastures")
    fun getAllPastures(): Flowable<List<PastureRecord>>

    @Query("SELECT * FROM pastures")
    fun getAllPasturesAsList(): List<PastureRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(pastureRecord: PastureRecord?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPastures(pastures: List<PastureRecord?>)

    @Query("DELETE FROM pastures")
    fun deleteAll()
}
