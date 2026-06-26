package com.wizeline.bazvalidation.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wizeline.bazvalidation.domain.model.User
import com.wizeline.bazvalidation.domain.usecase.GetUserUseCase
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

data class UserDetailState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null
)

sealed class UserDetailIntent {
    data class LoadUser(val userId: Int) : UserDetailIntent()
    data object GoBack : UserDetailIntent()
}

sealed class UserDetailEffect {
    data class ShowError(val message: String) : UserDetailEffect()
    data object NavigateBack : UserDetailEffect()
}

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(UserDetailState())
    val state: StateFlow<UserDetailState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<UserDetailEffect>()
    val effect: SharedFlow<UserDetailEffect> = _effect.asSharedFlow()

    fun handleIntent(intent: UserDetailIntent) {
        when (intent) {
            is UserDetailIntent.LoadUser -> loadUser(intent.userId)
            UserDetailIntent.GoBack -> goBack()
        }
    }

    private fun loadUser(userId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val user = getUserUseCase(userId).getOrThrow()
                _state.update { it.copy(isLoading = false, user = user) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
                _effect.emit(UserDetailEffect.ShowError(e.message ?: "Unknown error"))
            }
        }
    }

    private fun goBack() {
        viewModelScope.launch {
            _effect.emit(UserDetailEffect.NavigateBack)
        }
    }
}
