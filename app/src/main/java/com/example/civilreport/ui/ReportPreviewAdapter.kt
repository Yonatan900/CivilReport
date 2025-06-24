package com.example.civilreport.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.civilreport.data.models.CardSize
import com.example.civilreport.data.models.ImageTextEntry
import com.example.civilreport.databinding.ItemImageTextBinding
import kotlin.math.roundToInt

class PreviewEntryAdapter :
    ListAdapter<ImageTextEntry, PreviewEntryAdapter.Holder>(Diff) {

    inner class Holder(private val bind: ItemImageTextBinding) :
        RecyclerView.ViewHolder(bind.root) {

        fun bind(item: ImageTextEntry) {
            Glide.with(bind.ivPhoto.context)
                .load(item.imageUri)
                .centerCrop()
                .into(bind.ivPhoto)
            val density = bind.root.resources.displayMetrics.density
            val hPx = when (item.imageSize) {
                CardSize.SMALL  -> (200 * density).roundToInt()
                CardSize.MEDIUM -> (420 * density).roundToInt()
                CardSize.FULL   -> (842 * density).roundToInt()
            }
            bind.cardRoot.updateLayoutParams { height = hPx }
            bind.ivPhoto.updateLayoutParams  { height = hPx }
        }
    }

    override fun onCreateViewHolder(p: ViewGroup, v: Int) =
        Holder(ItemImageTextBinding.inflate(LayoutInflater.from(p.context), p, false))

    override fun onBindViewHolder(h: Holder, pos: Int) = h.bind(getItem(pos))

    private object Diff : DiffUtil.ItemCallback<ImageTextEntry>() {
        override fun areItemsTheSame(o: ImageTextEntry, n: ImageTextEntry) = o.imageUri == n.imageUri
        override fun areContentsTheSame(o: ImageTextEntry, n: ImageTextEntry) = o == n
    }
}
