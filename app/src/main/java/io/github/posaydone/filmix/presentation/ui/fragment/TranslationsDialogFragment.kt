package io.github.posaydone.filmix.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import io.github.posaydone.filmix.databinding.FragmentTranslationsDialogBinding
import io.github.posaydone.filmix.presentation.ui.adapter.TranslationsAdapter
import io.github.posaydone.filmix.presentation.ui.viewModel.PlayerViewModel

class TranslationsDialogFragment : FullscreenBottomSheetDialogFragment() {
    private val viewModel: PlayerViewModel by activityViewModels()
    private lateinit var binding: FragmentTranslationsDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentTranslationsDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.contentType.observe(requireActivity()) { contentType ->
            if (contentType == "movie") {
                val adapter =
                    TranslationsAdapter(
                        viewModel,
                        selectedTranslationLiveData = viewModel.selectedMovieTranslation,
                        setTranslation = { viewModel.setMovieTranslation(it) }
                    )

                binding.translationsListView.adapter = adapter

                viewModel.moviePieces.observe(viewLifecycleOwner) { translations ->
                    translations?.let { adapter.updateTranslations(it) }
                }

            } else {
                val adapter =
                    TranslationsAdapter(
                        viewModel,
                        selectedTranslationLiveData = viewModel.selectedTranslation,
                        setTranslation = { viewModel.setTranslation(it) }
                    )

                binding.translationsListView.adapter = adapter

                viewModel.selectedEpisode.observe(viewLifecycleOwner) { episode ->
                    episode?.translations?.let { adapter.updateTranslations(it) }
                }
            }
        }

    }
}