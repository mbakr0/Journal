package online.mohmedbakr.newsfeed.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import online.mohmedbakr.newsfeed.data.repository.getCategory

class MainViewModel(dataStore: DataStore<Preferences>) : ViewModel() {
    private val _networkState = MutableStateFlow(false)
    val networkState: StateFlow<Boolean> = _networkState.asStateFlow()

    val categoryList = getCategory(dataStore)

    fun setNetworkState(state: Boolean) {
        _networkState.value = state
    }
}
