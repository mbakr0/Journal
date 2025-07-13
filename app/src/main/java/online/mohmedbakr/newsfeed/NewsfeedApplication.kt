package online.mohmedbakr.newsfeed

import android.app.Application
import online.mohmedbakr.newsfeed.data.datasources.local.database.LocalDB
import online.mohmedbakr.newsfeed.data.repository.ArticleLocalRepository

class NewsfeedApplication : Application() {

    private val databaseDao by lazy { LocalDB.createArticleDao(this) }
    val articleLocalRepository by lazy { ArticleLocalRepository(databaseDao) }

}

