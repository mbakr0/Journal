package online.mohmedbakr.newsfeed

import android.app.Application
import online.mohmedbakr.newsfeed.data.database.LocalDB
import online.mohmedbakr.newsfeed.source.ArticleLocalRepository

class NewsfeedApplication : Application() {

    private val databaseDao by lazy { LocalDB.createArticleDao(this) }
    val articleLocalRepository by lazy { ArticleLocalRepository(databaseDao) }

}

