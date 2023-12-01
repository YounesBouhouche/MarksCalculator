package com.example.markscalculator

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.*

class Calculations(val context: Context) {
    companion object {
        val Context.dataStore by preferencesDataStore(name = "calculations")
        val CALCULATIONS_KEY = stringPreferencesKey("calculation")
    }
    suspend fun saveList(list: List<Calculation>) {
        context.dataStore.edit { preferences ->
            preferences[CALCULATIONS_KEY] = Gson().toJson(list)
        }
    }
    suspend fun clearList() {
        context.dataStore.edit { preferences ->
            preferences[CALCULATIONS_KEY] = Gson().toJson(listOf<Calculation>())
        }
    }
    val calculations: Flow<List<Calculation>> = context.dataStore.data.map { preferences ->
        if ((preferences[CALCULATIONS_KEY] ?: "") == "") listOf() else Gson().fromJson(preferences[CALCULATIONS_KEY] ?: "", (object : TypeToken<List<Calculation>>() {}).type)
    }
    suspend fun addItem(item: Calculation) {
        context.dataStore.edit { preferences ->
            val list = calculations.first().toMutableList()
            list.add(item)
            preferences[CALCULATIONS_KEY] = Gson().toJson(list)
        }
    }
    suspend fun removeItem(index: Int) {
        context.dataStore.edit { preferences ->
            val list = calculations.first().toMutableList()
            list.removeAt(index)
            preferences[CALCULATIONS_KEY] = Gson().toJson(list)
        }
    }
    suspend fun removeItem(item: Calculation) {
        context.dataStore.edit { preferences ->
            val list = calculations.first().toMutableList()
            list.remove(item)
            preferences[CALCULATIONS_KEY] = Gson().toJson(list)
        }
    }
    suspend fun replaceItem(index: Int, item: Calculation) {
        context.dataStore.edit { preferences ->
            val list = calculations.first().toMutableList()
            list[index] = item
            preferences[CALCULATIONS_KEY] = Gson().toJson(list)
        }
    }
    suspend fun renameItem(index: Int, newName: String) {
        context.dataStore.edit { preferences ->
            val list = calculations.first().toMutableList()
            list[index].name = newName
            preferences[CALCULATIONS_KEY] = Gson().toJson(list)
        }
    }
}