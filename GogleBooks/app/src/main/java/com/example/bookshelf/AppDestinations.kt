package com.example.bookshelf

enum class AppDestinations(val title: String) {
    MenuScreen(title = "Menú"),
    QueryScreen(title = "Estante de Libros de Google"),
    FavoriteScreen(title = "Mis Libros Favoritos"),
    DetailScreen(title = "Libro: ")
}
