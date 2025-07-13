package online.mohmedbakr.newsfeed.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import online.mohmedbakr.newsfeed.data.repository.getCategory

class MainViewModel(dataStore: DataStore<Preferences>) : ViewModel() {
    val networkState: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    val categoryList = getCategory(dataStore)


    fun setNetworkState(state: Boolean) {
        networkState.postValue(state)
    }
}