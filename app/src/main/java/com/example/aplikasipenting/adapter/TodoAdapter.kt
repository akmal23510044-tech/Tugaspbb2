package com.example.aplikasipenting.adapter

import android.annotation.SuppressLint
import android.service.autofill.Dataset
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasipenting.databinding.ItemTodoBinding
import com.example.aplikasipenting.entitiy.Todo

class TodoAdapter(
    private val dataset: MutableList<Todo>
) : RecyclerView.Adapter<TodoAdapter.CustomViewHolder>() {

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
        position: Int
    ) {
        val data = dataset[position]
        holder.bindData(data)
    }

    override fun getItemCount() = dataset.size

    /**
     * class custom view
     */
    inner class CustomViewHolder(val view: ItemTodoBinding)
        : RecyclerView.ViewHolder(view.root) {

        fun bindData(item: Todo) {
            view.title.text = item.title
            view.description.text = item.description
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<Todo>) {
        dataset.clear()
        dataset.addAll(newData)
        notifyDataSetChanged()
    }
}