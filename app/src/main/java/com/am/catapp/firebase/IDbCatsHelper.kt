package com.am.catapp.firebase

import androidx.lifecycle.MutableLiveData
import com.am.catapp.models.Cat

interface IDbCatsHelper {

    fun addCat(cat: Cat)

    fun removeCat(catId: String)

    fun updateUserCat(catsSaved: MutableLiveData<List<Cat>>)

    fun runIfCatSaved(catId: String, lambdaFunction: () -> Unit, negativeLambdaFunction: () -> Unit)
}