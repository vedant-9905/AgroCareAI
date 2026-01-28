package com.agrocareai.mobile.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.agrocareai.mobile.data.local.ScanResult
import com.agrocareai.mobile.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter : ListAdapter<ScanResult, HistoryAdapter.VH>(Diff()) {

    class VH(val b: ItemHistoryBinding) : RecyclerView.ViewHolder(b.root)

    class Diff : DiffUtil.ItemCallback<ScanResult>() {
        override fun areItemsTheSame(o: ScanResult, n: ScanResult) = o.id == n.id
        override fun areContentsTheSame(o: ScanResult, n: ScanResult) = o == n
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        // Using standard fields to avoid mismatch errors
        holder.b.txtDisease.text = item.diseaseName
        holder.b.txtConfidence.text = "${(item.confidence * 100).toInt()}%"
        holder.b.txtDate.text = SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(item.timestamp))
    }
}