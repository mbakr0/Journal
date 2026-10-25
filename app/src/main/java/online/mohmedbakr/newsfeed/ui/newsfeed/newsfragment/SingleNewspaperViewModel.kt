
package online.mohmedbakr.newsfeed.ui.newsfeed.newsfragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import online.mohmedbakr.newsfeed.core.ArticleDTO
import online.mohmedbakr.newsfeed.core.Response
import online.mohmedbakr.newsfeed.data.repository.FeedRepository

class SingleNewspaperViewModel(
    private val feedRepository: FeedRepository,
) : ViewModel() {

    // Persistent UI state: the latest articles survive view recreation.
    val articleList = MutableLiveData<ArrayList<ArticleDTO>>()

    // Nullable so a previous error can be cleared when a new request starts.
    val error = MutableLiveData<Int?>()

    private var categoryList: ArrayList<List<String>> = arrayListOf()
    private var job: Job? = null

    fun getNews(position: Int) {
        job?.cancel()

        job = viewModelScope.launch {
            error.value = null

            try {
                val response = when (position) {
                    0 -> {
                        if (categoryList.size == 4)
                            feedRepository.getMasrawyFeed(
                                categoryList[0]
                            )
                        else
                            feedRepository.getMasrawyFeed()
                    }

                    1 -> feedRepository.getRassdFeed()

                    2 -> feedRepository.getArabicBBCFeed()

                    3 -> feedRepository.getMadaMasr()

                    4 -> {
                        if (categoryList.size == 4)
                            feedRepository.getAlarabiyaFeed(
                                categoryList[1]
                            )
                        else
                            feedRepository.getAlarabiyaFeed()
                    }

                    5 -> {
                        if (categoryList.size == 4)
                            feedRepository.getAlhadathFeed(
                                categoryList[2]
                            )
                        else
                            feedRepository.getAlhadathFeed()
                    }

                    6 -> feedRepository.getAljazeeraFeed()

                    7 -> {
                        if (categoryList.size == 4)
                            feedRepository.getYoum7Feed(
                                categoryList[3]
                            )
                        else
                            feedRepository.getYoum7Feed()
                    }

                    else -> Response.Error(
                        Response.NO_NEWSPAPER_SELECTED
                    )
                }

                when (response) {
                    is Response.Success -> {
                        articleList.value = response.data
                    }

                    is Response.Error -> {
                        error.value = response.errorType
                    }
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                error.value = Response.ERROR_CONNECT_TO_THE_HOST
            }
        }
    }

    fun setCategoryList(categoryList: java.util.ArrayList<List<String>>) {
        this.categoryList = categoryList
    }
}
