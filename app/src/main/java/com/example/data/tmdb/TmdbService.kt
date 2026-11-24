package com.example.data.tmdb

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.BuildConfig
import com.example.data.model.MediaCategory
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private val Context.tmdbDataStore by preferencesDataStore(name = "afterglow_tmdb_prefs")

object TmdbPreferences {
    private val KEY_TMDB_API_KEY = stringPreferencesKey("tmdb_api_key")

    fun observeUserKey(context: Context): Flow<String> =
        context.tmdbDataStore.data.map { prefs -> prefs[KEY_TMDB_API_KEY] ?: "" }

    suspend fun saveUserKey(context: Context, key: String) {
        context.tmdbDataStore.edit { prefs ->
            prefs[KEY_TMDB_API_KEY] = key.trim()
        }
    }

    suspend fun resolveEffectiveKey(context: Context): String {
        val userKey = observeUserKey(context).first().trim()
        if (userKey.isNotBlank()) return userKey
        return BuildConfig.TMDB_API_KEY.trim().takeIf { it != "MY_TMDB_API_KEY" } ?: ""
    }
}

data class TmdbSearchResponse(
    val results: List<TmdbMultiResult> = emptyList()
)

data class TmdbMultiResult(
    val id: Int = 0,
    @Json(name = "media_type") val mediaType: String? = null,
    val title: String? = null,
    val name: String? = null,
    @Json(name = "poster_path") val posterPath: String? = null,
    @Json(name = "release_date") val releaseDate: String? = null,
    @Json(name = "first_air_date") val firstAirDate: String? = null,
    @Json(name = "vote_average") val voteAverage: Double? = null,
    val overview: String? = null,
    @Json(name = "original_language") val originalLanguage: String? = null
)

data class TmdbGenre(val id: Int = 0, val name: String = "")
data class TmdbCastMember(val name: String = "", val order: Int = 99)
data class TmdbCrewMember(val name: String = "", val job: String = "")
data class TmdbCredits(
    val cast: List<TmdbCastMember> = emptyList(),
    val crew: List<TmdbCrewMember> = emptyList()
)
data class TmdbVideo(val key: String = "", val site: String = "", val type: String = "")
data class TmdbVideos(val results: List<TmdbVideo> = emptyList())
data class TmdbCountry(val name: String = "")
data class TmdbProviderItem(@Json(name = "provider_name") val providerName: String = "")
data class TmdbLocaleProviders(val flatrate: List<TmdbProviderItem>? = null)
data class TmdbWatchProviders(val results: Map<String, TmdbLocaleProviders> = emptyMap())
data class TmdbCreator(val name: String = "")

data class TmdbMovieDetails(
    val id: Int = 0,
    val title: String = "",
    @Json(name = "poster_path") val posterPath: String? = null,
    @Json(name = "release_date") val releaseDate: String? = null,
    val runtime: Int? = null,
    @Json(name = "vote_average") val voteAverage: Double? = null,
    @Json(name = "imdb_id") val imdbId: String? = null,
    @Json(name = "original_language") val originalLanguage: String? = null,
    val overview: String? = null,
    val genres: List<TmdbGenre> = emptyList(),
    @Json(name = "production_countries") val productionCountries: List<TmdbCountry> = emptyList(),
    val credits: TmdbCredits? = null,
    val videos: TmdbVideos? = null,
    @Json(name = "watch/providers") val watchProviders: TmdbWatchProviders? = null
)

data class TmdbTvDetails(
    val id: Int = 0,
    val name: String = "",
    @Json(name = "poster_path") val posterPath: String? = null,
    @Json(name = "first_air_date") val firstAirDate: String? = null,
    @Json(name = "episode_run_time") val episodeRunTime: List<Int> = emptyList(),
    @Json(name = "number_of_seasons") val numberOfSeasons: Int? = null,
    @Json(name = "number_of_episodes") val numberOfEpisodes: Int? = null,
    @Json(name = "vote_average") val voteAverage: Double? = null,
    @Json(name = "original_language") val originalLanguage: String? = null,
    @Json(name = "origin_country") val originCountry: List<String> = emptyList(),
    val overview: String? = null,
    val genres: List<TmdbGenre> = emptyList(),
    @Json(name = "created_by") val createdBy: List<TmdbCreator> = emptyList(),
    val credits: TmdbCredits? = null,
    val videos: TmdbVideos? = null,
    @Json(name = "watch/providers") val watchProviders: TmdbWatchProviders? = null
)

interface TmdbApi {
    @GET("search/multi")
    suspend fun searchMulti(
        @Query("api_key") apiKey: String,
        @Query("query") query: String
    ): TmdbSearchResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("append_to_response") append: String = "credits,videos,watch/providers"
    ): TmdbMovieDetails

    @GET("tv/{tv_id}")
    suspend fun getTvDetails(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String,
        @Query("append_to_response") append: String = "credits,videos,watch/providers"
    ): TmdbTvDetails
}

data class TmdbPrefillResult(
    val title: String,
    val category: MediaCategory,
    val posterUrl: String,
    val genre: String,
    val language: String,
    val releaseYear: Long,
    val runtimeMinutes: Long,
    val director: String,
    val mainCast: String,
    val country: String,
    val imdbRating: Double,
    val streamingPlatform: String,
    val trailerLink: String,
    val imdbLink: String,
    val totalSeasons: Long,
    val totalEpisodes: Long,
    val notes: String
)

