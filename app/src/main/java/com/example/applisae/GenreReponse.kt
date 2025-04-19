package com.example.applisae

import kotlinx.serialization.Serializable

/**
 * Modèle de données pour la réponse JSON de l’API TMDb qui retourne la liste des genres.
 * Ce modèle correspond à la racine de la réponse :
 * {
 *   "genres": [
 *     { "id": 28, "name": "Action" },
 *     ...
 *   ]
 * }
 *
 * @property genres Liste des objets Genre contenus dans la réponse
 */
@Serializable
data class GenreReponse(val genres: List<Genre> = emptyList())
