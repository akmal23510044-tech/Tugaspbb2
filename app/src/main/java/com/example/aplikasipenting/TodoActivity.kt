package com.example.aplikasipenting

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasipenting.adapter.TodoAdapter
import com.example.aplikasipenting.databinding.ActivityTodoBinding
import com.example.aplikasipenting.entitiy.Todo
import com.example.aplikasipenting.usecases.TodoUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TodoActivity : AppCompatActivity() {

    private lateinit var adapter: TodoAdapter
    private lateinit var binding: ActivityTodoBinding
    private val todoUseCase = TodoUseCase()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerEvents()
        setupRecyclerView()

        // BARU PASANG KE RECYCLER VIEW
        binding.container.apply {
            layoutManager = LinearLayoutManager(this@TodoActivity)
            adapter = this@TodoActivity.adapter
        }
    }

    private fun registerEvents() {
        binding.TombolCreateTodo.setOnClickListener{
            val intent = Intent(this, CreateTodoActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loadTodos() {
        binding.uiLoading.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val todos = todoUseCase.getTodo()
                withContext(Dispatchers.Main) {
                    adapter.updateData(todos)
                    binding.uiLoading.visibility = View.GONE
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.uiLoading.visibility = View.GONE
                }
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = TodoAdapter(mutableListOf(), object : TodoAdapter.TodoItemEvents {

            override fun onDelete(todo: Todo) {
                val builder = AlertDialog.Builder(this@TodoActivity)
                builder.setTitle("Konfirmasi Hapus Data")
                builder.setMessage("Apakah kamu yakin ingin menghapus todo ini?")

                builder.setPositiveButton("Ya") { dialog, _ ->
                    lifecycleScope.launch {
                        try {
                            todoUseCase.deleteTodo(todo.id)
                            displayMessage("Data berhasil dihapus")
                            loadTodos() // refresh data
                        } catch (exc: Exception) {
                            displayMessage("Gagal menghapus data: ${exc.message}")
                        }
                    }
                    dialog.dismiss()
                }

                builder.setNegativeButton("Tidak") { dialog, _ ->
                    dialog.dismiss()
                }
                val  dialog = builder.create()
                dialog.show()
            }

            override fun onEdit(todo: Todo) {
                val intent = Intent( this@TodoActivity, EditTodoActivity::class.java)
                intent.putExtra("todo.item.id",todo.id)
                startActivity(intent)
            }
        })

    }


    private fun displayMessage(message: String) {
        Toast.makeText(this@TodoActivity, message, Toast.LENGTH_SHORT).show()
    }



    override fun onResume() {
        super.onResume()
        loadTodos()
    }

}