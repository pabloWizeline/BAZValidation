package com.wizeline.bazvalidation.ui.detail

import com.wizeline.bazvalidation.domain.model.Address
import com.wizeline.bazvalidation.domain.model.Company
import com.wizeline.bazvalidation.domain.model.User
import com.wizeline.bazvalidation.domain.usecase.GetUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class UserDetailViewModelTest {

    private lateinit var getUserUseCase: GetUserUseCase
    private lateinit var viewModel: UserDetailViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val testUser = User(
        id = 1,
        name = "Leanne Graham",
        username = "Bret",
        email = "Sincere@april.biz",
        phone = "1-770-736-8031 x56442",
        website = "hildegard.org",
        company = Company("Romaguera-Crona", "Multi-layered client-server neural-net", "harness real-time e-markets"),
        address = Address("Kulas Light", "Apt. 556", "Gwenborough", "92998-3874")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getUserUseCase = mock()
        viewModel = UserDetailViewModel(getUserUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load user successfully updates state with user details`() = runTest {
        whenever(getUserUseCase(1)) doReturn Result.success(testUser)

        viewModel.handleIntent(UserDetailIntent.LoadUser(1))
        advanceUntilIdle()

        val state = viewModel.state.first { !it.isLoading }
        assertEquals(testUser, state.user)
        assertNull(state.error)
        assertFalse(state.isLoading)
    }

    @Test
    fun `load user with error updates state with error message`() = runTest {
        val errorMessage = "User not found"
        whenever(getUserUseCase(1)) doReturn Result.failure(IOException(errorMessage))

        viewModel.handleIntent(UserDetailIntent.LoadUser(1))
        advanceUntilIdle()

        val state = viewModel.state.first { !it.isLoading }
        assertNull(state.user)
        assertEquals(errorMessage, state.error)
        assertFalse(state.isLoading)
    }

    @Test
    fun `go back emits navigate back effect`() = runTest {
        viewModel.handleIntent(UserDetailIntent.GoBack)

        val effect = viewModel.effect.first { it is UserDetailEffect.NavigateBack }
        assertTrue(effect is UserDetailEffect.NavigateBack)
    }

    @Test
    fun `load different user replaces previous user data`() = runTest {
        val user1 = testUser
        val user2 = User(
            id = 2,
            name = "Ervin Howell",
            username = "Antonette",
            email = "Shanna@melissa.tv",
            phone = "010-692-6593 x09125",
            website = "anastasia.net",
            company = Company("Deckow-Crist", "Proactive didactic contingency", "synergize scalable supply-chains"),
            address = Address("Victor Plains", "Suite 879", "Wisokyburgh", "90566-7771")
        )
        whenever(getUserUseCase(1)) doReturn Result.success(user1)
        whenever(getUserUseCase(2)) doReturn Result.success(user2)

        viewModel.handleIntent(UserDetailIntent.LoadUser(1))
        advanceUntilIdle()
        assertEquals(user1, viewModel.state.value.user)

        viewModel.handleIntent(UserDetailIntent.LoadUser(2))
        advanceUntilIdle()
        assertEquals(user2, viewModel.state.value.user)
    }
}
