package com.example.applisae

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

// Annotation pour ignorer l'avertissement lié à l'utilisation de certaines API Kotlin expérimentales
@SuppressLint("UnsafeOptInUsageError")

// Annotation de sérialisation pour indiquer que cette classe peut être convertie vers/depuis JSON
@Serializable
data class Movie(
    val id: Int, // Identifiant unique du film
    val title: String, // Titre du film
    val release_date: String, // Date de sortie (au format yyyy-MM-dd)
    val overview: String,  // Description ou résumé du film

    // URL partielle vers l'affiche du film (peut être null si l'API ne fournit pas d'image)
    val poster_path: String? = null,

    // Liste d'ID des genres associés au film (valeurs entières)
    val genre_ids: List<Int> = emptyList(),

    // Liste d'ID des genres associés au film (valeurs entières)
    val genres: List<Genre> = emptyList()

)  {
    // Redéfinition de la méthode toString pour afficher le film dans les logs ou dans une liste
    override fun toString(): String {
        // Retourne le titre suivi de la date, formaté avec un emoji calendrier
        return "$title \n \uD83D\uDCC5($release_date)"
    }

}

