package com.example.applisae

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Activité de base utilisée pour injecter dynamiquement une barre de navigation
 * sur toutes les pages de l'application qui l'étendent.
 */
open class BaseActivity : AppCompatActivity() {
    /**
     * Cette méthode doit être redéfinie dans chaque activité enfant pour indiquer
     * quel item de la BottomNavigationView doit être sélectionné.
     */
    protected open fun getSelectedNavItemId(): Int = -1

    /**
     * Surcharge de setContentView pour injecter dynamiquement le layout de base
     * avec la barre de navigation, puis y inclure le layout spécifique à l'activité.
     */
    override fun setContentView(layoutResID: Int) {
        // On récupère le layout de base (barre de nav + container vide)
        val rootView = layoutInflater.inflate(R.layout.activity_base, null)

        // On insère le layout spécifique dans le container prévu à cet effet
        val container = rootView.findViewById<FrameLayout>(R.id.base_container)
        layoutInflater.inflate(layoutResID, container, true)

        // On définit le contenu complet comme contenu de l'activité
        super.setContentView(rootView)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        // On identifie l'onglet qui doit être sélectionné pour cette activité
        val currentSelectedId = getSelectedNavItemId()
        bottomNav.selectedItemId = currentSelectedId

        // Gestion du clic sur les icônes de navigation
        bottomNav.setOnItemSelectedListener { item ->
            if (item.itemId == currentSelectedId) {
                // Si l'utilisateur clique sur l'onglet déjà actif, on ne fait rien
                return@setOnItemSelectedListener true
            }

            // Redirection vers la bonne activité en fonction de l'onglet cliqué
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                }
                R.id.nav_search -> {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                R.id.nav_fav -> {
                    val intent = Intent(this, ListFilm::class.java)
                    intent.putExtra("favorisOnly", true)
                    startActivity(intent)
                }
            }

            // Supprime l'animation entre les pages
            overridePendingTransition(0, 0)
            true
        }


        // On reforce la sélection du bouton actif après la config de la nav
        val selectedId = getSelectedNavItemId()
        if (selectedId != -1) {
            bottomNav.selectedItemId = selectedId
        }
    }
}

