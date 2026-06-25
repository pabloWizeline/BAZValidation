package com.wizeline.bazvalidation.ui.list

import com.wizeline.bazvalidation.domain.model.Address
import com.wizeline.bazvalidation.domain.model.Company
import com.wizeline.bazvalidation.domain.model.User
import com.wizeline.bazvalidation.domain.usecase.GetUsersUseCase
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class UserListViewModelTest {

    private lateinit var getUsersUseCase: GetUsersUseCase
    private lateinit var viewModel: UserListViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val testUsers = listOf(
        User(
            id = 1,
            name = "Leanne Graham",
            username = "Bret",
            email = "Sincere@april.biz",
            phone = "1-770-736-8031 x56442",
            website = "hildegard.org",
            company = Company("Romaguera-Crona", "Multi-layered client-server neural-net", "harness real-time e-markets"),
            address = Address("Kulas Light", "Apt. 556", "Gwenborough", "92998-3874")
        ),
        User(
            id = 2,
            name = "Ervin Howell",
            username = "Antonette",
            email = "Shanna@melissa.tv",
            phone = "010-692-6593 x09125",
            website = "anastasia.net",
            company = Company("Deckow-Crist", "Proactive didactic contingency", "synergize scalable supply-chains"),
            address = Address("Victor Plains", "Suite 879", "Wisokyburgh", "90566-7771")
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getUsersUseCase = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load users successfully updates state with users list`() = runTest {
        whenever(getUsersUseCase()) doReturn Result.success(testUsers)

        viewModel = UserListViewModel(getUsersUseCase)
        advanceUntilIdle()

        val state = viewModel.state.first { !it.isLoading }
        assertEquals(testUsers, state.users)
        assertNull(state.error)
        assertFalse(state.isLoading)
    }

    @Test
    fun `load users with error updates state with error message`() = runTest {
        val errorMessage = "Network error"
        whenever(getUsersUseCase()) doReturn Result.failure(IOException(errorMessage))

        viewModel = UserListViewModel(getUsersUseCase)
        advanceUntilIdle()

        val state = viewModel.state.first { !it.isLoading }
        assertTrue(state.users.isEmpty())
        assertEquals(errorMessage, state.error)
        assertFalse(state.isLoading)
    }

    @Test
    fun `select user emits navigate to detail effect`() = runTest {
        val userId = 1
        whenever(getUsersUseCase()) doReturn Result.success(testUsers)

        viewModel = UserListViewModel(getUsersUseCase)
        advanceUntilIdle()

        viewModel.handleIntent(UserListIntent.SelectUser(userId))

        val effect = viewModel.effect.first { it is UserListEffect.NavigateToDetail }
        assertTrue(effect is UserListEffect.NavigateToDetail)
        assertEquals(userId, (effect as UserListEffect.NavigateToDetail).userId)
    }

    @Test
    fun `dismiss error clears error state`() = runTest {
        val errorMessage = "Error occurred"
        whenever(getUsersUseCase()) doReturn Result.failure(IOException(errorMessage))

        viewModel = UserListViewModel(getUsersUseCase)
        advanceUntilIdle()

        assertNotNull(viewModel.state.value.error)

        viewModel.handleIntent(UserListIntent.DismissError)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `refresh reloads users`() = runTest {
        whenever(getUsersUseCase()) doReturn Result.success(testUsers)

        viewModel = UserListViewModel(getUsersUseCase)
        advanceUntilIdle()

        val refreshedUsers = listOf(testUsers.first())
        whenever(getUsersUseCase()) doReturn Result.success(refreshedUsers)

        viewModel.handleIntent(UserListIntent.Refresh)
        advanceUntilIdle()

        val state = viewModel.state.first { !it.isLoading }
        assertEquals(refreshedUsers, state.users)
    }

    @Test
    fun `initial state loads users on creation`() = runTest {
        whenever(getUsersUseCase()) doReturn Result.success(testUsers)

        viewModel = UserListViewModel(getUsersUseCase)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(testUsers, state.users)
        assertNull(state.error)
        assertFalse(state.isLoading)
    }
}
