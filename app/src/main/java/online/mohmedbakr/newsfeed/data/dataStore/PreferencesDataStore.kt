package online.mohmedbakr.newsfeed.data.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.filterPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "filter"
)

object PreferencesKey {
    val masrawyFilter = stringPreferencesKey("masrawyFilter")
    val alarabiyaFilter = stringPreferencesKey("alarabiyaFilter")
    val alhadathFilter = stringPreferencesKey("alhadathFilter")
    val youm7Filter = stringPreferencesKey("youm7Filter")
}

fun getStoredCategory(dataStore: DataStore<Preferences>) = dataStore.data
