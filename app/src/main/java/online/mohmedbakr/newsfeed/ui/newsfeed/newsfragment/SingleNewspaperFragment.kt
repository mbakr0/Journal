package online.mohmedbakr.newsfeed.ui.newsfeed.newsfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import online.mohmedbakr.newsfeed.NewsfeedApplication
import online.mohmedbakr.newsfeed.R
import online.mohmedbakr.newsfeed.core.Response
import online.mohmedbakr.newsfeed.databinding.FragmentSingleNewspaperBinding
import online.mohmedbakr.newsfeed.data.datasources.remote.ParseRssFeedObject
import online.mohmedbakr.newsfeed.data.repository.FeedRepository
import online.mohmedbakr.newsfeed.ui.MainActivity
import online.mohmedbakr.newsfeed.ui.MainViewModel
import online.mohmedbakr.newsfeed.ui.bookmark.BookmarkViewModel
import online.mohmedbakr.newsfeed.ui.bookmark.BookmarkViewModelFactory
import online.mohmedbakr.newsfeed.ui.newsfeed.NewsfeedFragmentDirections

private const val ARG_PARAM1 = "param1"

class SingleNewspaperFragment : Fragment() {

    private var _binding: FragmentSingleNewspaperBinding? = null
    private val binding get() = _binding!!
    private val parseRssFeed = ParseRssFeedObject.createRssObject()
    private val feedRepository = FeedRepository(parseRssFeed)
    private val viewModel: SingleNewspaperViewModel by viewModels {
        SingleNewspaperViewModelFactory(feedRepository)
    }

    private val bookmarkViewModel: BookmarkViewModel by viewModels {
        BookmarkViewModelFactory((requireActivity().application as NewsfeedApplication).articleLocalRepository)
    }

    private var position: Int = 0
    private lateinit var adapter: FeedRecyclerView
    private lateinit var mainViewModel: MainViewModel
    private lateinit var observer: Observer<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            position = it.getInt(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSingleNewspaperBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel = (activity as MainActivity).viewModel
        val progressColor = (activity as MainActivity).progressColor

        val navController = findNavController()


        val directions = NewsfeedFragmentDirections

        adapter = FeedRecyclerView(
            progressColor,
            {
                navController.navigate(directions.actionNavigationNewsfeedToDetailsFragment(it))
            }
        ) {

            bookmarkViewModel.saveArticle(it, position)
            val text = resources.getString(R.string.article_saved)
            val toastTextColor = R.color.primary
            Toast.makeText(
                activity,

                HtmlCompat.fromHtml(
                    "<font color='${
                        resources.getColor(
                            toastTextColor,
                            requireActivity().theme
                        )
                    }'>$text</font>",
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                ),

                Toast.LENGTH_SHORT
            ).show()
        }
        binding.feedRecyclerView.adapter = adapter

        binding.swipeToRefresh.setOnRefreshListener {
            (activity as MainActivity).viewModel.apply {
                networkState.value?.let { setNetworkState(it) }
            }
            binding.swipeToRefresh.isRefreshing = false
        }

        observer = Observer {
            when (it) {
                true -> {
                    getNews()
                }

                false -> {
                    viewModel.error.value = Response.NO_INTERNET_CONNECTION
                }
            }
        }
        mainViewModel.networkState.observe(viewLifecycleOwner, observer)
        lifecycleScope.launch {
            mainViewModel.categoryList.collectLatest { list ->
                viewModel.setCategoryList(list)
            }
        }


        viewModel.articleList.observe(viewLifecycleOwner) { list ->
            binding.shimmerViewContainer.stopShimmer()
            binding.shimmerViewContainer.visibility = View.GONE
            binding.noInternet.noInternet.visibility = View.GONE
            adapter.submitList(list)


        }



        viewModel.error.observe(viewLifecycleOwner) {
            if (viewModel.articleList.value == null || viewModel.articleList.value!!.isEmpty()) {
                binding.shimmerViewContainer.stopShimmer()
                binding.shimmerViewContainer.visibility = View.GONE
                binding.noInternet.noInternet.visibility = View.VISIBLE

                if (it == Response.ERROR_CONNECT_TO_THE_HOST)
                    binding.noInternet.errorText.text = Response.ERROR_CONNECT_TO_THE_HOST_MESSAGE
            }

        }

    }

    private fun getNews() {
        binding.noInternet.noInternet.visibility = View.GONE
        binding.shimmerViewContainer.visibility = View.VISIBLE
        binding.shimmerViewContainer.startShimmer()
        viewModel.getNews(position)
    }


    override fun onResume() {
        super.onResume()

        if (!binding.shimmerViewContainer.isShimmerStarted)
            binding.shimmerViewContainer.startShimmer()
    }

    companion object {
        @JvmStatic
        fun newInstance(position: Int) =
            SingleNewspaperFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, position)
                }
            }
    }


}