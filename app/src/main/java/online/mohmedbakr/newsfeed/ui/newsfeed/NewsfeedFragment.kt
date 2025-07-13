package online.mohmedbakr.newsfeed.ui.newsfeed

import android.app.ActionBar.LayoutParams
import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.divider.MaterialDivider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import online.mohmedbakr.newsfeed.R
import online.mohmedbakr.newsfeed.core.ConstantsName
import online.mohmedbakr.newsfeed.data.datasources.local.PreferencesKey
import online.mohmedbakr.newsfeed.data.datasources.local.filterPreferencesDataStore
import online.mohmedbakr.newsfeed.databinding.ChipGroupBinding
import online.mohmedbakr.newsfeed.databinding.FragmentNewsfeedBinding
import online.mohmedbakr.newsfeed.data.repository.CategoriesRepository
import online.mohmedbakr.newsfeed.ui.MainActivity
import online.mohmedbakr.newsfeed.ui.newsfeed.newsfragment.SingleNewspaperViewpagerAdapter


class NewsfeedFragment : Fragment() {

    private var _binding: FragmentNewsfeedBinding? = null
    private val binding get() = _binding!!

    private var _bindingChipGroup: ChipGroupBinding? = null
    private val bindingChipGroup get() = _bindingChipGroup!!

    private lateinit var category: CategoriesRepository

    private lateinit var filter: AlertDialog
    private lateinit var pager: ViewPager2
    private lateinit var menuHost: MenuHost

    private lateinit var newspaperName: TextView
    private lateinit var masrawyChipGroup: HashMap<TextView, ChipGroup>
    private lateinit var alarabiyaChipGroup: ChipGroup
    private lateinit var youm7ChipGroup: ChipGroup
    private lateinit var alhadathChipGroup: ChipGroup


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNewsfeedBinding.inflate(inflater, container, false)
        _bindingChipGroup = ChipGroupBinding.inflate(inflater, null, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val tabLayout = binding.tabLayout
        pager = binding.singleNewspaper
        createChipGroupFilter()
        filter = AlertDialog.Builder(activity)
            .setView(bindingChipGroup.root)
            .setOnDismissListener {
                removePreviousAlertLayout()
            }
            .setPositiveButton("Save") { _, _ ->
                when (pager.currentItem) {
                    0, 4, 5, 7 -> {
                        serializeFilter(pager.currentItem)
                    }
                }
            }
            .create()

        val adapter = SingleNewspaperViewpagerAdapter(requireActivity())
        pager.adapter = adapter

        TabLayoutMediator(tabLayout, pager) { tab, position ->
            tab.text = ConstantsName.newspaperNames[position]
        }.attach()

        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                pager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        menuHost = requireActivity()
        createMenu(view)

    }


