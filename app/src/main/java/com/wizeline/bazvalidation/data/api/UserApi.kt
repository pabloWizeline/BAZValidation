package com.wizeline.bazvalidation.data.api

import com.wizeline.bazvalidation.domain.model.User
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApi {

    @GET("users")
    suspend fun getUsers(): List<User>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): User
}
