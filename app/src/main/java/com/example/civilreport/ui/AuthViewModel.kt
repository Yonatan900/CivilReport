package com.example.civilreport.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.civilreport.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _signedOut = MutableLiveData<Unit>()
    val signedOut: LiveData<Unit> = _signedOut

    fun logout() {
        authRepository.logout()
        _signedOut.value = Unit
    }
}