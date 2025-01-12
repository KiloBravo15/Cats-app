package com.am.catapp.api

import com.am.catapp.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CatApi {
    @GET("/v1/images/search")
    suspend fun searchImages(
        @Query("limit") limit: Int = Constants.LIMIT,
        @Query("has_breeds") hasBreeds: Int = Constants.HAS_BREEDS,
        @Query("api_key") apiKey: String = Constants.APIKEY,
    ): Response<List<CatImageSearchApiResponse>>

    @GET("/v1/breeds/search")
    suspend fun searchBreeds(
        @Query("q") name: String,
        @Query("attach_image") attachImage: Int = Constants.ATTACH_IMG,
        @Query("api_key") apiKey: String = Constants.APIKEY,
    ): Response<List<CatBreedSearchApiResponse>>
}