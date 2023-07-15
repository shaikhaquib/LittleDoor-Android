package com.devative.littledoor.architecturalComponents.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.devative.littledoor.model.LoginModel

/**
 * Created by AQUIB RASHID SHAIKH on 25-03-2023.
 */
@Dao
interface UserDao {

    @Query("SELECT * FROM users")
    fun getAll(): LiveData<List<LoginModel.BasicDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: LoginModel.BasicDetails)

    @Query("DELETE FROM users")
    fun deleteAll()
}
