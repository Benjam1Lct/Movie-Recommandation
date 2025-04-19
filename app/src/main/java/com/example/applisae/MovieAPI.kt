package com.example.applisae

import android.util.Log
import com.example.applisae.Genre
import com.example.applisae.GenreReponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.observer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Classe responsable de tous les appels API vers TheMovieDB
class MovieAPI(private val apiKey: String = "3dcd85ea72424bd4462c6322fdd52888") {

    // Configuration JSON pour gérer la désérialisation même si certains champs sont inconnus
    private val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }

    // Initialisation du client HTTP avec Ktor + OkHttp
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(json)
        }

        // Plugin de log pour afficher les requêtes/réponses dans Logcat
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("Ktor", message)
                }
            }
            level = LogLevel.ALL
        }

        // Observer pour afficher les statuts HTTP
        install(ResponseObserver) {
            onResponse { response ->
                Log.d("HTTP status", "${response.status.value}")
            }
        }
    }

    /**
     * Récupère la liste des genres disponibles via l'API TMDB
     */
    fun getGenres(): List<Genre> = runBlocking(Dispatchers.IO) {
        val url = "https://api.themoviedb.org/3/genre/movie/list?api_key=$apiKey&language=fr-FR"
        try {
            val response: GenreReponse = client.get(url).body()
            Log.d("GENRES_API", "Réponse : ${response.genres}")
            return@runBlocking response.genres
        } catch (e: Exception) {
            Log.e("GENRES_API", "Erreur récupération genres", e)
            return@runBlocking emptyList()
        }
    }

    /**
     * Recherche de films filtrée par genre, année, et tri par popularité
     * @param genreId ID du genre
     * @param count nombre de films à renvoyer
     * @param date année de sortie (ex: "2023")
     */
    fun searchFilmsFiltered(genreId: Int, count: Int, date: String, callback: (List<Movie>) -> Unit) {
        runBlocking(Dispatchers.IO) {
            try {
                val url = "https://api.themoviedb.org/3/discover/movie?api_key=$apiKey&language=fr-FR&with_genres=$genreId&primary_release_year=$date&sort_by=popularity.desc"
                val response: MovieResponse = client.get(url).body()
                callback(response.results.take(count))
            } catch (e: Exception) {
                Log.e("MovieAPI", "Error fetching films", e)
                callback(emptyList())
            }
        }
    }

    /**
     * Recherche des films par leur titre
     * @param titre chaîne de recherche
     * @param count nombre de résultats max
     */
    fun searchFilmByTitle(titre: String, count: Int, callback: (List<Movie>) -> Unit) {
        runBlocking(Dispatchers.IO) {
            try {
                val url = "https://api.themoviedb.org/3/search/movie?api_key=$apiKey&language=fr-FR&query=$titre"
                val response: HttpResponse = client.get(url)
                val bodyAsText = response.bodyAsText()
                val jsonElement = json.parseToJsonElement(bodyAsText)
                val films = json.decodeFromJsonElement(ListSerializer(Movie.serializer()), jsonElement.jsonObject["results"]!!)
                callback(films.take(count)) // ✅ limite ici
            } catch (e: Exception) {
                Log.e("MovieAPI", "Erreur lors de la recherche de film par titre", e)
                callback(emptyList())
            }
        }
    }

    /**
     * Récupère les détails d'un film à partir de son ID
     */
    fun getMovieById(movieId: Int): Movie? = runBlocking(Dispatchers.IO) {
        try {
            val url = "https://api.themoviedb.org/3/movie/$movieId?api_key=$apiKey&language=fr-FR"
            return@runBlocking client.get(url).body<Movie>()
        } catch (e: Exception) {
            Log.e("MovieAPI", "Erreur film ID $movieId", e)
            return@runBlocking null
        }
    }

    /**
     * Récupère les 6 derniers films populaires sortis au cinéma, avec un backdrop associé
     */
    fun getLatestReleasedBackdrops(count: Int = 6): List<BackdropItem> = runBlocking(Dispatchers.IO) {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val url = "https://api.themoviedb.org/3/discover/movie" +
                "?api_key=$apiKey" +
                "&language=fr-FR" +
                "&sort_by=release_date.desc" +
                "&include_adult=false" +
                "&with_release_type=3" +
                "&primary_release_date.lte=$todayStr" +
                "&vote_count.gte=1000"

        try {
            val response: MovieResponse = client.get(url).body()

            val result = mutableListOf<BackdropItem>()

            val recentMovies = response.results
                .filter { it.release_date.isNotBlank() }
                .take(count)

            for (movie in recentMovies) {
                val images = getMovieBackdrops(movie.id)
                if (images.isNotEmpty()) {
                    result.add(BackdropItem(images.first(), movie.id))
                }
            }

            return@runBlocking result
        } catch (e: Exception) {
            Log.e("MOVIE_API", "Erreur récupération des backdrops récents", e)
            return@runBlocking emptyList()
        }
    }


    /**
     * Récupère les images de type "backdrop" pour un film donné (souvent utilisé pour les bannières)
     */
    fun getMovieBackdrops(movieId: Int): List<String> = runBlocking(Dispatchers.IO) {
        val url =
            "https://api.themoviedb.org/3/movie/$movieId/images?api_key=$apiKey&include_image_language=fr,null"
        try {
            val response = client.get(url).bodyAsText()
            val jsonElement = json.parseToJsonElement(response)
            val backdrops = jsonElement.jsonObject["backdrops"]

            if (backdrops != null && backdrops is kotlinx.serialization.json.JsonArray) {
                return@runBlocking backdrops.mapNotNull { element: kotlinx.serialization.json.JsonElement ->
                    val filePath = element.jsonObject["file_path"]?.jsonPrimitive?.contentOrNull
                    filePath?.let { "https://image.tmdb.org/t/p/w780$it" }
                }
            }

            return@runBlocking emptyList()
        } catch (e: Exception) {
            Log.e("MOVIE_API", "Erreur récupération backdrops", e)
            return@runBlocking emptyList()
        }
    }

    /**
     * Modèle de donnée pour stocker une image de backdrop + l'ID du film associé
     */
    data class BackdropItem(
        val imageUrl: String,
        val movieId: Int
    )


}
