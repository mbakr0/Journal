package online.mohmedbakr.newsfeed.ui.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import online.mohmedbakr.newsfeed.NewsfeedApplication
import online.mohmedbakr.newsfeed.R
import online.mohmedbakr.newsfeed.databinding.FragmentBookmarkBinding

class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookmarkViewModel by viewModels {
        BookmarkViewModelFactory((requireActivity().application as NewsfeedApplication).articleLocalRepository)
    }

    private lateinit var adapter: BookmarkRecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = BookmarkRecyclerView({
            view.findNavController()
                .navigate(BookmarkFragmentDirections.actionNavigationBookmarkToDetailsFragment(it))
        }) {
            viewModel.deleteArticle(it)
        }
        binding.savedArticles.adapter = adapter

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.savedArticles.addItemDecoration(dividerItemDecoration)

        viewModel.getLiveData().observe(viewLifecycleOwner) {
            viewModel.invalidateShowNoData(it)
            adapter.submitList(it)

        }

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu, menu)
            }

            override fun onPrepareMenu(menu: Menu) {
                menu.removeItem(R.id.navigation_bookmark)
                menu.removeItem(R.id.filter)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.deleteAllArticle)
                    viewModel.deleteAllArticle()
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}