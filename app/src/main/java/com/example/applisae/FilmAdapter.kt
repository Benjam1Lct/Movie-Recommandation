package com.example.applisae

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import coil.load
import coil.transform.RoundedCornersTransformation

/**
 * Adaptateur personnalisé pour afficher les films dans une ListView.
 * Utilisé dans l’écran ListFilm.
 *
 * @param context Contexte de l'application
 * @param films Liste des films à afficher
 */
class FilmAdapter(context: Context, private val films: List<Movie>) : ArrayAdapter<Movie>(context, 0, films)  {

    /**
     * Crée et retourne la vue d’un élément de la liste (titre, année, image...).
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Récupère le film à la position donnée
        val film = getItem(position)

        // Réutilise une vue existante ou en crée une nouvelle
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.activity_film_item, parent, false)

        // Références des éléments visuels dans le layout personnalisé
        val imageView = view.findViewById<ImageView>(R.id.item_image)
        val titleView = view.findViewById<TextView>(R.id.item_title)
        val yearView = view.findViewById<TextView>(R.id.item_year)

        // Mise à jour du titre et de l’année de sortie
        titleView.text = film?.title
        yearView.text = film?.release_date

        // Chargement de l’image du film avec Coil et coins arrondis
        val urlImage = film?.poster_path?.let { "https://image.tmdb.org/t/p/w500$it" }
        if (!urlImage.isNullOrEmpty()) {
            imageView.load(urlImage) {transformations(RoundedCornersTransformation(20f))} // Coin arrondi
        }

        return view
    }
}
