package io.github.posaydone.filmix.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.posaydone.filmix.data.adapters.TranslationsAdapter
import io.github.posaydone.filmix.databinding.FragmentTranslationsDialogBinding
import io.github.posaydone.filmix.ui.viewmodels.PlayerViewModel

class TranslationsDialogFragment : BottomSheetDialogFragment() {
    private lateinit var sharedViewModel: PlayerViewModel
    private lateinit var binding: FragmentTranslationsDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(PlayerViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentTranslationsDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TranslationsAdapter(sharedViewModel)

        binding.translationsListView.adapter = adapter

        sharedViewModel.selectedEpisode.observe(viewLifecycleOwner) { episode ->
            episode?.translations?.let { adapter.updateTranslations(it) }
        }
    }
}