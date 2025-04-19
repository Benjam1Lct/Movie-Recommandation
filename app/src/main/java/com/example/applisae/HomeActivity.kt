package com.example.applisae

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import coil.load
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import coil.load
import coil.transform.RoundedCornersTransformation



class HomeActivity : BaseActivity() {

    // Composants de l'UI
    private lateinit var carouselRecycler: RecyclerView

    // Pour le défilement automatique du carrousel
    private var autoScrollHandler: Handler? = null
    private var autoScrollRunnable: Runnable? = null
    private val scrollInterval: Long = 4000 // Intervalle entre chaque scroll (ms)

    // Boutons d'accès aux catégories
    private lateinit var releaseButton: Button
    private lateinit var actionButton: Button
    private lateinit var horrorButton: Button

    // Affichage d’une affiche par catégorie
    private lateinit var releaseImage: ImageView
    private lateinit var actionImage: ImageView
    private lateinit var horrorImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Rendre la barre de statut transparente pour immersion visuelle
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
        window.statusBarColor = Color.TRANSPARENT


        setContentView(R.layout.activity_home)
        AndroidThreeTen.init(this) // Initialisation de la librairie de date

        // Configuration du carrousel horizontal
        carouselRecycler = findViewById(R.id.carouselRecycler)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        carouselRecycler.layoutManager = layoutManager
        val snapHelper = PagerSnapHelper() // effet "snap" image par image
        snapHelper.attachToRecyclerView(carouselRecycler)

        val dao = MovieAPI()

        // Chargement des images de fond (backdrops) récentes pour le carrousel
        CoroutineScope(Dispatchers.IO).launch {
            val items = dao.getLatestReleasedBackdrops(6)

            val carouselItems = items.map {
                CarouselItem(it.imageUrl, it.movieId)
            }

            runOnUiThread {
                carouselRecycler.layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
                carouselRecycler.adapter = CarouselAdapter(this@HomeActivity, carouselItems) { movieId ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val movie = dao.getMovieById(movieId)

                        movie?.let {
                            val genreIds = if (it.genre_ids.isNotEmpty()) it.genre_ids else it.genres.map { g -> g.id }

                            val intent = Intent(this@HomeActivity, DetailFilm::class.java).apply {
                                putExtra("selectedGenres", genreIds.toIntArray())
                                putExtra("selectedTitle", it.title)
                                putExtra("selectedId", it.id)
                                putExtra("selectedDate", it.release_date)
                                putExtra("selectedOverview", it.overview)
                                putExtra("selectedImage", it.poster_path) // fallback
                            }
                            runOnUiThread {
                                startActivity(intent)
                            }
                        }
                    }
                }

            }

            // Lancement du scroll automatique
            startAutoScroll(carouselItems.size)

        }

        // Initialisation des éléments visuels pour les 3 catégories
        releaseButton = findViewById(R.id.cat1_more)
        actionButton = findViewById(R.id.cat2_more)
        horrorButton = findViewById(R.id.cat3_more)

        releaseImage = findViewById(R.id.cat1_imageView)
        actionImage = findViewById(R.id.cat2_imageView)
        horrorImage = findViewById(R.id.cat3_imageView)


        val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
        val nexYear = (year.toInt() + 1).toString()

        // Chargement d’un film à venir (ex: sorties 2026) pour recuperer le poster
        dao.searchFilmsFiltered(genreId = 18, count = 1, date = nexYear) { films ->
            val film = films.firstOrNull()
            val posterUrl = film?.poster_path?.let { "https://image.tmdb.org/t/p/w500$it" }
            runOnUiThread {
                posterUrl?.let { releaseImage.load(it){transformations(RoundedCornersTransformation(20f))} }
            }
        }

        // Chargement d’un film d’action pour recuperer le poster
        dao.searchFilmsFiltered(genreId = 28, count = 1, date = year) { films ->
            val film = films.firstOrNull()
            val posterUrl = film?.poster_path?.let { "https://image.tmdb.org/t/p/w500$it" }
            runOnUiThread {
                posterUrl?.let { actionImage.load(it){transformations(RoundedCornersTransformation(20f))} }
            }
        }

        // Chargement d’un film d’horreur pour recuperer le poster
        dao.searchFilmsFiltered(genreId = 27, count = 1, date = year) { films ->
            val film = films.firstOrNull()
            val posterUrl = film?.poster_path?.let { "https://image.tmdb.org/t/p/w500$it" }
            runOnUiThread {
                posterUrl?.let { horrorImage.load(it){transformations(RoundedCornersTransformation(20f))} }
            }
        }

        // Redirections vers ListFilm avec filtres pré-remplis selon le bouton cliqué
        val nombre = 30 // nombre de films dans la list

        releaseButton.setOnClickListener {
            val intent = Intent(this, ListFilm::class.java).apply {
                putExtra("titre", "")
                putExtra("date", nexYear) // ➡️ films sortis cette année
                putExtra("genre_id", 878) // Ex : Science-fiction
                putExtra("nombre", nombre)
                putExtra("favorisOnly", false)
            }
            startActivity(intent)
        }

        actionButton.setOnClickListener {
            val intent = Intent(this, ListFilm::class.java).apply {
                putExtra("titre", "")
                putExtra("date", "") // pas de filtre date
                putExtra("genre_id", 28) // 🎬 Action
                putExtra("nombre", nombre)
                putExtra("favorisOnly", false)
            }
            startActivity(intent)
        }

        horrorButton.setOnClickListener {
            val intent = Intent(this, ListFilm::class.java).apply {
                putExtra("titre", "")
                putExtra("date", "") // pas de filtre date
                putExtra("genre_id", 27) // 😱 Horreur
                putExtra("nombre", nombre)
                putExtra("favorisOnly", false)
            }
            startActivity(intent)
        }
    }

    /**
     * Fait défiler automatiquement les éléments du carrousel avec un intervalle défini.
     */
    private fun startAutoScroll(itemCount: Int) {
        autoScrollHandler = Handler(Looper.getMainLooper())
        var currentIndex = 0

        autoScrollRunnable = object : Runnable {
            override fun run() {
                if (itemCount == 0) return

                carouselRecycler.smoothScrollToPosition(currentIndex)
                currentIndex = (currentIndex + 1) % itemCount // boucle infinie
                autoScrollHandler?.postDelayed(this, scrollInterval)
            }
        }

        autoScrollHandler?.postDelayed(autoScrollRunnable!!, scrollInterval)
    }

    /**
     * Nettoyage du handler pour éviter les fuites de mémoire.
     */
    override fun onDestroy() {
        super.onDestroy()
        autoScrollHandler?.removeCallbacks(autoScrollRunnable!!)
    }

    /**
     * Utilisé par BaseActivity pour savoir quel bouton de navigation est sélectionné.
     */
    override fun getSelectedNavItemId(): Int = R.id.nav_home // ou nav_home / nav_search / nav_fav

}

/**
 * Modèle de données représentant une image à afficher dans le carrousel.
 */
data class CarouselItem(val imageUrl: String, val movieId: Int)

/**
 * Modèle représentant une catégorie de films (ex: Action, Horreur, Sorties 2026)
 */
data class FilmCategory(val title: String, val genreId: Int, val year: String? = null)
