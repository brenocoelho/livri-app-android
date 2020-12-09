package io.livri.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.livri.database.user.UserDatabase
import io.livri.domain.User

/**
 * DataTransferObjects go in this file. These are responsible for parsing responses from the server
 * or formatting objects to send to the server. You should convert these to domain objects before
 * using them.
 */

@JsonClass(generateAdapter = true)
data class UserNetwork(
    val id: String,
    @Json(name = "first_name") var firstName: String,
    @Json(name = "last_name") var lastName: String,
    var username: String,
    var document: String,
    var email: String,
    var phone: String,
    @Json(name = "token") var token: String?
)

@JsonClass(generateAdapter = true)
data class LoginNetwork(
    var username: String,
    var password: String
)

@JsonClass(generateAdapter = true)
data class UsersNetworkContainer(val data: List<UserNetwork>)

@JsonClass(generateAdapter = true)
data class UserNetworkRequest(val user: UserNetwork)

@JsonClass(generateAdapter = true)
data class UserNetworkResponse(val data: UserNetwork)


@JsonClass(generateAdapter = true)
data class LoginNetworkRequest(val user: LoginNetwork)

@JsonClass(generateAdapter = true)
data class LoginNetworkResponse(val data: UserNetwork)

/**
 * Convert Network results to database objects
 */

fun LoginNetwork.asRequest(): LoginNetworkRequest {
    return LoginNetworkRequest(this)
}
fun UserNetwork.asRequest(): UserNetworkRequest {
    return UserNetworkRequest(this)
}

fun UsersNetworkContainer.asDomainModel(): List<User> {
    return data.map {
        User(
            id = it.id,
            firstName = it.firstName,
            lastName = it.lastName,
            email = it.email,
            phone = it.phone,
            username = it.username,
            document = it.document,
            token = it.token)
    }
}

fun UserNetworkResponse.asDatabaseModel():UserDatabase {
    return data.let {
        UserDatabase(
            id = it.id,
            firstName = it.firstName,
            lastName = it.lastName,
            email = it.email,
            phone = it.phone,
            username = it.username,
            document = it.document,
            token = it.token)
    }
}

fun UsersNetworkContainer.asDatabaseModel(): Array<UserDatabase> {
    return data.map {
        UserDatabase(
            id = it.id,
            firstName = it.firstName,
            lastName = it.lastName,
            email = it.email,
            phone = it.phone,
            username = it.username,
            document = it.document,
            token = it.token)
    }.toTypedArray()
}



fun LoginNetworkResponse.asDatabaseModel():UserDatabase {
    return data.let {
        UserDatabase(
            id = it.id,
            firstName = it.firstName,
            lastName = it.lastName,
            email = it.email,
            phone = it.phone,
            username = it.username,
            document = it.document,
            token = it.token)
    }
}