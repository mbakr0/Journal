package online.mohmedbakr.newsfeed.ui.newsfeed.newsfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import online.mohmedbakr.newsfeed.source.FeedRepository

class SingleNewspaperViewModelFactory(
    private val repository: FeedRepository,
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SingleNewspaperViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SingleNewspaperViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}