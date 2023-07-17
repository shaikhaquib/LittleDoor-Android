package com.devative.littledoor.architecturalComponents.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.devative.littledoor.model.LoginModel
import com.devative.littledoor.model.UserDetails

/**
 * Created by AQUIB RASHID SHAIKH on 25-03-2023.
 */
@Database(entities = [UserDetails.Data::class], version = 5)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
