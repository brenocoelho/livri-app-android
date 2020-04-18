package io.livri.network

import com.squareup.moshi.JsonClass
import io.livri.database.tag.TagDatabase
import io.livri.domain.Tag

/**
 * DataTransferObjects go in this file. These are responsible for parsing responses from the server
 * or formatting objects to send to the server. You should convert these to domain objects before
 * using them.
 */

@JsonClass(generateAdapter = true)
data class TagNetwork(
    val id: String,
    var name: String,
    var color: String,
    var priority: Int
)

@JsonClass(generateAdapter = true)
data class TagsNetworkContainer(val data: List<TagNetwork>)

@JsonClass(generateAdapter = true)
data class TagNetworkRequest(val tag: TagNetwork)

@JsonClass(generateAdapter = true)
data class TagNetworkResponse(val data: TagNetwork)


/**
 * Convert Network results to database objects
 */

fun TagNetwork.asRequest(): TagNetworkRequest {
    return TagNetworkRequest(this)
}

fun TagsNetworkContainer.asDomainModel(): List<Tag> {
    return data.map {
        Tag(
            id = it.id,
            name = it.name,
            color = it.color,
            priority = it.priority)
    }
}

fun TagsNetworkContainer.asDatabaseModel(): Array<TagDatabase> {
    return data.map {
        TagDatabase(
            id = it.id,
            name = it.name,
            color = it.color,
            priority = it.priority)
    }.toTypedArray()
}

fun TagNetworkResponse.asDatabaseModel(): TagDatabase {
    return data.let {
        TagDatabase(
            id = it.id,
            name = it.name,
            color = it.color,
            priority = it.priority)
    }
}
