package com.wizeline.bazvalidation.data.repository

import com.wizeline.bazvalidation.data.api.UserApi
import com.wizeline.bazvalidation.domain.model.Address
import com.wizeline.bazvalidation.domain.model.Company
import com.wizeline.bazvalidation.domain.model.User
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class UserRepositoryImplTest {

    private val user1 = User(
        id = 1,
        name = "Leanne Graham",
        username = "Bret",
        email = "Sincere@april.biz",
        phone = "1-770-736-8031 x56442",
        website = "hildegard.org",
        company = Company("Romaguera-Crona", "Multi-layered client-server neural-net", "harness real-time e-markets"),
        address = Address("Kulas Light", "Apt. 556", "Gwenborough", "92998-3874")
    )

    private val user2 = User(
        id = 2,
        name = "Ervin Howell",
        username = "Antonette",
        email = "Shanna@melissa.tv",
        phone = "010-692-6593 x09125",
        website = "anastasia.net",
        company = Company("Deckow-Crist", "Proactive didactic contingency", "synergize scalable supply-chains"),
        address = Address("Victor Plains", "Suite 879", "Wisokyburgh", "90566-7771")
    )

    @Test
    fun `getUser with id 2 returns user with id 1 due to bug`() = runTest {
        val api: UserApi = mock()
        whenever(api.getUser(1)) doReturn user1
        whenever(api.getUser(2)) doReturn user2

        val repository = UserRepositoryImpl(api)
        val result = repository.getUser(2)

        val user = result.getOrThrow()
        assertEquals(user2, user)
    }

    @Test
    fun `getUsers returns users with email and company swapped due to bug`() = runTest {
        val api: UserApi = mock()
        whenever(api.getUsers()) doReturn listOf(user1, user2)

        val repository = UserRepositoryImpl(api)
        val result = repository.getUsers()

        val users = result.getOrThrow()
        val first = users.first()
        assertEquals(user1.email, first.email)
        assertEquals(user1.company.name, first.company.name)
    }
}
