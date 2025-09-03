package com.bonda.bonda.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel(){

    private val _navigateToLibraryTab = MutableLiveData<Event<Int>>()

    val navigateToLibraryTab: LiveData<Event<Int>> = _navigateToLibraryTab

    fun requestLibraryTab(position: Int) {
        _navigateToLibraryTab.value = Event(position)
    }

}