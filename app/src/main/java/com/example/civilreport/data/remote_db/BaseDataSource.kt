package com.example.civilreport.data.remote_db

import com.example.civilreport.util.Resource
import retrofit2.Response

abstract class BaseDataSource {
    protected suspend fun <T> getResult(
        call: suspend () -> Response<T>
    ): Resource<T> {

        try {
            val answer = call()
            if (answer.isSuccessful) {
                val body = answer.body()
                if (body != null) return Resource.success(body)
            }
            return Resource.error(
                "Network call has failed for the following reason: " +
                        "${answer.message()} ${answer.code()}"
            )
        } catch (e: Exception) {
            return Resource.error(
                "Network call has failed for the following reason: "
                        + (e.localizedMessage ?: e.toString())
            )
        }
    }
}