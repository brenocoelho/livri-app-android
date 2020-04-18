package io.livri.database.task

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {
    @Query("SELECT * FROM task_table ORDER BY due_date")
    fun getAll(): LiveData<List<TaskDatabase>>

    @Query("SELECT * FROM task_table  WHERE tags = :tag ORDER BY due_date")
    fun getTasksByTag(tag: String): LiveData<List<TaskDatabase>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg tasks: TaskDatabase)

    @Query("DELETE FROM task_table WHERE id = :id")
    fun delete(id: String)

    @Query("DELETE FROM task_table")
    fun clear()

}