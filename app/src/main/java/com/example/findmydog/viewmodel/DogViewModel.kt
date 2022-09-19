package com.example.findmydog.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.findmydog.model.data.Dog
import com.example.findmydog.model.repository.DogRepository

class DogViewModel : ViewModel() {
    fun deleteDog(position: Int) {
        repository.deleteDog(position)
    }

    private val repository: DogRepository
    private val _allDogs = MutableLiveData<List<Dog>>()
    val allDog: LiveData<List<Dog>> = _allDogs

    init {
        repository = DogRepository().getInstance()
        repository.loadDogs(_allDogs)
    }
}