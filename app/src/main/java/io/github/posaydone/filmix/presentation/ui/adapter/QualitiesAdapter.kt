package io.github.posaydone.filmix.presentation.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckedTextView
import io.github.posaydone.filmix.data.network.model.File
import io.github.posaydone.filmix.ui.viewModel.PlayerViewModel

class QualitiesAdapter(
    private val viewModel: PlayerViewModel,
    private var files: List<File> = listOf()
) : BaseAdapter() {
    private val TAG: String = "TranslationsAdapter"
    private var selectedFile: File? = null

    init {
        viewModel.selectedQuality.observeForever { file ->
            val previousSelected = selectedFile
            selectedFile = file

            previousSelected?.let { notifyDataSetChanged() }
            file?.let { notifyDataSetChanged() }
        }
    }

    override fun getCount(): Int = files.size

    override fun getItem(position: Int): File = files[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: CheckedTextView
        val holder: TranslationViewHolder

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
            holder = view.tag as TranslationViewHolder
        }


        val file = getItem(position)
        holder.bind(file)

        view.isChecked = (file == selectedFile)

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
            (view as CheckedTextView).text = file.toString()
        }
    }
}
