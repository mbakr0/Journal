package online.mohmedbakr.newsfeed.ui.bookmark

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import online.mohmedbakr.newsfeed.core.ArticleDTO
import online.mohmedbakr.newsfeed.core.ConstantsName
import online.mohmedbakr.newsfeed.data.database.Article
import online.mohmedbakr.newsfeed.source.ArticleLocalRepository

class BookmarkViewModel(private val articleLocalRepository: ArticleLocalRepository) : ViewModel() {

    val showNoData: MutableLiveData<Boolean> = MutableLiveData()

    fun getLiveData(): LiveData<List<Article>> = articleLocalRepository.getLiveDate()

    fun invalidateShowNoData(articleList: List<Article>) {
        showNoData.value = articleList.isEmpty()
    }

    fun saveArticle(articleDto: ArticleDTO, position: Int) {
        val newspaperName = ConstantsName.newspaperNames[position]
        viewModelScope.launch {
            articleLocalRepository.saveArticle(
                Article(
                    newspaperName = newspaperName,
                    title = articleDto.title,
                    url = articleDto.link,
                    publicationDate = articleDto.publicationDate,
                    imageLink = articleDto.imageLink
                )
            )
        }

    }

    fun deleteArticle(article: Article) =
        viewModelScope.launch {
            articleLocalRepository.deleteArticle(article)
        }

    fun deleteAllArticle() {
        viewModelScope.launch {
            articleLocalRepository.deleteAllArticles()
        }
    }


}