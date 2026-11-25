package com.jeremye.dompetku

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jeremye.dompetku.databinding.ItemKategoriBinding

class AdapterKategori(private val listKategori: ArrayList<dcKategori>) :
    RecyclerView.Adapter<AdapterKategori.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: dcKategori)
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
        val binding = ItemKategoriBinding.inflate(
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
        val kategori = listKategori[position]

        holder.binding.tvNamaKategori.text = kategori.nama
        holder.binding.tvJenisKategori.text = kategori.jenis

        holder.itemView.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION &&
                ::onItemClickCallback.isInitialized
            ) {
                onItemClickCallback.onItemClicked(listKategori[currentPosition])
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
        return listKategori.size
    }

    inner class ListViewHolder(val binding: ItemKategoriBinding) :
        RecyclerView.ViewHolder(binding.root)
}