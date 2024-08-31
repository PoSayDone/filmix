package io.github.posaydone.filmix.data.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import io.github.posaydone.filmix.data.model.File
import io.github.posaydone.filmix.ui.viewmodels.PlayerViewModel

class QualitiesAdapter(
    private val viewModel: PlayerViewModel,
    private var files: List<File> = listOf()
) : BaseAdapter() {
    private val TAG: String = "TranslationsAdapter"
    private var selectedFile: File? = null

    init {
        // Observe the selected translation in the ViewModel
        viewModel.selectedQuality.observeForever { file ->
            val previousSelected = selectedFile
            selectedFile = file

            // Update UI when the selected translation changes
            previousSelected?.let { notifyDataSetChanged() }
            file?.let { notifyDataSetChanged() }
        }
    }

    override fun getCount(): Int = files.size

    override fun getItem(position: Int): File = files[position]

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

        val file = getItem(position)
        holder.bind(file)

        view.isSelected = (file == selectedFile)

        view.setOnClickListener {
            Log.d(TAG, "onClick: $file")
            viewModel.setQuality(file)
        }

        return view
    }

    fun updateFiles(newFiles: List<File>) {
        this.files = newFiles
        notifyDataSetChanged()
    }

    private class TranslationViewHolder(private val view: View) {
        fun bind(file: File) {
            (view as TextView).text = file.toString()
        }
    }
}
