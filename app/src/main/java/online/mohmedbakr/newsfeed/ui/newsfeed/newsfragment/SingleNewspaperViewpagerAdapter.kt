package online.mohmedbakr.newsfeed.ui.newsfeed.newsfragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import online.mohmedbakr.newsfeed.core.ConstantsName

class SingleNewspaperViewpagerAdapter(
    fragmentActivity: FragmentActivity,
) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount() = ConstantsName.newspaperNames.size

    override fun createFragment(position: Int): Fragment {
        return SingleNewspaperFragment.newInstance(position)
    }
}