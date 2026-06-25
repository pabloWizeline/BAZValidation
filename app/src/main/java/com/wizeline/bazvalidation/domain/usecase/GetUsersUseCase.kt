package com.wizeline.bazvalidation.domain.usecase

import com.wizeline.bazvalidation.domain.model.User
import com.wizeline.bazvalidation.domain.repository.UserRepository
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Result<List<User>> = repository.getUsers()
}
