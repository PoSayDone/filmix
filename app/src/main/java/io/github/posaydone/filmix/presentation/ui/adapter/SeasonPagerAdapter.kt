package io.github.posaydone.filmix.presentation.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.github.posaydone.filmix.data.network.model.Season
import io.github.posaydone.filmix.ui.fragment.EpisodesListFragment

class SeasonPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val seasons: List<Season>

) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = seasons.size

    override fun createFragment(position: Int): Fragment {
        return EpisodesListFragment.newInstance(seasons[position])
    }
}
