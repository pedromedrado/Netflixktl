package com.example.netflixktl.model

data class MovieDetail(
    val movie : Movie,
    val similar: List<Movie>
)
