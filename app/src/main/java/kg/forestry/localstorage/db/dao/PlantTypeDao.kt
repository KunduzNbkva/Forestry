package kg.forestry.localstorage.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kg.forestry.localstorage.model.PlantType
import io.reactivex.Flowable

@Dao
interface PlantTypeDao {

    @Query("SELECT * FROM plant_type")
    fun getAllPlants(): Flowable<List<PlantType>>

    @Query("SELECT * FROM plant_type")
    fun getAllPlantsList(): List<PlantType>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(plants: PlantType?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlants(plants: List<PlantType>)

    @Query("DELETE FROM plant_type")
    fun deleteAll()
}