class TmdbRepository {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val client = OkHttpClient.Builder().build()
    private val api: TmdbApi = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(TmdbApi::class.java)

    suspend fun search(apiKey: String, query: String): Result<List<TmdbMultiResult>> = try {
        val resp = api.searchMulti(apiKey, query)
        val filtered = resp.results.filter { it.mediaType == "movie" || it.mediaType == "tv" }
        Result.success(filtered)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun fetchDetails(apiKey: String, item: TmdbMultiResult): Result<TmdbPrefillResult> = try {
        if (item.mediaType == "tv") {
            val d = api.getTvDetails(item.id, apiKey)
            val isAnime = d.originalLanguage == "ja" && d.genres.any { it.name.equals("Animation", true) }
            val isKDrama = d.originalLanguage == "ko"
            val category = when {
                isAnime -> MediaCategory.ANIME
                isKDrama -> MediaCategory.K_DRAMA
                else -> MediaCategory.WEB_SERIES
            }
            val year = d.firstAirDate?.take(4)?.toLongOrNull() ?: 2024L
            val trailerKey = d.videos?.results
                ?.firstOrNull { it.site.equals("YouTube", true) && it.type.equals("Trailer", true) }
                ?.key ?: d.videos?.results?.firstOrNull { it.site.equals("YouTube", true) }?.key
            val provider = d.watchProviders?.results?.get("US")?.flatrate?.firstOrNull()?.providerName
                ?: d.watchProviders?.results?.values?.firstOrNull()?.flatrate?.firstOrNull()?.providerName
                ?: "Netflix"
            Result.success(
                TmdbPrefillResult(
                    title = d.name,
                    category = category,
                    posterUrl = d.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" } ?: "",
                    genre = d.genres.joinToString(", ") { it.name },
                    language = mapLanguageCode(d.originalLanguage),
                    releaseYear = year,
                    runtimeMinutes = (d.episodeRunTime.firstOrNull() ?: 45).toLong(),
                    director = d.createdBy.joinToString(", ") { it.name },
                    mainCast = d.credits?.cast?.sortedBy { it.order }?.take(5)?.joinToString(", ") { it.name } ?: "",
                    country = d.originCountry.firstOrNull() ?: "",
                    imdbRating = ((d.voteAverage ?: 0.0) * 10.0).toInt() / 10.0,
                    streamingPlatform = provider,
                    trailerLink = trailerKey?.let { "https://www.youtube.com/watch?v=$it" } ?: "",
                    imdbLink = "",
                    totalSeasons = (d.numberOfSeasons ?: 1).toLong(),
                    totalEpisodes = (d.numberOfEpisodes ?: 10).toLong(),
                    notes = d.overview?.take(400) ?: ""
                )
            )
        } else {
            val d = api.getMovieDetails(item.id, apiKey)
            val year = d.releaseDate?.take(4)?.toLongOrNull() ?: 2024L
            val director = d.credits?.crew?.firstOrNull { it.job.equals("Director", true) }?.name ?: ""
            val trailerKey = d.videos?.results
                ?.firstOrNull { it.site.equals("YouTube", true) && it.type.equals("Trailer", true) }
                ?.key ?: d.videos?.results?.firstOrNull { it.site.equals("YouTube", true) }?.key
            val provider = d.watchProviders?.results?.get("US")?.flatrate?.firstOrNull()?.providerName
                ?: d.watchProviders?.results?.values?.firstOrNull()?.flatrate?.firstOrNull()?.providerName
                ?: "Netflix"
            Result.success(
                TmdbPrefillResult(
                    title = d.title,
                    category = MediaCategory.MOVIE,
                    posterUrl = d.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" } ?: "",
                    genre = d.genres.joinToString(", ") { it.name },
                    language = mapLanguageCode(d.originalLanguage),
                    releaseYear = year,
                    runtimeMinutes = (d.runtime ?: 120).toLong(),
                    director = director,
                    mainCast = d.credits?.cast?.sortedBy { it.order }?.take(5)?.joinToString(", ") { it.name } ?: "",
                    country = d.productionCountries.firstOrNull()?.name ?: "",
                    imdbRating = ((d.voteAverage ?: 0.0) * 10.0).toInt() / 10.0,
                    streamingPlatform = provider,
                    trailerLink = trailerKey?.let { "https://www.youtube.com/watch?v=$it" } ?: "",
                    imdbLink = d.imdbId?.takeIf { it.isNotBlank() }?.let { "https://www.imdb.com/title/$it" } ?: "",
                    totalSeasons = 1L,
                    totalEpisodes = 1L,
                    notes = d.overview?.take(400) ?: ""
                )
            )
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    private fun mapLanguageCode(code: String?): String = when (code?.lowercase()) {
        "en" -> "English"
        "ko" -> "Korean"
        "ja" -> "Japanese"
        "es" -> "Spanish"
        "fr" -> "French"
        "de" -> "German"
        "hi" -> "Hindi"
        "ta" -> "Tamil"
        "te" -> "Telugu"
        "zh" -> "Chinese"
        "it" -> "Italian"
        else -> code?.uppercase() ?: "English"
    }
}
