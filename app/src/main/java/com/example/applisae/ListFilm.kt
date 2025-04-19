package com.example.applisae

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ListFilm : BaseActivity() {

    // Déclaration des vues
    private lateinit var list_film: ListView
    private lateinit var recyclerView: RecyclerView
    private lateinit var bt_detail: Button
    private lateinit var bt_parametre: Button
    private lateinit var spinnertri: Spinner

    private var selectedMovie: Movie? = null
    private lateinit var movies: List<Movie>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        // Initialisation des composants
        list_film = findViewById(R.id.list_film)
        bt_detail = findViewById(R.id.button_details)
        bt_parametre = findViewById(R.id.button_parametre)
        spinnertri = findViewById(R.id.spinner_tri)

        // Bouton "détail" désactivé tant qu'aucun film n’est sélectionné
        bt_detail.isEnabled = false
        bt_detail.alpha = 0.5f

        // Récupération des paramètres d'intent
        val genreId = intent.getIntExtra("genre_id", 1)
        val nombreFilms = intent.getIntExtra("nombre", 10)
        val date = intent.getStringExtra("date") ?: ""
        val titre = intent.getStringExtra("titre") ?: ""
        val favorisOnly = intent.getBooleanExtra("favorisOnly", false)

        val dao = MovieAPI()

        // Si on est dans la vue des favoris, cacher le bouton "paramètres"
        if (favorisOnly) {
            bt_parametre.visibility = View.GONE
        } else {
            bt_parametre.visibility = View.VISIBLE
        }

        // Chargement des films
        CoroutineScope(Dispatchers.IO).launch {
            if (favorisOnly) {
                // Récupération des films en favoris
                val favoriteIds = FavoriteManager.getFavorites(this@ListFilm).mapNotNull { it.toIntOrNull() }
                val favorisMovies = favoriteIds.mapNotNull { dao.getMovieById(it) }

                Log.d("FAVORIS_API", "Films favoris récupérés : ${favorisMovies.map { it.title }}")

                movies = favorisMovies

                runOnUiThread {
                    if (movies.isEmpty()) {
                        Toast.makeText(this@ListFilm, "Aucun film en favori.", Toast.LENGTH_SHORT).show()
                    }
                    trierEtAfficher()
                }
            } else {
                // Recherche par titre ou date
                when {
                    titre.isNotBlank() -> dao.searchFilmByTitle(titre, nombreFilms) { films ->
                        movies = films
                        runOnUiThread { trierEtAfficher() }
                    }

                    date.isNotBlank() -> dao.searchFilmsFiltered(genreId, nombreFilms, date) { films ->
                        movies = films
                        runOnUiThread { trierEtAfficher() }
                    }

                    else -> dao.searchFilmsFiltered(genreId, nombreFilms, date) { films ->
                        movies = films
                        runOnUiThread { trierEtAfficher() }
                    }
                }
            }
        }

        // Action sur le spinner de tri
        spinnertri.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                if (::movies.isInitialized) {
                    trierEtAfficher()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Mode de sélection unique dans la liste
        list_film.choiceMode = ListView.CHOICE_MODE_SINGLE

        // Sélection d’un film dans la liste
        list_film.setOnItemClickListener { _, _, position, _ ->
            val sortedMovies = getSortedMovies()
            selectedMovie = sortedMovies[position]
            list_film.setItemChecked(position, true)
            bt_detail.isEnabled = true
            bt_detail.alpha = 1.0f
        }

        // Clic sur bouton détails → redirection vers page détails avec infos du film
        bt_detail.setOnClickListener {
            selectedMovie?.let { movie ->
                val genreIds = if (movie.genre_ids.isNotEmpty()) {
                    movie.genre_ids
                } else {
                    movie.genres.map { it.id }
                }


                val intentDetail = Intent(this@ListFilm, DetailFilm::class.java).apply {
                    putExtra("selectedGenres", genreIds.toIntArray())
                    putExtra("selectedTitle", movie.title)
                    putExtra("selectedId", movie.id)
                    putExtra("selectedDate", movie.release_date)
                    putExtra("selectedOverview", movie.overview)
                    putExtra("selectedImage", movie.poster_path)
                }
                startActivity(intentDetail)
            }
        }

        // Clic sur bouton paramètres → retour à la recherche avec les données
        bt_parametre.setOnClickListener {
            val intentBack = Intent().apply {
                putExtra("titre", titre)
                putExtra("date", date)
                putExtra("nombre", nombreFilms)
                putExtra("genre_id", genreId)
            }
            setResult(RESULT_OK, intentBack)
            finish() // ferme ListFilm et retourne à MainActivity
        }
    }

    /**
     * Trie les films selon l'option choisie dans le spinner.
     */
    private fun getSortedMovies(): List<Movie> {
        return when (spinnertri.selectedItemPosition) {
            0 -> movies.sortedBy { it.title }
            1 -> movies.sortedByDescending { it.release_date }
            else -> movies
        }
    }

    /**
     * Met à jour la liste des films triés à afficher dans la ListView.
     */
    private fun trierEtAfficher() {
        val sorted = getSortedMovies()
        val adapter = FilmAdapter(this, sorted)
        list_film.adapter = adapter
    }

    /**
     * Rafraîchit les favoris quand on revient sur l'activité (utile après suppression).
     */
    override fun onResume() {
        super.onResume()

        // Si on vient de DetailFilm et qu'on est dans la vue Favoris, on recharge les films
        if (intent.getBooleanExtra("favorisOnly", false)) {
            val dao = MovieAPI()

            CoroutineScope(Dispatchers.IO).launch {
                val favoriteIds = FavoriteManager.getFavorites(this@ListFilm).mapNotNull { it.toIntOrNull() }
                val favorisMovies = favoriteIds.mapNotNull { dao.getMovieById(it) }

                movies = favorisMovies

                runOnUiThread {
                    if (movies.isEmpty()) {
                        Toast.makeText(this@ListFilm, "Aucun film en favori.", Toast.LENGTH_SHORT).show()
                    }
                    trierEtAfficher()
                }
            }
        }
    }

    /**
     * Indique à la barre de navigation quel onglet est actif (favoris ou recherche).
     */
    override fun getSelectedNavItemId(): Int {
        return if (intent.getBooleanExtra("favorisOnly", false)) {
            R.id.nav_fav
        } else {
            R.id.nav_search
        }
    }



}