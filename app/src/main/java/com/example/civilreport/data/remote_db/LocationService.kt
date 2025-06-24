package com.example.civilreport.data.remote_db

import com.example.civilreport.data.models.LocationIqInfo
import com.example.civilreport.util.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LocationService {
   //limit to 5 results and only for israel
   @GET(Constants.AUTO_COMPLETE_ENDPOINT)
   suspend fun autoComplete(
      @Query("q")     query: String,
   ): Response<List<LocationIqInfo>>

}