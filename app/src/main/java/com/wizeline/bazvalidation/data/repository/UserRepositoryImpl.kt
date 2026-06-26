package com.wizeline.bazvalidation.data.repository

import com.wizeline.bazvalidation.data.api.UserApi
import com.wizeline.bazvalidation.domain.model.User
import com.wizeline.bazvalidation.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val api: UserApi
) : UserRepository {

    override suspend fun getUsers(): Result<List<User>> = runCatching {
        api.getUsers()
    }

    override suspend fun getUser(id: Int): Result<User> = runCatching {
        api.getUser(id)
    }
}
