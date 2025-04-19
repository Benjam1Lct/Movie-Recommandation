# 🎬 AppliSAE - Application Android de consultation de films

## 📱 Présentation

**AppliSAE** est une application Android développée en **Kotlin** qui permet à l'utilisateur de :
- rechercher des films via une API (The Movie Database),
- consulter leurs détails (titre, description, genres, date de sortie),
- enregistrer des favoris en local,
- naviguer via une interface moderne avec un carrousel dynamique et des catégories prédéfinies.

---

## 🧭 Fonctionnalités principales

- 🔍 **Recherche de films** par titre, genre, année de sortie, et nombre de résultats.
- ⭐ **Système de favoris** en local via `SharedPreferences`.
- 🧾 **Détails enrichis** (genres, description, backdrop en HD, année...).
- 🖼️ **Carrousel d’affiches** pour les derniers films en salle.
- 🗂️ **Catégories prédéfinies** (Action, Horreur, Sorties 2026).
- 🎨 **UI immersive** avec :
  - images plein écran + effet de dégradé,
  - bottom navigation bar personnalisée (icônes `outline` / `filled` selon sélection),
  - transitions douces entre les vues.

---

## 📂 Structure du projet

```
AppliSAE/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/applisae/
│   │   │   ├── MainActivity.kt
│   │   │   ├── HomeActivity.kt
│   │   │   ├── ListFilm.kt
│   │   │   ├── DetailFilm.kt
│   │   │   ├── BaseActivity.kt
│   │   │   ├── Movie.kt
│   │   │   ├── MovieAPI.kt
│   │   │   ├── MovieResponse.kt
│   │   │   ├── Genre.kt / GenreReponse.kt
│   │   │   ├── FavoriteManager.kt
│   │   │   ├── CarouselAdapter.kt
│   │   │   └── FilmAdapter.kt
│   │   ├── res/layout/
│   │   │   ├── activity_main.xml
│   │   │   ├── activity_home.xml
│   │   │   ├── activity_list.xml
│   │   │   ├── activity_detail_film.xml
│   │   │   └── item_film.xml / item_category.xml / item_carousel.xml
│   │   ├── res/drawable/
│   │   │   ├── gradient_overlay.xml
│   │   │   ├── ic_home_outline.xml / ic_home_filled.xml (etc.)
│   │   └── AndroidManifest.xml
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 🔧 Technologies utilisées

- **Langage** : Kotlin
- **HTTP client** : Ktor
- **Image loading** : Coil
- **Architecture** : Activités + Adaptateurs RecyclerView
- **Stockage local** : `SharedPreferences` (favoris)
- **UI** : XML, `ConstraintLayout`, `RecyclerView`, `BottomNavigationView`, `VectorDrawable`, etc.

---

## ▶️ Lancer l’application

1. **Ouvrir dans Android Studio**
2. **Synchroniser Gradle**
3. **Configurer la clé API**
4. **Exécuter sur un émulateur ou un appareil réel**

---

## 📁 Fichiers clés

| Fichier | Description |
|--------|-------------|
| `MainActivity.kt` | Recherche personnalisée par titre / année / genre |
| `HomeActivity.kt` | Page d’accueil immersive avec carrousel + catégories |
| `ListFilm.kt` | Liste des films recherchés ou des favoris |
| `DetailFilm.kt` | Affichage détaillé avec poster ou backdrop |
| `BaseActivity.kt` | Activité de base avec barre de navigation réutilisable |
| `CarouselAdapter.kt` | Adapter pour les backdrops des films récents |
| `MovieAPI.kt` | Gestion des appels API (films, genres, backdrops...) |
| `FavoriteManager.kt` | Gestion des favoris (add / remove / check) |


---

## 👨‍💻 Réalisé par

Projet développé dans le cadre du module **SAE Android**.
- Benjamin LECOMTE
- Driss BARITAUD
- Ilan BUCHOUX
