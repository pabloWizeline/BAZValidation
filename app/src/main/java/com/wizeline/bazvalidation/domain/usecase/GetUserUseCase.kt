package com.wizeline.bazvalidation.domain.usecase

import com.wizeline.bazvalidation.domain.model.User
import com.wizeline.bazvalidation.domain.repository.UserRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(id: Int): Result<User> = repository.getUser(id)
}