    private fun createMenu(view: View) {
        menuHost.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu, menu)

            }

            override fun onPrepareMenu(menu: Menu) {
                menu.removeItem(R.id.deleteAllArticle)

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.navigation_bookmark -> {
                        val navController = view.findNavController()
                        navController.navigate(NewsfeedFragmentDirections.actionNavigationNewsfeedToNavigationBookmark())
                    }

                    R.id.filter -> {
                        showFilterDialog(pager.currentItem)
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun serializeFilter(currentNewspaper: Int) {
        var filterString = ""
        when (currentNewspaper) {
            0 -> {
                masrawyChipGroup.forEach { (_, chipGroup) ->
                    chipGroup.checkedChipIds.forEach { chipID ->
                        filterString += chipGroup.findViewById<Chip>(chipID).tag.toString() + "@"
                    }

                }
            }

            4 -> {
                alarabiyaChipGroup.checkedChipIds.forEach { chipID ->
                    filterString += alarabiyaChipGroup.findViewById<Chip>(chipID).tag.toString() + "@"
                }
            }

            5 -> {
                alhadathChipGroup.checkedChipIds.forEach { chipID ->
                    filterString += alhadathChipGroup.findViewById<Chip>(chipID).tag.toString() + "@"

                }
            }

            7 -> {
                youm7ChipGroup.checkedChipIds.forEach { chipID ->
                    filterString += youm7ChipGroup.findViewById<Chip>(chipID).tag.toString() + "@"


                }
            }
        }
        saveCheckedFilter(filterString, currentNewspaper)
    }

    private fun saveCheckedFilter(filterString: String, currentNewspaper: Int) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                requireContext().filterPreferencesDataStore.edit { preferences ->
                    when (currentNewspaper) {
                        0 -> preferences[PreferencesKey.masrawyFilter] = filterString
                        4 -> preferences[PreferencesKey.alarabiyaFilter] = filterString
                        5 -> preferences[PreferencesKey.alhadathFilter] = filterString
                        7 -> preferences[PreferencesKey.youm7Filter] = filterString
                    }
                }
            }
            withContext(Dispatchers.Main) {
                (activity as MainActivity).viewModel.apply {
                    setNetworkState(networkState.value!!)
                }
            }


        }

    }

    private fun createChipGroupFilter() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                category = CategoriesRepository(resources)
                newspaperName = TextView(context)
                val layoutParams = LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
                )

                layoutParams.gravity = Gravity.CENTER
                newspaperName.textSize = 25f
                newspaperName.layoutParams = layoutParams

                createMasrawyChipGroup()
                createAlarabiyaChipGroup()
                createYoum7ChipGroup()
                createAlhadathChipGroup()

            }
        }
    }

    private fun createAlarabiyaChipGroup() {
        alarabiyaChipGroup = ChipGroup(context)
        val layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
        alarabiyaChipGroup.layoutParams = layoutParams
        var count = 0
        category.getAlarabiya().forEach { (filterName, urlParam) ->
            val chipView = View.inflate(context, R.layout.chip, null) as Chip
            chipView.text = filterName
            chipView.tag = urlParam
            chipView.id = count++
            alarabiyaChipGroup.addView(chipView)
        }


    }


    private fun createMasrawyChipGroup() {
        masrawyChipGroup = HashMap()
        var count = 0
        category.getMasrawy().forEach { (categoryName, filterMap) ->
            val layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            val chipGroup = ChipGroup(context)
            val textView = TextView(context)
            textView.text = categoryName
            textView.textSize = 20f
            textView.layoutParams = layoutParams
            chipGroup.layoutParams = layoutParams

            filterMap.forEach { (filterName, urlParam) ->
                val chipView = View.inflate(context, R.layout.chip, null) as Chip
                chipView.text = filterName
                chipView.tag = urlParam
                chipView.id = count++
                chipGroup.addView(chipView)
            }
            masrawyChipGroup[textView] = chipGroup
        }
    }

    private fun createAlhadathChipGroup() {
        alhadathChipGroup = ChipGroup(context)
        val layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )

        alhadathChipGroup.layoutParams = layoutParams
        var count = 0
        category.getAlhadath().forEach { (filterName, urlParam) ->
            val chipView = View.inflate(context, R.layout.chip, null) as Chip
            chipView.text = filterName
            chipView.tag = urlParam
            chipView.id = count++
            alhadathChipGroup.addView(chipView)
        }

    }

    private fun createYoum7ChipGroup() {
        youm7ChipGroup = ChipGroup(context)
        val layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )

        youm7ChipGroup.layoutParams = layoutParams
        var count = 0
        category.getYoum7().forEach { (filterName, urlParam) ->

            val chipView = View.inflate(context, R.layout.chip, null) as Chip
            chipView.text = filterName
            chipView.tag = urlParam
            chipView.id = count++
            youm7ChipGroup.addView(chipView)
        }
    }

    private fun showFilterDialog(currentItem: Int) {
        val name = ConstantsName.newspaperNames[currentItem]
        when (currentItem) {
            0 -> {
                addNewspaperName(name)
                showMasrawyDialog()
            }

            4 -> {
                addNewspaperName(name)
                showAlarabiyaDialog()
            }

            5 -> {
                addNewspaperName(name)
                showAlhadathDialog()
            }

            7 -> {
                addNewspaperName(name)
                showYoum7Dialog()
            }

            else -> {
                Snackbar.make(
                    requireContext(),
                    binding.root,
                    "$name  ليس لديه اقسام  ",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }


    }

    private fun addNewspaperName(name: String) {
        newspaperName.text = name
        bindingChipGroup.chipGroupLayout.addView(newspaperName)
    }

    private fun showAlarabiyaDialog() {
        bindingChipGroup.chipGroupLayout.addView(alarabiyaChipGroup)
        filter.show()
    }

    private fun showYoum7Dialog() {
        bindingChipGroup.chipGroupLayout.addView(youm7ChipGroup)
        filter.show()
    }


    private fun showAlhadathDialog() {
        bindingChipGroup.chipGroupLayout.addView(alhadathChipGroup)
        filter.show()
    }

    private fun showMasrawyDialog() {
        masrawyChipGroup.forEach { (textView, chipGroup) ->
            bindingChipGroup.chipGroupLayout.addView(textView)
            bindingChipGroup.chipGroupLayout.addView(chipGroup)
            val divider = MaterialDivider(requireContext())
            val layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                5
            )
            divider.layoutParams = layoutParams
            bindingChipGroup.chipGroupLayout.addView(divider)

        }
        filter.show()
    }

    private fun removePreviousAlertLayout() {
        bindingChipGroup.chipGroupLayout.removeAllViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}

