package io.github.posaydone.filmix.data.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import io.github.posaydone.filmix.data.model.Translation
import io.github.posaydone.filmix.ui.viewmodels.PlayerViewModel

class TranslationsAdapter(
    private val viewModel: PlayerViewModel,
    private var translations: List<Translation> = listOf()
) : BaseAdapter() {
    private val TAG: String = "TranslationsAdapter"
    private var selectedTranslation: Translation? = null

    init {
        // Observe the selected translation in the ViewModel
        viewModel.selectedTranslation.observeForever { translation ->
            val previousSelected = selectedTranslation
            selectedTranslation = translation

            // Update UI when the selected translation changes
            previousSelected?.let { notifyDataSetChanged() }
            translation?.let { notifyDataSetChanged() }
        }
    }

    override fun getCount(): Int = translations.size

    override fun getItem(position: Int): Translation = translations[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: TranslationViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_single_choice, parent, false)
            holder = TranslationViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as TranslationViewHolder
        }

        val translation = getItem(position)
        holder.bind(translation)

        view.isSelected = (translation == selectedTranslation)

        view.setOnClickListener {
            viewModel.setTranslation(translation)
        }

        return view
    }

    fun updateTranslations(newTranslations: List<Translation>) {
        this.translations = newTranslations
        notifyDataSetChanged()
    }

    private class TranslationViewHolder(private val view: View) {
        fun bind(translation: Translation) {
            (view as TextView).text = translation.toString()
        }
    }
}
