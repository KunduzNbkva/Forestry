package kg.forestry.localstorage.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kg.forestry.localstorage.model.TreeType
import io.reactivex.Flowable

@Dao
interface TreeTypeDao {
    @Query("SELECT * FROM tree_type")
    fun getAllTrees(): Flowable<List<TreeType>>

    @Query("SELECT * FROM tree_type")
    fun getAllTreesList(): List<TreeType>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tree: TreeType?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrees(trees: List<TreeType>)

    @Query("DELETE FROM tree_type")
    fun deleteAll()

}