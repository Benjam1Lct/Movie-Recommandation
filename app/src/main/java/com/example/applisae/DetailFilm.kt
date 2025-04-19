package com.example.applisae

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import coil.load
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Écran de détails d’un film sélectionné.
 * Affiche l’image, le titre, la date, les genres et une description.
 * Permet aussi d’ajouter ou retirer le film des favoris.
 */
class DetailFilm : AppCompatActivity() {

    // Déclaration des éléments de l’UI
    private lateinit var bt_leave : Button
    private lateinit var titre_film : TextView
    private lateinit var date_film : TextView
    private lateinit var img_film : ImageView
    private lateinit var description_film : TextView
    private lateinit var favButton : ImageButton
    private lateinit var favButtonBox : LinearLayout


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_film)

        // Initialisation des vues
        favButtonBox = findViewById<LinearLayout>(R.id.favButtonBox)
        favButton = findViewById(R.id.favButton)
        bt_leave = findViewById(R.id.button_leave)
        titre_film = findViewById(R.id.titre_film)
        date_film = findViewById(R.id.date_film)
        img_film = findViewById(R.id.image_film)
        description_film = findViewById(R.id.description_film)

        // Récupération des données passées via l’intent
        val imgPath = intent.getStringExtra("selectedImage")
        val title = intent.getStringExtra("selectedTitle") ?: "Titre inconnu"
        val fullDate = intent.getStringExtra("selectedDate") ?: "Date inconnue"
        val description = intent.getStringExtra("selectedOverview") ?: "Pas de description"
        val id = intent.getIntExtra("selectedId", -1)
        val genreIds = intent.getIntArrayExtra("selectedGenres")?.toList() ?: emptyList()

        // Récupère la liste des genres et crée un mapping id -> nom
        val allGenres = MovieAPI().getGenres()
        val genreMap = allGenres.associateBy({ it.id }, { it.name })
        val genreNames = genreIds.mapNotNull { genreMap[it] }
        val genreText = genreNames.joinToString(" , ") // ou ", ", ou autre

        // Affiche une étoile pleine ou vide selon si le film est en favori
        if (FavoriteManager.isFavorite(this, id)) {
            favButton.setBackgroundResource(R.drawable.ic_star_filled_red) // ton drawable personnalisé
        } else {
            favButton.setBackgroundResource(R.drawable.ic_star_outline_white) // ton icône vide
        }

        // Affiche les infos texte
        val year = fullDate.substringBefore("-")
        titre_film.text = title
        date_film.text = year + " • " + genreText
        description_film.text = description

        // Chargement de l’image : d’abord une backdrop, sinon fallback sur poster
        CoroutineScope(Dispatchers.IO).launch {
            val dao = MovieAPI()
            val backdrops = dao.getMovieBackdrops(id)

            if (backdrops.isNotEmpty()) {
                val backdropUrl = backdrops.first()

                runOnUiThread {
                    img_film.load(backdropUrl)
                }
            } else {
                runOnUiThread {
                    imgPath.let {
                        val imageUrl = "https://image.tmdb.org/t/p/w500$it"
                        img_film.load(imageUrl)
                    }
                }
            }
        }

        // Bouton de retour
        bt_leave.setOnClickListener {
            finish()
        }

        // Gestion des clics pour ajout / retrait des favoris (boîte cliquable ou icône directe)
        favButtonBox.setOnClickListener {
            FavoriteManager.toggleFavorite(this, id)

            if (FavoriteManager.isFavorite(this, id)) {
                favButton.setBackgroundResource(R.drawable.ic_star_filled_red) // ton drawable personnalisé
            } else {
                favButton.setBackgroundResource(R.drawable.ic_star_outline_white) // ton icône vide
            }
        }

        favButton.setOnClickListener {
            FavoriteManager.toggleFavorite(this, id)

            if (FavoriteManager.isFavorite(this, id)) {
                favButton.setBackgroundResource(R.drawable.ic_star_filled_red)
            } else {
                favButton.setBackgroundResource(R.drawable.ic_star_outline_white)
            }
        }



    }
}