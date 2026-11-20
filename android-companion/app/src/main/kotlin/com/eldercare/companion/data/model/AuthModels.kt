package com.eldercare.companion.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("token") val token: String,
    @SerializedName("sessionId") val sessionId: String,
    @SerializedName("role") val role: String,
    @SerializedName("name") val name: String
)

data class CurrentUser(
    @SerializedName("id") val id: Long,
    @SerializedName("email") val email: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("roleName") val roleName: String
) {
    val fullName: String
        get() = "$firstName $lastName".trim()
}
