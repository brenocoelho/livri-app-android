package io.livri.database.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.livri.domain.User

@Entity(tableName = "user_table")
data class UserDatabase constructor(
    @PrimaryKey(autoGenerate = false)
    val id: String,

    @ColumnInfo(name = "first_name")
    var firstName: String,

    @ColumnInfo(name = "last_name")
    var lastName: String,

    @ColumnInfo(name = "email")
    var email: String,

    @ColumnInfo(name = "phone")
    var phone: String,

    @ColumnInfo(name = "username")
    var username: String,

    @ColumnInfo(name = "document")
    var document: String,

    @ColumnInfo(name = "digital_hash")
    var token: String?
)


fun UserDatabase.asDomainModel(): User {
    return User(
            id = id,
            firstName = firstName,
            lastName = lastName,
            email = email,
            phone= phone,
            document = document,
            username = username,
            token = token)
}

fun List<UserDatabase>.asDomainModel(): List<User> {
    return map {
        User(
            id = it.id,
            firstName = it.firstName,
            lastName = it.lastName,
            email = it.email,
            phone= it.phone,
            document = it.document,
            username = it.username,
            token = it.token)
    }
}
