package com.example.applisae

import kotlinx.serialization.Serializable

// Ce modèle représente la structure de la réponse JSON renvoyée par l'API TheMovieDB
// lors des appels pour récupérer une liste de films (par titre, genre, etc.).

@Serializable
data class MovieResponse(val results: List<Movie>) // Liste des films renvoyés dans la réponse JSON