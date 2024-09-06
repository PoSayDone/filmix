package io.github.posaydone.filmix.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import io.github.posaydone.filmix.databinding.FragmentPlayerSettingsDialogBinding
import io.github.posaydone.filmix.presentation.ui.adapter.QualitiesAdapter
import io.github.posaydone.filmix.presentation.ui.viewModel.PlayerViewModel

class PlayerSettingsDialogFragment : FullscreenBottomSheetDialogFragment() {
    private val viewModel: PlayerViewModel by activityViewModels()
    private lateinit var binding: FragmentPlayerSettingsDialogBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerSettingsDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = QualitiesAdapter(viewModel)
        binding.qualitiesListView.adapter = adapter

        viewModel.contentType.observe(requireActivity()) { contentType ->
            if (contentType == "movie") {
                viewModel.selectedMovieTranslation.observe(viewLifecycleOwner) { translation ->
                    translation?.files?.let { adapter.updateFiles(it) }
                }
            } else {
                viewModel.selectedTranslation.observe(viewLifecycleOwner) { translation ->
                    translation?.files?.let { adapter.updateFiles(it) }
                }
            }
        }
    }
}
