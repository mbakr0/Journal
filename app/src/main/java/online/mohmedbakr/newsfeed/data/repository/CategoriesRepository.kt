package online.mohmedbakr.newsfeed.data.repository

import android.content.res.Resources
import online.mohmedbakr.newsfeed.core.NewspaperCategories

class CategoriesRepository(resources: Resources) {

    private val newspaperCategories = NewspaperCategories(resources)

    fun getMasrawy(): HashMap<String, HashMap<String, Int>> {
        return newspaperCategories.getMasrawyCategories()
    }

    fun getYoum7(): HashMap<String, Int> {
        return newspaperCategories.getYoum7Categories()
    }

    fun getAlhadath(): HashMap<String, Int> {
        return newspaperCategories.getAlhadathCategories()
    }

    fun getAlarabiya(): HashMap<String, String> {
        return newspaperCategories.getAlarabiyaCategories()
    }
}