package com.wizeline.bazvalidation.domain.repository

import com.wizeline.bazvalidation.domain.model.User

interface UserRepository {
    suspend fun getUsers(): Result<List<User>>
    suspend fun getUser(id: Int): Result<User>
}
