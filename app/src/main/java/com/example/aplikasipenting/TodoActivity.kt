package com.example.aplikasipenting

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasipenting.adapter.TodoAdapter
import com.example.aplikasipenting.databinding.ActivityTodoBinding
import com.example.aplikasipenting.usecases.TodoUseCase
import kotlinx.coroutines.launch

class TodoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTodoBinding
    private lateinit var todoUseCase: TodoUseCase
    private lateinit var todoAdapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        todoUseCase = TodoUseCase()
//        siapkan kebutuhan recyclerciew terlebih dahulu
        setupRecyclerView()

//        inisiasi mengambil data dari firestore
        initializeData()

    }

    private fun setupRecyclerView() {
        todoAdapter = TodoAdapter(mutableListOf())
        binding.container.apply {
            adapter = todoAdapter
            layoutManager = LinearLayoutManager(this@TodoActivity)
        }
    }

    private fun initializeData() {
        lifecycleScope.launch {
            // sembunyikan tambilan recyclerview terlebih dahulu dan tampilkan ui loading
            binding.container.visibility = View.GONE
            binding.uiLoading.visibility = View.VISIBLE

            try {
                //  ambil data dariu firebase
                var todoList = todoUseCase.getTodo()

                // jika sudah mendapatkan data dan tidak ada error tampilkan kembali recyclerview dan sembunyikan ui loading
                binding.uiLoading.visibility = View.GONE
                binding.container.visibility = View.VISIBLE

                // update data yang ada di adapter
                todoAdapter.updateData(todoList)

            } catch (e: Exception) {
                Toast.makeText(this@TodoActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}