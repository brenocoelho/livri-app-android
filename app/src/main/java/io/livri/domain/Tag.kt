package io.livri.domain

import android.os.Parcelable
import io.livri.database.tag.TagDatabase
import io.livri.network.TagNetwork
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Tag(
    val id: String,
    var name: String,
    var color: String,
    var priority: Int
): Parcelable {
}


fun Tag.asDatabaseModel(): TagDatabase {
    return TagDatabase(
        id = id,
        name = name,
        color = color,
        priority = priority)
}

fun Tag.asNetworkModel(): TagNetwork {
    return TagNetwork(
        id = id,
        name = name,
        color = color,
        priority = priority)
}

fun List<Tag>.asDatabaseModel(): Array<TagDatabase> {
    return map {
        TagDatabase(
            id = it.id,
            name = it.name,
            color = it.color,
            priority = it.priority)
    }.toTypedArray()
}