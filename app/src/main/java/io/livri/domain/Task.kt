package io.livri.domain
/**
 * Domain objects are plain Kotlin data classes that represent the things in our app. These are the
 * objects that should be displayed on screen, or manipulated by the app.
 *
 * @see database for objects that are mapped to the database
 * @see network for objects that parse or prepare network calls
 */

import android.os.Parcelable
import io.livri.database.task.TaskDatabase
import io.livri.network.TaskNetwork
import io.livri.util.smartTruncate
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Task(
    val id: String,
    var name: String,
    var dueDate: Date,
    var tags: String?,
    var status: String,
    var frequency: String): Parcelable {
    val shortDescription: String
        get() = name.smartTruncate(50)
}

fun Task.asDatabaseModel(): TaskDatabase {
    return TaskDatabase(
        id = id,
        name = name,
        dueDate = dueDate,
        tags = tags,
        status = status,
        frequency = frequency)
}

fun Task.asNetworkModel(): TaskNetwork {
    return TaskNetwork(
        id = id,
        name = name,
        dueDate = dueDate,
        tags = tags,
        status = status,
        frequency = frequency
    )
}

fun List<Task>.asDatabaseModel(): Array<TaskDatabase> {
    return map {
        TaskDatabase(
            id = it.id,
            name = it.name,
            dueDate = it.dueDate,
            tags = it.tags,
            status = it.status,
            frequency = it.frequency)
    }.toTypedArray()
}