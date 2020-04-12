package io.livri.database.user

import androidx.room.*
import io.livri.domain.User

@Dao
interface UserDao {
    @Query("SELECT * FROM user_table")
    fun getAll(): List<UserDatabase>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: UserDatabase)

    @Update
    fun update(user: UserDatabase)

    @Query("SELECT * from user_table LIMIT 1")
    fun get(): UserDatabase

    @Query("DELETE FROM user_table WHERE id = :id")
    fun delete(id: String)

    @Query("DELETE FROM user_table")
    fun clear()
}