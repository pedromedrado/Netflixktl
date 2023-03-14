package com.example.netflixktl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netflixktl.model.Category
import com.example.netflixktl.model.Movie


class CategoryAdapter(
    private val categories: List<Category>,
    private val onItemClickListener: (Int) -> Unit
) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)

    }

    override fun getItemCount(): Int {
        return categories.size
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(category: Category) {
            val txTitle: TextView = itemView.findViewById(R.id.txt_title1)
            txTitle.text = category.nome
            val rvCategory: RecyclerView = itemView.findViewById(R.id.rv_category1)

            rvCategory.layoutManager =
                LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
            rvCategory.adapter = MovieAdapter(category.movies, R.layout.movie_item,onItemClickListener)


        }
    }

}