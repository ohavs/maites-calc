package com.example.maitescalc.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class GitHubRelease(
    val tag_name: String = "",
    val name: String? = null,
    val body: String? = null,
    val published_at: String? = null,
    val html_url: String? = null,
    val assets: List<GitHubAsset> = emptyList()
)

data class GitHubAsset(
    val name: String = "",
    val browser_download_url: String = "",
    val size: Long = 0L,
    val content_type: String = ""
)

interface GitHubApiService {
    @GET("repos/ohavs/maites-calc/releases/latest")
    suspend fun getLatestRelease(): GitHubRelease
}

object GitHubApi {
    private const val BASE_URL = "https://api.github.com/"

    val service: GitHubApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GitHubApiService::class.java)
    }
}
