package com.example.mainui.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mainui.data.entities.User

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users ORDER BY id DESC")
    suspend fun getAllUsers(): List<User>
}