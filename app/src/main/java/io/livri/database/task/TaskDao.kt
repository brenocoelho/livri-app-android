package io.livri.database.task

import androidx.room.*

@Dao
interface TaskDao {
    @Query("SELECT * FROM task_table ORDER BY due_date")
    fun getAllTasks(): List<TaskDatabase>

    @Query("SELECT * FROM task_table  WHERE tags = :tag ORDER BY due_date")
    fun getTasksByTag(tag: String): List<TaskDatabase>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg tasks: TaskDatabase)

    @Query("DELETE FROM task_table WHERE id = :id")
    fun delete(id: String)

    @Query("DELETE FROM task_table")
    fun clear()

}