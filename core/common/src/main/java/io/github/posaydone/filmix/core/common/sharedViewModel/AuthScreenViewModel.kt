package io.github.posaydone.filmix.core.common.sharedViewModel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.posaydone.filmix.core.model.SessionManager
import javax.inject.Inject

@HiltViewModel
class AuthScreenViewModel @Inject constructor(val sessionManager: SessionManager) : ViewModel()