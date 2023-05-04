package online.mohmedbakr.newsfeed.ui.newsfeed.newsfragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class SingleNewspaperViewpagerAdapter(
    fragmentActivity: FragmentActivity,
) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount() = 8

    override fun createFragment(position: Int): Fragment {
        return SingleNewspaperFragment.newInstance(position)
    }
}