package br.edu.hazard.dao

data class Usuario(
    val username: String,
    val email:String,
    val senha:String,
    val cinema: Boolean,
    val esporte: Boolean,
    val musica: Boolean
)
