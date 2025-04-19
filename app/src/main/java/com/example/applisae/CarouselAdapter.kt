package com.example.applisae

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load

/**
 * Adaptateur personnalisé pour le carrousel d’images dans l’accueil.
 * Affiche une liste horizontale d’affiches ou de backdrops de films.
 *
 * @param context Contexte Android
 * @param items Liste des éléments à afficher dans le carrousel
 * @param onItemClick Callback déclenché lorsqu’un item est cliqué (renvoie l’ID du film)
 */
class CarouselAdapter(
    private val context: Context,
    private val items: List<CarouselItem>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    /**
     * ViewHolder qui contient l’image du film (backdrop).
     */
    inner class CarouselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.carousel_image)
    }

    /**
     * Crée une nouvelle vue pour un élément du carrousel.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_carousel, parent, false)
        return CarouselViewHolder(view)
    }

    /**
     * Lie les données à la vue (charge l’image et gère le clic).
     */
    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        val item = items[position]
        holder.image.load(item.imageUrl) // Chargement de l'image avec Coil

        // Gestion du clic : renvoie l'ID du film au callback
        holder.itemView.setOnClickListener {
            onItemClick(item.movieId)
        }
    }

    /**
     * Retourne le nombre total d’éléments dans le carrousel.
     */
    override fun getItemCount(): Int = items.size
}
