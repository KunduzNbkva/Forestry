package kg.forestry.localstorage.db

import androidx.room.*
import kg.forestry.localstorage.model.Plant
import io.reactivex.Flowable

@Dao
interface PlantDao {

    @Query("SELECT * FROM plants")
    fun getAllPlants(): Flowable<List<Plant>>

    @Query("SELECT * FROM plants WHERE date=:date")
    fun fetchPlantByDate(date: String): Flowable<Plant>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(plant: Plant?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlants(plant: List<Plant>)

    @Delete()
    fun delete(plant: Plant)


    @Query("DELETE FROM plants")
    fun deleteAll()

    @Query("SELECT * FROM plants")
    fun getAllPlantsAsList(): List<Plant>
}
