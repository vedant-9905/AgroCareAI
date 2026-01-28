package com.agrocareai.mobile.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

data class DiseaseInfo(
    val id: String,
    val name: String,
    val crop: String,
    val symptoms: String,
    val organic_cure: String,
    val chemical_cure: String,
    val prevention: String
)

class DiseaseRepository(private val context: Context) {

    fun getAllDiseases(): List<DiseaseInfo> {
        val jsonString = getJsonDataFromAsset("diseases.json") ?: return emptyList()
        val listType = object : TypeToken<List<DiseaseInfo>>() {}.type
        return Gson().fromJson(jsonString, listType)
    }

    fun getDiseaseByName(name: String): DiseaseInfo? {
        val all = getAllDiseases()
        // Simple fuzzy search (e.g., if AI says "Rice Leaf Blast", we find "Leaf Blast")
        return all.find { name.contains(it.name, ignoreCase = true) }
            ?: all.find { it.id == "healthy" } // Fallback
    }

    private fun getJsonDataFromAsset(fileName: String): String? {
        return try {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            null
        }
    }
}