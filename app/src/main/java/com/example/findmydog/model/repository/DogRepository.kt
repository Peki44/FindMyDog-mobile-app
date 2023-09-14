package com.example.findmydog.model.repository

import androidx.lifecycle.MutableLiveData
import com.example.findmydog.model.data.Dog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DogRepository {
    val uid = FirebaseAuth.getInstance().uid
    private var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
    private var listKey: MutableList<String> = mutableListOf()

    @Volatile
    private var INSTANCE: DogRepository? = null

    fun getInstance(): DogRepository {
        return INSTANCE ?: synchronized(this) {
            val instance = DogRepository()
            INSTANCE = instance
            instance
        }
    }

    fun loadDogs(dogList: MutableLiveData<List<Dog>>) {
        if (uid != null) {
            val itemList: ArrayList<Dog> = arrayListOf()
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    itemList.clear()
                    listKey.clear()
                    for (oneDog in snapshot.child(uid).child("Dogs").children) {
                        listKey.add(oneDog.key!!)
                        val emptyData = oneDog.getValue(Dog::class.java)
                        if (emptyData != null) {
                            itemList.add(emptyData)
                        }
                    }
                    dogList.value = itemList
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }

    }

    fun deleteDog(position: Int) {
        databaseReference.child(uid!!).child("Dogs").child(listKey[position]).removeValue()
    }
}