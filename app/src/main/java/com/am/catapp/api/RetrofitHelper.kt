package com.am.catapp.api

import com.am.catapp.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitHelper : IApiCatHelper {

    private val baseUrl = Constants.BASE_GATEWAY_URL

    override fun getApi(): CatApi {
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CatApi::class.java)
    }
}