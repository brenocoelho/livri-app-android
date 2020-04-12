package io.livri.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.livri.database.task.TaskDatabase
import io.livri.domain.Task
import java.util.*

/**
 * DataTransferObjects go in this file. These are responsible for parsing responses from the server
 * or formatting objects to send to the server. You should convert these to domain objects before
 * using them.
 */

@JsonClass(generateAdapter = true)
data class TaskNetwork(
    val id: String,
    var name: String,
    @Json(name = "when") var dueDate: Date,
    var tags: String?,
    var status: String,
    var frequency: String
)

@JsonClass(generateAdapter = true)
data class TasksNetworkContainer(val data: List<TaskNetwork>)

@JsonClass(generateAdapter = true)
data class TaskNetworkRequest(val task: TaskNetwork)

@JsonClass(generateAdapter = true)
data class TaskNetworkResponse(val data: TaskNetwork)


/**
 * Convert Network results to database objects
 */

fun TaskNetwork.asRequest(): TaskNetworkRequest {
    return TaskNetworkRequest(this)
}

fun TasksNetworkContainer.asDomainModel(): List<Task> {
    return data.map {
        Task(
            id = it.id,
            name = it.name,
            dueDate = it.dueDate,
            tags = it.tags,
            status = it.status,
            frequency = it.frequency)
    }
}

fun TasksNetworkContainer.asDatabaseModel(): Array<TaskDatabase> {
    return data.map {
        TaskDatabase(
            id = it.id,
            name = it.name,
            dueDate = it.dueDate,
            tags = it.tags,
            status = it.status,
            frequency = it.frequency)
    }.toTypedArray()
}
