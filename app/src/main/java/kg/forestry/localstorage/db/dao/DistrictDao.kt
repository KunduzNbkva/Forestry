package kg.forestry.localstorage.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kg.forestry.localstorage.model.District
import io.reactivex.Flowable

@Dao
interface DistrictDao {
    @Query("SELECT * FROM districts")
    fun getAllDistricts(): Flowable<List<District>>

    @Query("SELECT * FROM districts")
    fun getAllDistrictsAsList(): List<District>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(district: District)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDistrict(district: List<District>)

    @Query("DELETE FROM districts")
    fun deleteAll()
}