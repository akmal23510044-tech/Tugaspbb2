package com.example.aplikasipenting

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.aplikasipenting.databinding.ActivityCreateTodoBinding
import com.example.aplikasipenting.entitiy.Todo
import com.example.aplikasipenting.usecases.TodoUseCase
import kotlinx.coroutines.launch

class CreateTodoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateTodoBinding
    private lateinit var todoUseCase: TodoUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCreateTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        todoUseCase = TodoUseCase()
        registerEvents()
    }

    fun registerEvents() {
        binding.tombolTambah.setOnClickListener {
            saveDataToFirestore()
        }
    }

    private fun saveDataToFirestore() {
        val title = binding.title.text.toString().trim()
        val description = binding.description.text.toString().trim()

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Judul dan deskripsi tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }


        val todo = Todo(
            id = "",
            title = title,
            description = description
        )

        lifecycleScope.launch {
            try {
                todoUseCase.createTodo(todo)
                Toast.makeText(this@CreateTodoActivity, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                toTodoListActivity()
            } catch (e: Exception) {
                Toast.makeText(this@CreateTodoActivity, "Gagal menambahkan: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun toTodoListActivity() {
        val intent = Intent(this, TodoActivity::class.java)
        startActivity(intent)
        finish()
    }
}