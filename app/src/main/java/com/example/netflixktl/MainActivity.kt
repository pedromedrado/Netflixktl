package com.example.netflixktl

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netflixktl.model.Category
import com.example.netflixktl.model.Movie
import com.example.netflixktl.model.util.CategoryTask

class MainActivity : AppCompatActivity(), CategoryTask.Callback {

    private lateinit var progress:ProgressBar
    private lateinit var adapter : CategoryAdapter
    val categories = mutableListOf<Category>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progress = findViewById(R.id.progress_main)


        adapter = CategoryAdapter(categories) { id->

            val intent = Intent(this@MainActivity,MovieActivity::class.java)
            intent.putExtra("id",id)
            startActivity(intent)
        }


        val rv: RecyclerView = findViewById(R.id.rv_main)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        CategoryTask(this).execute("https://api.tiagoaguiar.co/netflixapp/home?apiKey=5b9bcf18-26cb-4367-b2aa-7b90384736a3")
    }

    override fun onPreExecute() {
    progress.visibility = View.VISIBLE

    }

    //aqui sera quando o Categorytask Chamara de volta
    //callback
    override fun onResult(categories: List<Category>) {
        this.categories.clear()
        this.categories.addAll(categories)
        adapter.notifyDataSetChanged()// Forçando o adapter a fazer uma nova chamada

        progress.visibility = View.GONE
    }

    override fun onFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        progress.visibility = View.GONE
    }


}