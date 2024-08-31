package io.github.posaydone.filmix.data.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.github.posaydone.filmix.data.model.Season
import io.github.posaydone.filmix.ui.fragments.EpisodesListFragment

class SeasonPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val seasons: List<Season>

) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = seasons.size

    override fun createFragment(position: Int): Fragment {
        return EpisodesListFragment.newInstance(seasons[position])
    }
}
