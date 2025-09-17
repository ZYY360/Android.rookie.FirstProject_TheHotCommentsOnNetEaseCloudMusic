package com.example.firstproject.Api

data class SongData(
    val songId: String,
    val title: String,
    val author: String,
    val imageUrl: String,
    val commentContent: String,
    val commentPublishedDate: String
)