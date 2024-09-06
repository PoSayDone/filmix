package io.github.posaydone.filmix.presentation.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.github.posaydone.filmix.data.network.model.Category
import io.github.posaydone.filmix.ui.fragment.CategoryShowsListFragment

class CategoriesPagerAdapter(
    fragment: Fragment,
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = Category.values().size

    override fun createFragment(position: Int): Fragment {
        val category = Category.values()[position]
        return CategoryShowsListFragment.newInstance(category)
    }
}
