package com.example.netflixktl.model.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.netflixktl.model.Category
import com.example.netflixktl.model.Movie
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection
import kotlin.math.log

class CategoryTask(private val callback : Callback) {

    private val handler = Handler(Looper.getMainLooper())
    val executor = Executors.newSingleThreadExecutor()


    interface Callback{
        fun onPreExecute()
        fun onResult(categories: List<Category>)
        fun onFailure(message:String)
    }


    fun execute(url: String) {

        callback.onPreExecute()
        //nesse momento estamos ultilizando a UI-thrend normal

        executor.execute {

            var urlConnection:HttpsURLConnection?= null

            var buffer: BufferedInputStream?= null
            var stream:InputStream?= null

            try {
                val requestURL = URL(url) //abrir a a conexão
                 urlConnection = requestURL.openConnection() as HttpsURLConnection

                urlConnection.readTimeout = 2000//  tempo de leitura
                urlConnection.connectTimeout = 2000
                // tempo de conexão com servidor, quanto tempo levara para se conectar ao servidor

                val statusCode: Int = urlConnection.responseCode

                if (statusCode > 400) {
                    throw IOException("Erro na comunicação com servidor!")
                }
                // 1 forma simples e rapida
                 stream = urlConnection.inputStream // Sequencia de bytes

//                val jsonAsString =  stream.bufferedReader().use { it.readText() }


                // 2 forma

                buffer = BufferedInputStream(stream)

                val jsonAsString = toString(buffer)

                //JSON esta preparado para ser convertido e, um data class.

                val categories = toCategories(jsonAsString)

                Log.i("Teste",categories.toString())

                handler.post {
                    // Aqui dentro roda de Ui - thread
                    callback.onResult(categories)
                }





            } catch (e: IOException) {
                val message =  e.message ?: "Erro desconhecido "
                Log.e("Teste", message, e)

                handler.post {
                    callback.onFailure(message)
                }


            }finally {
                urlConnection?.disconnect()
                stream?.close()
                buffer?.close()

            }

        }

    }

    private fun toCategories(jsonAsString: String): List<Category> {
        val categories = mutableListOf<Category>()
//conversor de json para string
        val jsonRoot = JSONObject(jsonAsString)
        val jsonCategories = jsonRoot.getJSONArray("category")

        for (i in 0 until jsonCategories.length()) {

            val jsonCategory = jsonCategories.getJSONObject(i)

            val title = jsonCategory.getString("title")
            val jsonMovies = jsonCategory.getJSONArray("movie")



            val movies = mutableListOf<Movie>()
            for (j in 0 until jsonMovies.length()) {
                val jsonMovies = jsonMovies.getJSONObject(j)
                val id = jsonMovies.getInt("id")
                val coverUrl = jsonMovies.getString("cover_url")

                movies.add(Movie(id,coverUrl))
            }

            categories.add(Category(title,movies))

        }


        return categories
    }

    private fun toString(stream: InputStream): String {

        val bytes = ByteArray(1024)
        var read: Int
        val baos = ByteArrayOutputStream()

        while (true) {
            read = stream.read(bytes)

            if (read <= 0) {
                break
            }
            baos.write(bytes, 0, read)
        }
        return String(baos.toByteArray())
    }
}