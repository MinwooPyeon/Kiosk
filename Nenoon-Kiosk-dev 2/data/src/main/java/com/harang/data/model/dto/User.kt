package com.harang.data.model.dto

data class User(
    val id: String? = null,
    val name: String? = null,
    val password: String? = null,
    val email: String? = null,
    val age: Double? = null,
    val gender: String? = null,
    val surveyId: String? = null,
    val faceIdAgreed: Boolean = false,
    val accessToken: String? = null,
    val refreshToken: String? = null,
)