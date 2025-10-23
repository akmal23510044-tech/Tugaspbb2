package com.example.aplikasipenting

import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.aplikasipenting.databinding.ActivityEditTodoBinding
import com.example.aplikasipenting.entitiy.Todo
import com.example.aplikasipenting.usecases.TodoUseCase
import kotlinx.coroutines.launch

class EditTodoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditTodoBinding
    private lateinit var todoItemId: String
    private lateinit var todoUseCase: TodoUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inisialisasi ViewBinding
        binding = ActivityEditTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Atur padding agar tidak tertutup status bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ambil ID todo dari Intent
        todoItemId = intent.getStringExtra("todo.item.id") ?: ""

        // Inisialisasi use case
        todoUseCase = TodoUseCase()

        loadTodo()
        registerEvent()


    }

    fun loadTodo() {
        lifecycleScope.launch {
            val data = todoUseCase.getTodo(todoItemId)

            if (data == null) {
                // Jika data tidak ditemukan, kembali ke TodoActivity
                val intent = Intent(this@EditTodoActivity, TodoActivity::class.java)
                startActivity(intent)
                displayMessage("Data task yang akan di edit tidak tersedia di server")
                back()
                finish()

            }

            binding.title.setText(data?.title)
            binding.description.setText(data?.description)

        }
    }

    fun back() {
        val intent = Intent (this, TodoActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun displayMessage(message: String){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }


    override fun onStart() {
        super.onStart()
        loadTodo()
    }

    fun registerEvent(){
        binding.tombolUpdate.setOnClickListener{
            lifecycleScope.launch {
                val title = binding.title.text.toString()
                val description = binding.description.text.toString()
                val payload = Todo (
                    id = todoItemId,
                    title = title,
                    description = description
                )

                try {
                    todoUseCase.updateTodo(payload)
                    displayMessage("Berhasil memperbarui data")
                    back()
                }catch (exc: Exception){
                    displayMessage("Gagal memperbarui data task :${exc.message}")
                }
            }
        }
    }
}




