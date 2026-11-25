package com.jeremye.dompetku

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jeremye.dompetku.databinding.ItemIncomeBinding

class AdapterIncome(private val listIncome: ArrayList<dcIncome>) :
    RecyclerView.Adapter<AdapterIncome.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: dcIncome)
        fun editData(pos: Int)
        fun delData(pos: Int)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val binding = ItemIncomeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ListViewHolder,
        position: Int
    ) {
        val income = listIncome[position]

        holder.binding.tvKategoriIncome.text = income.kategori
        holder.binding.tvNominalIncome.text = "Rp ${income.nominal}"
        holder.binding.tvTanggalIncome.text = income.tanggal
        holder.binding.tvKeteranganIncome.text =
            if (income.keterangan.isEmpty()) "-" else income.keterangan

        holder.itemView.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION &&
                ::onItemClickCallback.isInitialized
            ) {
                onItemClickCallback.onItemClicked(listIncome[currentPosition])
            }
        }

        holder.binding.btnEdit.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION &&
                ::onItemClickCallback.isInitialized
            ) {
                onItemClickCallback.editData(currentPosition)
            }
        }

        holder.binding.btnHapus.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION &&
                ::onItemClickCallback.isInitialized
            ) {
                onItemClickCallback.delData(currentPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return listIncome.size
    }

    inner class ListViewHolder(val binding: ItemIncomeBinding) :
        RecyclerView.ViewHolder(binding.root)
}