package com.wizeline.bazvalidation.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wizeline.bazvalidation.domain.model.User
import com.wizeline.bazvalidation.domain.usecase.GetUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserListState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val error: String? = null
)

sealed class UserListIntent {
    data object LoadUsers : UserListIntent()
    data object Refresh : UserListIntent()
    data class SelectUser(val userId: Int) : UserListIntent()
    data object DismissError : UserListIntent()
}

sealed class UserListEffect {
    data class ShowError(val message: String) : UserListEffect()
    data class NavigateToDetail(val userId: Int) : UserListEffect()
}

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(UserListState())
    val state: StateFlow<UserListState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<UserListEffect>()
    val effect: SharedFlow<UserListEffect> = _effect.asSharedFlow()

    init {
        handleIntent(UserListIntent.LoadUsers)
    }

    fun handleIntent(intent: UserListIntent) {
        when (intent) {
            UserListIntent.LoadUsers,
            UserListIntent.Refresh -> loadUsers()
            is UserListIntent.SelectUser -> selectUser(intent.userId)
            UserListIntent.DismissError -> dismissError()
        }
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val users = getUsersUseCase().getOrThrow()
                _state.update { it.copy(isLoading = false, users = users) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
                _effect.emit(UserListEffect.ShowError(e.message ?: "Unknown error"))
            }
        }
    }

    private fun selectUser(userId: Int) {
        viewModelScope.launch {
            _effect.emit(UserListEffect.NavigateToDetail(userId))
        }
    }

    private fun dismissError() {
        _state.update { it.copy(error = null) }
    }
}
