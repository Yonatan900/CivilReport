package com.example.civilreport.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.example.civilreport.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser

import javax.inject.Inject
import com.example.civilreport.util.Resource
import com.example.civilreport.util.Success
import com.example.civilreport.util.Error
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginResult = MediatorLiveData<Resource<FirebaseUser>>()
    val loginResult: LiveData<Resource<FirebaseUser>> = _loginResult

    fun signIn(email: String, password: String) {

        _loginResult.value = Resource.loading()

        val source = authRepository.signIn(email, password)

        _loginResult.addSource(source) { resource ->
            _loginResult.value = resource

       if (resource.status is Success|| resource.status is Error) {
                _loginResult.removeSource(source)
            }
        }
    }

//    fun signIn(email: String, password: String) = viewModelScope.launch {
//        _loginResult.value = Resource.loading()
//        _loginResult.value = authRepository.signIn(email, password)
//    }

    fun isUserLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }

}