package com.example.applisae


import android.annotation.SuppressLint
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.applisae.R
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : BaseActivity(), CoroutineScope {

    // Déclaration des composants UI
    private lateinit var editNomFilm: EditText
    private lateinit var editDate: EditText
    private lateinit var spinnerGenre: Spinner
    private lateinit var seekBar: SeekBar
    private lateinit var textViewNombre: TextView
    private lateinit var buttonValider: Button
    private lateinit var buttonFavoris: Button
    private lateinit var buttonHome: Button

    // Liste des genres récupérés depuis l'API
    private lateinit var genres: List<Genre>
    private var genreId: Int = 0

    // Contexte de coroutine utilisé pour les appels API asynchrones
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialisation des vues
        editNomFilm = findViewById(R.id.edit_nomfilm)
        editDate = findViewById(R.id.edit_date)
        spinnerGenre = findViewById(R.id.spinner_genre)
        seekBar = findViewById(R.id.seekBar_nbre)
        textViewNombre = findViewById(R.id.textView_nombre)
        buttonValider = findViewById(R.id.button_valider)

        seekBar.progress = 25
        buttonValider.isEnabled = false

        // Mise à jour du nombre de résultats lorsque la seekbar est utilisée
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                textViewNombre.text = progress.toString()
            }

            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        // Chargement asynchrone des genres depuis l’API
        loadGenres()

        // Chargement des genres et pré-sélection si nécessaire
        launch {
            val api = MovieAPI()
            genres = withContext(Dispatchers.IO) { api.getGenres() }

            val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, genres)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerGenre.adapter = adapter

            buttonValider.isEnabled = true
        }

        // Clic sur le bouton "Valider" → redirection vers ListFilm avec les critères sélectionnés
        buttonValider.setOnClickListener {
            val titre = editNomFilm.text.toString()
            val date = editDate.text.toString()
            val nombre = seekBar.progress
            val genre = spinnerGenre.selectedItem as Genre

            val intent = Intent(this, ListFilm::class.java).apply {
                putExtra("titre", titre)
                putExtra("date", date)
                putExtra("nombre", nombre)
                putExtra("genre_id", genre.id)
            }
            startActivity(intent)
        }

    }

    /**
     * Charge la liste des genres depuis l'API et les affiche dans le spinner.
     */
    private fun loadGenres() {
        launch {
            val api = MovieAPI()
            genres = withContext(Dispatchers.IO) { api.getGenres() }

            val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, genres)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerGenre.adapter = adapter
            buttonValider.isEnabled = true
        }
    }

    /**
     * Permet de signaler à la BottomNavView que cette activité correspond à l’onglet Recherche.
     */
    override fun getSelectedNavItemId(): Int = R.id.nav_search // ou nav_home / nav_search / nav_fav

    @Deprecated("Deprecated in Java") // Annotation pour indiquer que cette méthode est obsolète dans les versions modernes d’Android
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Appel de la méthode parente pour conserver le comportement par défaut
        super.onActivityResult(requestCode, resultCode, data)

        // Vérifie si le résultat est OK et que l'intent contient bien des données
        if (resultCode == RESULT_OK && data != null) {
            val titre = data.getStringExtra("titre") ?: ""
            val date = data.getStringExtra("date") ?: ""
            val nombre = data.getIntExtra("nombre", 15)
            val genreId = data.getIntExtra("genre_id", -1)

            // Met à jour les champs de l’interface avec les valeurs récupérées
            editNomFilm.setText(titre)
            editDate.setText(date)
            seekBar.progress = nombre
            textViewNombre.text = nombre.toString()

            // Si un genre a été précisé, on sélectionne le bon index dans le spinner
            if (genreId != -1) {
                val index = genres.indexOfFirst { it.id == genreId }
                if (index >= 0) spinnerGenre.setSelection(index)
            }
        }
    }

}
