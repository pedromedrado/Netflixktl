package com.example.netflixktl.model.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.netflixktl.model.Category
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class DowloadImageTask(private val callback: Callback) {

    private val handler = Handler(Looper.getMainLooper())
    var executor = Executors.newSingleThreadExecutor()


    interface Callback {
        fun onResult(bitmap: Bitmap)
    }

    fun execute(url: String) {
        executor.execute {

            var urlConnection: HttpsURLConnection? = null
            var stream: InputStream? = null

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

                val bitmap = BitmapFactory.decodeStream(stream)

                handler.post {
                    callback.onResult(bitmap)
                }


            } catch (e: IOException) {
                val message = e.message ?: "Erro desconhecido "
                Log.e("Teste", message, e)


            } finally {
                urlConnection?.disconnect()
                stream?.close()

            }

        }

    }

}

