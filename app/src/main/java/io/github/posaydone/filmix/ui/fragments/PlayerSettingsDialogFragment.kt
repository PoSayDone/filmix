package io.github.posaydone.filmix.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.posaydone.filmix.data.adapters.QualitiesAdapter
import io.github.posaydone.filmix.databinding.FragmentPlayerSettingsDialogBinding
import io.github.posaydone.filmix.ui.viewmodels.PlayerViewModel

class PlayerSettingsDialogFragment : BottomSheetDialogFragment() {
    private lateinit var sharedViewModel: PlayerViewModel
    private lateinit var binding: FragmentPlayerSettingsDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(PlayerViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerSettingsDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = QualitiesAdapter(sharedViewModel)

        binding.qualitiesListView.adapter = adapter

        sharedViewModel.selectedTranslation.observe(viewLifecycleOwner) { translation ->
            translation?.files?.let { adapter.updateFiles(it) }
        }
    }
}
