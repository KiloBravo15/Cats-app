package com.am.catapp.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.am.catapp.models.Cat

class FirebaseDbCatsHelper : IDbCatsHelper {

    private val db = FirebaseFirestore.getInstance()
    private val catsCollection = db.collection("cats")

    override fun addCat(cat: Cat) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        val query = catsCollection
            .whereEqualTo("id", cat.id)
            .whereEqualTo("userId", userId)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // cat with the given ID and user ID exists
                    Log.d("Firebase", "cat already saved")
                } else {
                    // cat with the given ID and user ID does not exist
                    val catData = mapOf(
                        "id" to cat.id,
                        "imageUrl" to cat.imageUrl,
                        "breed" to cat.breed,
                        "userId" to userId
                    )

                    catsCollection.add(catData)
                        .addOnSuccessListener { documentReference ->
                            // cat added successfully
                            val catId = documentReference.id
                            // Perform any additional actions or UI updates
                            Log.i("Firebase", "$catId added successfully")
                        }
                        .addOnFailureListener { exception ->
                            Log.i("Firebase", exception.toString())
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.i("Firebase", exception.toString())
            }
    }

    override fun removeCat(catId: String) {
        val query = catsCollection
            .whereEqualTo("id", catId)
            .whereEqualTo("userId", FirebaseAuth.getInstance().currentUser?.uid)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                for (documentSnapshot in querySnapshot) {
                    documentSnapshot.reference.delete()
                        .addOnSuccessListener {
                            // cats removed successfully
                            Log.i("Firebase", "$catId removed successfully")
                        }
                        .addOnFailureListener { exception ->
                            Log.i("Firebase", exception.toString())
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.i("Firebase", exception.toString())
            }
    }

    override fun updateUserCat(catsSaved: MutableLiveData<List<Cat>>) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val catsCollection = FirebaseFirestore.getInstance().collection("cats")
            val query = catsCollection.whereEqualTo("userId", userId)

            query.get()
                .addOnSuccessListener { querySnapshot ->
                    val cats = mutableListOf<Cat>()

                    for (documentSnapshot in querySnapshot.documents) {
                        val cat = documentSnapshot.toObject(Cat::class.java)
                        cat?.let { cats.add(it) }
                    }

                    catsSaved.postValue(cats)
                    Log.d("Firebase", "cats saved")
                }
                .addOnFailureListener { exception ->
                    catsSaved.postValue(emptyList())
                    Log.d("Firebase", exception.toString())
                }
        } else {
            // User is not authenticated
            catsSaved.postValue(emptyList())
            Log.d("Firebase", "User is not authenticated")
        }
    }

    override fun runIfCatSaved(catId: String, lambdaFunction: () -> Unit, negativeLambdaFunction: () -> Unit) {
        val query = catsCollection
            .whereEqualTo("id", catId)
            .whereEqualTo("userId", FirebaseAuth.getInstance().currentUser?.uid)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // cat with the given ID and user ID does exist
                    lambdaFunction()
                } else {
                    negativeLambdaFunction()
                }
            }
            .addOnFailureListener { exception ->
                Log.i("Firebase", exception.toString())
            }
    }

}