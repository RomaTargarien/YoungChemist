package com.example.youngchemist.db.dao

import androidx.room.*
import com.example.youngchemist.model.user.Model3D
import kotlinx.coroutines.flow.Flow

@Dao
interface Model3DDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveModel3D(model3D: Model3D)

    @Query("SELECT * FROM saved3DModels WHERE userId LIKE :currentUserId")
    fun getAllModels(currentUserId: String): List<Model3D>

    @Delete
    suspend fun deleteModel(model3D: Model3D)
}