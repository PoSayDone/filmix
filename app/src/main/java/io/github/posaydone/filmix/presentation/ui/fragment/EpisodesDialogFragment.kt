package io.github.posaydone.filmix.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import io.github.posaydone.filmix.R
import io.github.posaydone.filmix.databinding.FragmentEpisodesDialogBinding
import io.github.posaydone.filmix.presentation.ui.adapter.SeasonPagerAdapter
import io.github.posaydone.filmix.presentation.ui.viewModel.PlayerViewModel
import java.util.Objects


class EpisodesDialogFragment : FullscreenBottomSheetDialogFragment() {
    private val viewModel: PlayerViewModel by activityViewModels()
    private lateinit var binding: FragmentEpisodesDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentEpisodesDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.seasons.observe(this) { seasons ->
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
                            viewModel.seasons.value!![position].season
                        )
                }.attach()

            }
        }
        viewModel.selectedSeason.observe(this) { selectedSeason ->
            Objects.requireNonNull(binding.seasonTabs.getTabAt(selectedSeason?.season!!.minus(1)))
                .select()
        }
    }
}
