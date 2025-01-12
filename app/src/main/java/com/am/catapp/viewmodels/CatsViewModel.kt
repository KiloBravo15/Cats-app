package com.am.catapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.am.catapp.api.CatApi
import com.am.catapp.api.CatBreedSearchApiResponse
import com.am.catapp.api.CatImageSearchApiResponse
import com.am.catapp.api.IApiCatHelper
import com.am.catapp.firebase.IDbCatsHelper
import com.am.catapp.models.Breed
import com.am.catapp.models.Cat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class CatsViewModel @Inject constructor(
    private val dbCatsHelper: IDbCatsHelper,
    private val apiCatHelper: IApiCatHelper
) : ViewModel() {

    private val _loadingState = MutableLiveData<Boolean>()
    private val catApi: CatApi by lazy {
        apiCatHelper.getApi()
    }

    val loadingState: LiveData<Boolean> = _loadingState
    val currentUser: MutableLiveData<FirebaseUser> by lazy {
        MutableLiveData<FirebaseUser>().apply {
            value = FirebaseAuth.getInstance().currentUser
        }
    }
    val randomCatImages: MutableLiveData<List<Cat>> by lazy {
        val catsLiveData = MutableLiveData<List<Cat>>()
        _loadingState.value = true
        loadRandomImages(catApi, _loadingState, catsLiveData)
        catsLiveData
    }
    val searchedBreeds = MutableLiveData<List<Cat>>()
    val savedBreeds = MutableLiveData<List<Cat>>()


    private fun loadRandomImages(
        catApi: CatApi,
        loadingState: MutableLiveData<Boolean>,
        catsLiveData: MutableLiveData<List<Cat>>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = catApi.searchImages()
                val cats = convertImagesResponseToModel(result)
                catsLiveData.postValue(cats)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                loadingState.postValue(false)
            }
        }
    }

    fun loadBreedsByName(
        name: String
    ) {
        _loadingState.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = catApi.searchBreeds(name)
                val breeds = convertBreedsResponseToModel(result)
                searchedBreeds.postValue(breeds)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loadingState.postValue(false)
            }
        }
    }

    fun runIfCatSaved(catId: String, lambdaFunction: () -> Unit, negativeLambdaFunction: () -> Unit) {
        dbCatsHelper.runIfCatSaved(catId, lambdaFunction, negativeLambdaFunction)
    }

    fun addCat(cat: Cat) {
        dbCatsHelper.addCat(cat)
    }

    fun removeCat(catId: String) {
        dbCatsHelper.removeCat(catId)
    }

    fun getUserCats() {
        dbCatsHelper.updateUserCat(savedBreeds)
    }



    private fun convertImagesResponseToModel(response: Response<List<CatImageSearchApiResponse>>): List<Cat> {
        Log.d("Api response", response.toString())
        return response.body()?.map { result ->
            Cat(
                result.id,
                result.url,
                result.breeds.map { item ->
                    Breed(item.id, item.name, item.alt_names ?: "", item.temperament, item.description, item.wikipedia_url ?: "https://wikipedia.org", null)
                }[0] // ?: are required, for some reason there might be null in the api response
            )
        } ?: emptyList()
    }

    private fun convertBreedsResponseToModel(response: Response<List<CatBreedSearchApiResponse>>): List<Cat> {
        Log.d("Api response", response.toString())

        return response.body()?.map { item ->
            Cat(
                "",
                null,
                Breed(item.id, item.name, item.alt_names ?: "", item.temperament, item.description, item.wikipedia_url ?: "https://wikipedia.org", item.image.url)
            )
        } ?: emptyList()
    }
}