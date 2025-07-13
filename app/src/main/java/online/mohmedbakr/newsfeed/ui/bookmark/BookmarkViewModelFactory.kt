package online.mohmedbakr.newsfeed.ui.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import online.mohmedbakr.newsfeed.data.repository.ArticleLocalRepository

class BookmarkViewModelFactory(private val repository: ArticleLocalRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookmarkViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookmarkViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}