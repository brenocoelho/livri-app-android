package io.livri.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.livri.database.tag.TagDao
import io.livri.database.task.TaskDao
import io.livri.database.user.UserDao
import io.livri.database.task.TaskDatabase
import io.livri.database.user.UserDatabase
import io.livri.database.tag.TagDatabase

@Database(entities = [TaskDatabase::class, TagDatabase::class, UserDatabase::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class LivriDatabase : RoomDatabase() {

    abstract val taskDao: TaskDao
    abstract val tagDao: TagDao
    abstract val userDao: UserDao

    companion object {

        @Volatile
        private var INSTANCE: LivriDatabase? = null

        fun getInstance(context: Context): LivriDatabase {
            synchronized( this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        LivriDatabase::class.java,
                        "livri_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}