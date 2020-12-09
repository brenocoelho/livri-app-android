package io.livri.domain

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id: String,
    var firstName: String,
    var lastName: String,
    var username: String,
    var document: String,
    var email: String,
    var phone: String,
    var token:String?
): Parcelable {

}