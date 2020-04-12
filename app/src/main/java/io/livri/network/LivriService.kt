package io.livri.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.*

private const val BASE_URL = "https://glacial-headland-00548.herokuapp.com/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()


object Network {
    val retrofitService : LivriService by lazy {
        retrofit.create(LivriService::class.java)
    }
}

interface LivriService {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: String?): UserNetworkResponse

    @GET("tasks")
    suspend fun getTasks(): TasksNetworkContainer

    @POST("tasks")
    suspend fun createTask(@Body request: TaskNetworkRequest): TaskNetworkResponse

    @PUT("tasks/{id}")
    suspend fun updateTask(@Path("id") id: String?, @Body request: TaskNetworkRequest): TaskNetworkResponse

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: String?): Response<Unit>

    @GET("tags")
    suspend fun getTags(): TagsNetworkContainer

    @POST("tags")
    suspend fun createTag(@Body request: TagNetworkRequest): TagNetworkResponse

    @PUT("tags/{id}")
    suspend fun updateTag(@Path("id") id: String?, @Body request: TagNetworkRequest): TagNetworkResponse

    @DELETE("tags/{id}")
    suspend fun deleteTag(@Path("id") id: String?): Response<Unit>
}