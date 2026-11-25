package com.jeremye.dompetku

import android.content.SharedPreferences
import android.os.Bundle
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
import com.jeremye.dompetku.databinding.ActivityKategoriBinding
import com.jeremye.dompetku.databinding.DialogKategoriBinding

class KategoriActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKategoriBinding
    private var _arKategori = ArrayList<dcKategori>()
    private lateinit var adapterKategori: AdapterKategori
    lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityKategoriBinding.inflate(layoutInflater)
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

        bacaDariSP()
        tampilkanData()

        binding.btnTambahKategori.setOnClickListener {
            showDialogTambahData()
        }
    }

    private fun bacaDariSP() {
        val gson = Gson()
        val isiSP = sp.getString("spKategori", null)
        val type = object : TypeToken<ArrayList<dcKategori>>() {}.type

        if (isiSP != null) {
            _arKategori = gson.fromJson(isiSP, type)
        }
    }

    private fun simpanKeSP() {
        val gson = Gson()
        val json = gson.toJson(_arKategori)

        sp.edit {
            putString("spKategori", json)
        }
    }

    private fun tampilkanData() {
        binding.rvKategori.layoutManager = LinearLayoutManager(this)
        adapterKategori = AdapterKategori(_arKategori)
        binding.rvKategori.adapter = adapterKategori

        adapterKategori.setOnItemClickCallback(object : AdapterKategori.OnItemClickCallback {
            override fun onItemClicked(data: dcKategori) {
                Toast.makeText(
                    this@KategoriActivity,
                    "${data.nama} (${data.jenis})",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun editData(pos: Int) {
                showDialogUbahData(pos)
            }

            override fun delData(pos: Int) {
                AlertDialog.Builder(this@KategoriActivity)
                    .setTitle("Konfirmasi HAPUS DATA")
                    .setMessage("Apakah benar kategori '${_arKategori[pos].nama}' akan dihapus?")
                    .setPositiveButton("Hapus") { _, _ ->
                        _arKategori.removeAt(pos)
                        simpanKeSP()
                        adapterKategori.notifyItemRemoved(pos)
                        adapterKategori.notifyItemRangeChanged(pos, _arKategori.size)
                        Toast.makeText(
                            this@KategoriActivity,
                            "Kategori berhasil dihapus",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }
        })
    }

    private fun showDialogTambahData() {
        val dialogBinding = DialogKategoriBinding.inflate(layoutInflater)

        AlertDialog.Builder(this)
            .setTitle("Tambah Kategori")
            .setView(dialogBinding.root)
            .setPositiveButton("Simpan") { _, _ ->
                val nama = dialogBinding.etNamaKategori.text.toString().trim()
                val jenis = if (dialogBinding.rbIncome.isChecked) "Income" else "Expense"

                if (nama.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Nama kategori wajib diisi!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                _arKategori.add(dcKategori(nama, jenis))
                simpanKeSP()
                adapterKategori.notifyItemInserted(_arKategori.size - 1)
                Toast.makeText(this, "Kategori ditambahkan", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showDialogUbahData(pos: Int) {
        val dialogBinding = DialogKategoriBinding.inflate(layoutInflater)
        val dataLama = _arKategori[pos]

        dialogBinding.etNamaKategori.setText(dataLama.nama)
        if (dataLama.jenis == "Income") {
            dialogBinding.rbIncome.isChecked = true
        } else {
            dialogBinding.rbExpense.isChecked = true
        }

        AlertDialog.Builder(this)
            .setTitle("Ubah Kategori")
            .setView(dialogBinding.root)
            .setPositiveButton("Simpan") { _, _ ->
                val namaBaru = dialogBinding.etNamaKategori.text.toString().trim()
                val jenisBaru = if (dialogBinding.rbIncome.isChecked) "Income" else "Expense"

                if (namaBaru.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Nama kategori wajib diisi!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                _arKategori[pos].nama = namaBaru
                _arKategori[pos].jenis = jenisBaru
                simpanKeSP()
                adapterKategori.notifyItemChanged(pos)
                Toast.makeText(this, "Kategori berhasil diubah", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}