package io.github.posaydone.filmix.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import io.github.posaydone.filmix.R
import io.github.posaydone.filmix.data.adapters.SeasonPagerAdapter
import io.github.posaydone.filmix.databinding.FragmentEpisodesDialogBinding
import io.github.posaydone.filmix.ui.viewmodels.PlayerViewModel
import java.util.Objects


class EpisodesDialogFragment : BottomSheetDialogFragment() {
    private lateinit var sharedViewModel: PlayerViewModel
    private lateinit var binding: FragmentEpisodesDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(PlayerViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentEpisodesDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.seasons.observe(this) { seasons ->
            seasons?.let {
                val seasonPagerAdapter = SeasonPagerAdapter(requireActivity(), it)
                binding.pager.adapter = seasonPagerAdapter
                TabLayoutMediator(
                    binding.seasonTabs,
                    binding.pager
                ) { tab, position ->
                    tab.text =
                        getString(
                            R.string.season,
                            sharedViewModel.seasons.value!![position].season
                        )
                }.attach()

            }
        }
        sharedViewModel.selectedSeason.observe(this) { selectedSeason ->
            Objects.requireNonNull(binding.seasonTabs.getTabAt(selectedSeason?.season!!.minus(1)))
                .select()
        }
    }
}
