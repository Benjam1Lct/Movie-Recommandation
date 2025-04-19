package com.example.applisae

import android.content.Context
import android.content.SharedPreferences

/**
 * Singleton pour gérer les films favoris en local via SharedPreferences.
 * Permet d'ajouter, retirer, ou vérifier les films marqués comme favoris.
 */
object FavoriteManager {
    // Nom du fichier de préférences
    private const val PREF_NAME = "favorites_pref"

    // Clé sous laquelle sont stockés les IDs des films favoris
    private const val FAVORITE_KEY = "favorite_movies"

    /**
     * Récupère l'objet SharedPreferences lié à l'application.
     */
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Récupère la liste des IDs de films favoris (au format Set de Strings).
     */
    fun getFavorites(context: Context): Set<String> {
        return getPrefs(context).getStringSet(FAVORITE_KEY, emptySet()) ?: emptySet()
    }

    /**
     * Vérifie si un film est présent dans les favoris.
     *
     * @param movieId L'identifiant numérique du film à vérifier.
     */
    fun isFavorite(context: Context, movieId: Int): Boolean {
        return getFavorites(context).contains(movieId.toString())
    }

    /**
     * Ajoute ou retire un film des favoris. Si déjà présent, le retire ; sinon, l’ajoute.
     *
     * @param movieId L'identifiant du film à basculer dans les favoris.
     */
    fun toggleFavorite(context: Context, movieId: Int) {
        val prefs = getPrefs(context)
        val favorites = getFavorites(context).toMutableSet()
        val idStr = movieId.toString()

        if (favorites.contains(idStr)) {
            favorites.remove(idStr) // Supprime des favoris
        } else {
            favorites.add(idStr) // Ajoute aux favoris
        }

        // Sauvegarde la nouvelle liste dans SharedPreferences
        prefs.edit().putStringSet(FAVORITE_KEY, favorites).apply()
    }
}