package com.jeremye.dompetku

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jeremye.dompetku.databinding.ActivityIncomeBinding
import com.jeremye.dompetku.databinding.DialogIncomeBinding

class IncomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIncomeBinding
    private var _arIncome = ArrayList<dcIncome>()
    private var _arKategori = ArrayList<dcKategori>()
    private lateinit var adapterIncome: AdapterIncome
    lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        sp = getSharedPreferences("data_SP", MODE_PRIVATE)

        bacaKategoriDariSP()
        bacaIncomeDariSP()
        tampilkanData()

        binding.btnTambahIncome.setOnClickListener {
            showDialogTambahData()
        }
    }

    private fun bacaKategoriDariSP() {
        val gson = Gson()
        val isiSP = sp.getString("spKategori", null)
        val type = object : TypeToken<ArrayList<dcKategori>>() {}.type

        if (isiSP != null) {
            _arKategori = gson.fromJson(isiSP, type)
        }
    }

    private fun bacaIncomeDariSP() {
        val gson = Gson()
        val isiSP = sp.getString("spIncome", null)
        val type = object : TypeToken<ArrayList<dcIncome>>() {}.type

        if (isiSP != null) {
            _arIncome = gson.fromJson(isiSP, type)
        }
    }

    private fun simpanKeSP() {
        val gson = Gson()
        val json = gson.toJson(_arIncome)

        sp.edit {
            putString("spIncome", json)
        }
    }

    private fun getListKategoriIncome(): ArrayList<String> {
        val listNama = ArrayList<String>()
        for (item in _arKategori) {
            if (item.jenis == "Income") {
                listNama.add(item.nama)
            }
        }
        return listNama
    }

    private fun tampilkanData() {
        binding.rvIncome.layoutManager = LinearLayoutManager(this)
        adapterIncome = AdapterIncome(_arIncome)
        binding.rvIncome.adapter = adapterIncome

        adapterIncome.setOnItemClickCallback(object : AdapterIncome.OnItemClickCallback {
            override fun onItemClicked(data: dcIncome) {
                Toast.makeText(
                    this@IncomeActivity,
                    "${data.kategori}: Rp ${data.nominal}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun editData(pos: Int) {
                showDialogUbahData(pos)
            }

            override fun delData(pos: Int) {
                AlertDialog.Builder(this@IncomeActivity)
                    .setTitle("Konfirmasi HAPUS DATA")
                    .setMessage("Apakah benar pemasukan '${_arIncome[pos].kategori}' sebesar Rp ${_arIncome[pos].nominal} akan dihapus?")
                    .setPositiveButton("Hapus") { _, _ ->
                        _arIncome.removeAt(pos)
                        simpanKeSP()
                        adapterIncome.notifyItemRemoved(pos)
                        adapterIncome.notifyItemRangeChanged(pos, _arIncome.size)
                        Toast.makeText(
                            this@IncomeActivity,
                            "Pemasukan berhasil dihapus",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }
        })
    }

    private fun showDialogTambahData() {
        val listKategoriIncome = getListKategoriIncome()
        if (listKategoriIncome.isEmpty()) {
            Toast.makeText(
                this,
                "Belum ada kategori Income. Tambahkan dulu di Master Kategori!",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val dialogBinding = DialogIncomeBinding.inflate(layoutInflater)
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listKategoriIncome
        )
        dialogBinding.spKategoriIncome.adapter = spinnerAdapter

        AlertDialog.Builder(this)
            .setTitle("Tambah Pemasukan")
            .setView(dialogBinding.root)
            .setPositiveButton("Simpan") { _, _ ->
                val kategori = dialogBinding.spKategoriIncome.selectedItem.toString()
                val nominalText = dialogBinding.etNominalIncome.text.toString().trim()
                val tanggal = dialogBinding.etTanggalIncome.text.toString().trim()
                val keterangan = dialogBinding.etKeteranganIncome.text.toString().trim()

                if (nominalText.isEmpty() || tanggal.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Nominal dan tanggal wajib diisi!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                val nominal = nominalText.toLongOrNull()
                if (nominal == null || nominal <= 0) {
                    Toast.makeText(
                        this,
                        "Nominal harus berupa angka lebih dari 0!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                _arIncome.add(dcIncome(kategori, nominal, tanggal, keterangan))
                simpanKeSP()
                adapterIncome.notifyItemInserted(_arIncome.size - 1)
                Toast.makeText(this, "Pemasukan ditambahkan", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showDialogUbahData(pos: Int) {
        val listKategoriIncome = getListKategoriIncome()
        if (listKategoriIncome.isEmpty()) {
            Toast.makeText(
                this,
                "Belum ada kategori Income. Tambahkan dulu di Master Kategori!",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val dialogBinding = DialogIncomeBinding.inflate(layoutInflater)
        val dataLama = _arIncome[pos]

        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listKategoriIncome
        )
        dialogBinding.spKategoriIncome.adapter = spinnerAdapter

        val indexKategoriLama = listKategoriIncome.indexOf(dataLama.kategori)
        if (indexKategoriLama >= 0) {
            dialogBinding.spKategoriIncome.setSelection(indexKategoriLama)
        }

        dialogBinding.etNominalIncome.setText(dataLama.nominal.toString())
        dialogBinding.etTanggalIncome.setText(dataLama.tanggal)
        dialogBinding.etKeteranganIncome.setText(dataLama.keterangan)

        AlertDialog.Builder(this)
            .setTitle("Ubah Pemasukan")
            .setView(dialogBinding.root)
            .setPositiveButton("Simpan") { _, _ ->
                val kategoriBaru = dialogBinding.spKategoriIncome.selectedItem.toString()
                val nominalText = dialogBinding.etNominalIncome.text.toString().trim()
                val tanggalBaru = dialogBinding.etTanggalIncome.text.toString().trim()
                val keteranganBaru = dialogBinding.etKeteranganIncome.text.toString().trim()

                if (nominalText.isEmpty() || tanggalBaru.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Nominal dan tanggal wajib diisi!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                val nominalBaru = nominalText.toLongOrNull()
                if (nominalBaru == null || nominalBaru <= 0) {
                    Toast.makeText(
                        this,
                        "Nominal harus berupa angka lebih dari 0!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                _arIncome[pos].kategori = kategoriBaru
                _arIncome[pos].nominal = nominalBaru
                _arIncome[pos].tanggal = tanggalBaru
                _arIncome[pos].keterangan = keteranganBaru

                simpanKeSP()
                adapterIncome.notifyItemChanged(pos)
                Toast.makeText(this, "Pemasukan berhasil diubah", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}