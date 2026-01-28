package com.agrocareai.mobile.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

// The Data Structure
data class DiseaseInfo(
    val id: String, val name: String, val crop: String,
    val symptoms: String, val organic_cure: String,
    val chemical_cure: String, val prevention: String
)

class DiseaseRepository(private val context: Context) {

    // Simple function to read the local file
    fun getDiseaseDetails(name: String): DiseaseInfo? {
        val jsonString = try {
            context.assets.open("diseases.json").bufferedReader().use { it.readText() }
        } catch (e: IOException) { return null }

        val type = object : TypeToken<List<DiseaseInfo>>() {}.type
        val list: List<DiseaseInfo> = Gson().fromJson(jsonString, type)

        return list.find { name.contains(it.name, ignoreCase = true) }
            ?: list.find { it.id == "healthy" }
    }
}