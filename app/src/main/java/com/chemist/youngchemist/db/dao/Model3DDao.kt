package com.chemist.youngchemist.db.dao

import androidx.room.*
import com.chemist.youngchemist.model.user.Model3D
import kotlinx.coroutines.flow.Flow

@Dao
interface Model3DDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveModel3D(model3D: Model3D)

    @Query("SELECT * FROM saved3DModels")
    fun getAllModels(): List<Model3D>

    @Delete
    suspend fun deleteModel(model3D: Model3D)

    @Query("SELECT * FROM saved3DModels WHERE userId LIKE :currentUserId AND modelId LIKE :modelId")
    fun getModel(currentUserId: String,modelId: String): List<Model3D>

    @Query("SELECT * FROM saved3DModels WHERE userId LIKE :currentUserId")
    fun getAllModelsFlow(currentUserId: String): Flow<List<Model3D>>

}