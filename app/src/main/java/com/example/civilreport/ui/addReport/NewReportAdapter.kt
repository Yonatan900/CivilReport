package com.example.civilreport.ui.addReport

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.civilreport.data.models.CardSize
import com.example.civilreport.data.models.ItemReportCard
import com.example.civilreport.databinding.ItemImageTextBinding
import kotlin.math.roundToInt
import androidx.core.net.toUri

class NewReportAdapter(
    private val onCardClick: (View, Int) -> Unit
) : ListAdapter<ItemReportCard, NewReportAdapter.CardHolder>(Diff) {

    inner class CardHolder(val bind: ItemImageTextBinding) :
        RecyclerView.ViewHolder(bind.root) {

        init {                                         // tap => open size menu
            bind.root.setOnClickListener {
                onCardClick(it, bindingAdapterPosition)
            }
        }

        fun bind(item: ItemReportCard) {
            val uri = item.uri.toUri()

            Glide.with(bind.ivPhoto.context)
                .load(uri)
                .centerCrop()
                .into(bind.ivPhoto)

            bind.tvCaption.text = item.caption

            val density = bind.root.resources.displayMetrics.density
            val hPx = when (item.size) {
                CardSize.SMALL     -> (200 * density).roundToInt()
                CardSize.MEDIUM    -> (420 * density).roundToInt()
                CardSize.FULL -> (842 * density).roundToInt()
            }
            bind.cardRoot.updateLayoutParams { height = hPx }
            bind.ivPhoto.updateLayoutParams { height = hPx }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CardHolder(
            ItemImageTextBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: CardHolder, position: Int) =
        holder.bind(getItem(position))


    fun onItemMove(from: Int, to: Int) {
        val mutable = currentList.toMutableList()
        val moved = mutable.removeAt(from)
        mutable.add(to, moved)
        submitList(mutable)
    }

    private object Diff : DiffUtil.ItemCallback<ItemReportCard>() {
        override fun areItemsTheSame(o: ItemReportCard, n: ItemReportCard) = o.uri == n.uri
        override fun areContentsTheSame(o: ItemReportCard, n: ItemReportCard) = o == n
    }
}


