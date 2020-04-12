package io.livri.database.task

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.livri.domain.Task
import java.util.*

@Entity(tableName = "task_table")
data class TaskDatabase constructor(
    @PrimaryKey(autoGenerate = false)
    val id: String,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "due_date")
    var dueDate: Date,

    @ColumnInfo(name = "tags")
    var tags: String?,

    @ColumnInfo(name = "status")
    var status: String,

    @ColumnInfo(name = "frequency")
    var frequency: String
)

fun List<TaskDatabase>.asDomainModel(): List<Task> {
    return map {
        Task(
            id = it.id,
            name = it.name,
            dueDate = it.dueDate,
            tags = it.tags,
            status = it.status,
            frequency = it.frequency
        )
    }
}