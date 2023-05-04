package online.mohmedbakr.newsfeed.source

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.map
import online.mohmedbakr.newsfeed.data.dataStore.PreferencesKey
import online.mohmedbakr.newsfeed.data.dataStore.getStoredCategory

fun getCategory(dataStore: DataStore<Preferences>) =
    getStoredCategory(dataStore).map { preferences ->
        val list: ArrayList<List<String>> = arrayListOf()
        val masrawy = preferences[PreferencesKey.masrawyFilter].toString().split("@").dropLast(1)
        val alarabiya =
            preferences[PreferencesKey.alarabiyaFilter].toString().split("@").dropLast(1)
        val alhadath = preferences[PreferencesKey.alhadathFilter].toString().split("@").dropLast(1)
        val youm7 = preferences[PreferencesKey.youm7Filter].toString().split("@").dropLast(1)

        list.add(0, masrawy)
        list.add(1, alarabiya)
        list.add(2, alhadath)
        list.add(3, youm7)

        list
    }

