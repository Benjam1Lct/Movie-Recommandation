package com.example.applisae
import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

/**
 * Modèle représentant un genre de film (ex : Action, Comédie, Horreur...).
 * Utilisé pour la récupération depuis l’API TMDb et l’affichage dans le Spinner.
 *
 * @property id Identifiant unique du genre (utilisé dans l'API)
 * @property name Nom du genre (ex: "Action", "Horror")
 */
@SuppressLint("UnsafeOptInUsageError") // Utilisé ici pour permettre la sérialisation même avec des API marquées "opt-in"
@Serializable
data class Genre(val id: Int, val name: String) {
    // Permet d’afficher directement le nom du genre dans les Spinner ou ListView
    override fun toString(): String = name
}
