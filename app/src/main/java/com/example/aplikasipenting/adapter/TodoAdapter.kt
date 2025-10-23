package com.example.aplikasipenting.adapter

import android.annotation.SuppressLint
import android.service.autofill.Dataset
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasipenting.databinding.ItemTodoBinding
import com.example.aplikasipenting.entitiy.Todo

class TodoAdapter(
    private val dataset: MutableList<Todo>,
    private val events: TodoItemEvents
) : RecyclerView.Adapter<TodoAdapter.CustomViewHolder>() {


    interface TodoItemEvents {
        fun onDelete(todo: Todo)
        fun onEdit(todo: Todo)
    }


    inner class CustomViewHolder(val view: ItemTodoBinding)
        : RecyclerView.ViewHolder(view.root) {

        fun bindData(data: Todo) {
            view.title.text = data.title
            view.description.text = data.description

            //eh ketika misal ada user yang click item todo tolong kasih tau aku
            view.root.setOnLongClickListener {
                events.onDelete(data)
                true
            }

            view.root.setOnClickListener{
                events.onEdit(data)
            }

        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomViewHolder {
        val binding = ItemTodoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CustomViewHolder,
        index: Int
    ) {
        val data = dataset[index]
        holder.bindData(data)
    }

    override fun getItemCount() = dataset.size

    /**
     * class custom view
     */


    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<Todo>) {
        dataset.clear()
        dataset.addAll(newData)
        notifyDataSetChanged()
    }
}