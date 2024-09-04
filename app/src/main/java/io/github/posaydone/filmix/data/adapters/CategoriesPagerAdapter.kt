package io.github.posaydone.filmix.data.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.github.posaydone.filmix.data.model.Category
import io.github.posaydone.filmix.ui.fragments.ShowsListFragment

class CategoriesPagerAdapter(
    fragment: Fragment,
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = Category.values().size

    override fun createFragment(position: Int): Fragment {
        val category = Category.values()[position]
        return ShowsListFragment.newInstance(category)
    }
}
