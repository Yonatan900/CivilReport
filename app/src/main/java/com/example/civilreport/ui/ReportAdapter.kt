package com.example.civilreport.ui
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.civilreport.data.models.ItemReport
import com.example.civilreport.databinding.ItemReportRowBinding
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportAdapter(
    private val listener: ReportItemListener
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    private val reports = mutableListOf<ItemReport>()

    inner class ReportViewHolder(
        private val binding: ItemReportRowBinding
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        fun bind(report: ItemReport) {
            binding.tvReportTitle.text = report.title
            val sdf = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())
            binding.tvReportDate.text = sdf.format((Date(report.timestamp)))
            Glide.with(binding.root.context)
                .load(report.mainImageUri)
                .circleCrop()
                .into(binding.imgReportIcon)
        }
        override fun onClick(v: View?) {
            listener.onReportClick(reports[adapterPosition].id)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemReportRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReportViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
       holder.bind(reports[position])
    }

    override fun getItemCount() = reports.size

    fun setReports(newList: List<ItemReport>) {
        reports.clear()
        reports.addAll(newList)
        notifyDataSetChanged()
    }

    interface ReportItemListener {
        fun onReportClick(reportId: String)
    }



}
