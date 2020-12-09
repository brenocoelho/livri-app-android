package io.livri.database.tag

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TagDao {
    @Query("SELECT * FROM tag_table ORDER BY name")
    fun getAll(): LiveData<List<TagDatabase>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg tags: TagDatabase)

    @Query("DELETE FROM tag_table WHERE id = :id")
    fun delete(id: String)

    @Query("DELETE FROM tag_table")
    fun clear()

}