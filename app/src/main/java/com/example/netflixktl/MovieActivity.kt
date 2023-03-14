package com.example.netflixktl

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netflixktl.model.Movie
import com.example.netflixktl.model.MovieDetail
import com.example.netflixktl.model.util.DowloadImageTask
import com.example.netflixktl.model.util.MovieTask
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

class MovieActivity : AppCompatActivity(), MovieTask.Callback {

    private lateinit var txtTitle: TextView
    private lateinit var txtDesc: TextView
    private lateinit var txtCast: TextView
    private lateinit var adapter: MovieAdapter
    private lateinit var progress: ProgressBar
    private var movies = mutableListOf<Movie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)


        txtTitle = findViewById(R.id.movie_txt_title)
        txtDesc = findViewById(R.id.movie_txt_desc)
        txtCast = findViewById(R.id.movie_txt_cast)
        val rv: RecyclerView = findViewById(R.id.movie_rv_similar)
        progress  = findViewById(R.id.movie_progress)

        val id = intent?.getIntExtra("id", 0) ?: throw IllegalStateException("Id não encontrado")

        val url =
            "https://api.tiagoaguiar.co/netflixapp/movie/$id?apiKey=5b9bcf18-26cb-4367-b2aa-7b90384736a3"

        MovieTask(this).execute(url)



        adapter = MovieAdapter(movies, R.layout.movie_item_similar)
        rv.layoutManager = GridLayoutManager(this, 3)
        rv.adapter = adapter


        val toolbar: Toolbar = findViewById(R.id.movie_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null



    }


    override fun onPreExecute() {
        progress.visibility = View.VISIBLE
    }


    override fun onFailure(message: String) {
        progress.visibility = View.GONE
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onResult(movieDetail: MovieDetail) {
        progress.visibility = View.GONE

        txtTitle.text  = movieDetail.movie.title
        txtDesc.text = movieDetail.movie.desc
        txtCast.text = getString(R.string.cast, movieDetail.movie.cast)
        movies.clear()
        movies.addAll(movieDetail.similar)
        adapter.notifyDataSetChanged()

        DowloadImageTask(object: DowloadImageTask.Callback {
            override fun onResult(bitmap: Bitmap) {

                val layerDrawble: LayerDrawable =
                    ContextCompat.getDrawable(this@MovieActivity, R.drawable.shadows) as LayerDrawable

                //buscar o filme que eu quero
                val movieCover = BitmapDrawable(resources, bitmap)

                //Atribuir a esse layer list
                layerDrawble.setDrawableByLayerId(R.id.cover_drawble, movieCover)

                //set no imageView
                val coverImg: ImageView = findViewById(R.id.movie_img)
                coverImg.setImageDrawable(layerDrawble)

            }

        }).execute(movieDetail.movie.coverUrl)



    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}