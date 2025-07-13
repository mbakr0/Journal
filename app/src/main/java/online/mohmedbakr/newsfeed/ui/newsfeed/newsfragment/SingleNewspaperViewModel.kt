package online.mohmedbakr.newsfeed.ui.newsfeed.newsfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import online.mohmedbakr.newsfeed.core.ArticleDTO
import online.mohmedbakr.newsfeed.core.Response
import online.mohmedbakr.newsfeed.core.SingleLiveEvent
import online.mohmedbakr.newsfeed.data.repository.FeedRepository

class SingleNewspaperViewModel(
    private val feedRepository: FeedRepository,
) : ViewModel() {
    val articleList: SingleLiveEvent<ArrayList<ArticleDTO>> by lazy {
        SingleLiveEvent()
    }
    val error: SingleLiveEvent<Int> by lazy {
        SingleLiveEvent()
    }

    private var categoryList: ArrayList<List<String>> = arrayListOf()
    private var job: Job = Job()

    fun getNews(position: Int) {
        loadLatestNews(position)
    }

    private fun loadLatestNews(position: Int) {

        job = viewModelScope.launch {
            val response = when (position) {
                0 -> {
                    if (categoryList.size == 4)
                        feedRepository.getMasrawyFeed(ArrayList(categoryList[0]))
                    else
                        feedRepository.getMasrawyFeed()
                }

                1 -> {
                    feedRepository.getRassdFeed()
                }

                2 -> {
                    feedRepository.getArabicBBCFeed()
                }

                3 -> {
                    feedRepository.getMadaMasr()
                }

                4 -> {
                    if (categoryList.size == 4)
                        feedRepository.getAlarabiyaFeed(ArrayList(categoryList[1]))
                    else
                        feedRepository.getAlarabiyaFeed()


                }

                5 -> {
                    if (categoryList.size == 4)
                        feedRepository.getAlhadathFeed(ArrayList(categoryList[2]))
                    else
                        feedRepository.getAlhadathFeed()

                }

                6 -> {
                    feedRepository.getAljazeeraFeed()
                }

                7 -> {
                    if (categoryList.size == 4)
                        feedRepository.getYoum7Feed(ArrayList(categoryList[3]))
                    else
                        feedRepository.getYoum7Feed()

                }

                else -> Response.Error(Response.NO_NEWSPAPER_SELECTED)
            }

            when (response) {
                is Response.Success -> {
                    val list = response.data
                    articleList.postValue(list)
                }

                is Response.Error -> {
                    error.postValue(response.errorType)
                }

            }

        }

    }

    fun setCategoryList(categoryList: java.util.ArrayList<List<String>>) {

        this.categoryList = categoryList
    }


}