package io.github.posaydone.filmix.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckedTextView
import androidx.lifecycle.LiveData
import io.github.posaydone.filmix.presentation.ui.viewModel.PlayerViewModel

class TranslationsAdapter<T>(
    private val viewModel: PlayerViewModel,
    private var translations: List<T> = listOf(),
    private val selectedTranslationLiveData: LiveData<T?>,
    private val setTranslation: (T) -> Unit,
) : BaseAdapter() {
    private val TAG: String = "TranslationsAdapter"
    private var selectedTranslation: T? = null

    init {
        // Observe the selected translation in the ViewModel
        selectedTranslationLiveData.observeForever { translation ->
            val previousSelected = selectedTranslation
            selectedTranslation = translation

            // Update UI when the selected translation changes
            previousSelected?.let { notifyDataSetChanged() }
            translation?.let { notifyDataSetChanged() }
        }
    }

    override fun getCount(): Int = translations.size

    override fun getItem(position: Int): T = translations[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: CheckedTextView
        val holder: TranslationViewHolder<T>

        if (convertView == null) {
            view = LayoutInflater.from(parent.context)
                .inflate(
                    android.R.layout.simple_list_item_single_choice,
                    parent,
                    false
                ) as CheckedTextView
            holder = TranslationViewHolder(view)
            view.tag = holder
        } else {
            view = convertView as CheckedTextView
            holder = view.tag as TranslationViewHolder<T>
        }

        val translation = getItem(position)
        holder.bind(translation)

        view.isChecked = (translation == selectedTranslation)

        view.setOnClickListener {
            setTranslation(translation)
        }

        return view
    }

    fun updateTranslations(newTranslations: List<T>) {
        this.translations = newTranslations
        notifyDataSetChanged()
    }

    private class TranslationViewHolder<T>(private val view: View) {
        fun bind(translation: T) {
            (view as CheckedTextView).text = translation.toString()
        }
    }
}
