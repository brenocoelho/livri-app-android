package io.livri.database.tag

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.livri.domain.Tag

@Entity(tableName = "tag_table")
data class TagDatabase constructor(
    @PrimaryKey(autoGenerate = false)
    val id: String,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "color")
    var color: String,

    @ColumnInfo(name = "priority")
    var priority: Int
)

fun List<TagDatabase>.asDomainModel(): List<Tag> {
    return map {
        Tag(
            id = it.id,
            name = it.name,
            color = it.color,
            priority = it.priority
        )
    }
}