package com.example.civilreport.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.civilreport.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
){

    private val _authState = MutableLiveData<FirebaseUser?>().apply {
        value = auth.currentUser
    }

    private val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _authState.postValue(firebaseAuth.currentUser)
    }
    init {
        auth.addAuthStateListener(listener)
    }



    fun clearAuthListener() {
        auth.removeAuthStateListener(listener)
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun signIn(email: String, password: String): LiveData<Resource<FirebaseUser>> {
        val result = MutableLiveData<Resource<FirebaseUser>>().apply {
            value = Resource.loading()
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                authResult.user
                    ?.let { result.value = Resource.success(it) }
                    ?: run { result.value = Resource.error("Sign-in succeeded but no user returned") }
            }
            .addOnFailureListener { e ->
                result.value = Resource.error("Sign-in failed: ${e.message}")
            }

        return result
    }
//    suspend fun signIn(email: String, password: String): Resource<FirebaseUser> =
//        try {
//            val result = auth
//                .signInWithEmailAndPassword(email, password)
//                .await()
//            val user = result.user
//                ?: return Resource.error("No user returned")
//            Resource.success(user)
//        } catch (e: Exception) {
//            Resource.error("Sign-in failed: ${e.message}")
//        }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun logout() {
        auth.signOut()
    }


}