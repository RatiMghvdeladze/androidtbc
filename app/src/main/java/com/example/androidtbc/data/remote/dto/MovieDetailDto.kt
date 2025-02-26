package com.example.androidtbc.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDetailDto(
    val id: Int = 0,
    val title: String = "",
    @SerialName("original_title")
    val originalTitle: String = "",
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    val overview: String = "",
    @SerialName("release_date")
    val releaseDate: String = "",
    @SerialName("vote_average")
    val voteAverage: Double = 0.0,
    @SerialName("vote_count")
    val voteCount: Int = 0,
    val runtime: Int? = null,
    val genres: List<Genre>? = null,
    val status: String = "",
    val tagline: String? = null,
    val budget: Long = 0,
    val revenue: Long = 0,
    val adult: Boolean = false,
    @SerialName("original_language")
    val originalLanguage: String = "",
    val popularity: Double = 0.0,
    @SerialName("production_companies")
    val productionCompanies: List<ProductionCompany>? = null,
    @SerialName("production_countries")
    val productionCountries: List<ProductionCountry>? = null,
    @SerialName("spoken_languages")
    val spokenLanguages: List<SpokenLanguage>? = null,
    val homepage: String? = null
) {
    // Empty constructor for Firestore deserialization
    constructor() : this(
        id = 0,
        title = "",
        originalTitle = "",
        posterPath = null,
        backdropPath = null,
        overview = "",
        releaseDate = "",
        voteAverage = 0.0,
        voteCount = 0,
        runtime = null,
        genres = null,
        status = "",
        tagline = null,
        budget = 0,
        revenue = 0,
        adult = false,
        originalLanguage = "",
        popularity = 0.0,
        productionCompanies = null,
        productionCountries = null,
        spokenLanguages = null,
        homepage = null
    )
}

@Serializable
data class Genre(
    val id: Int = 0,
    val name: String = ""
) {
    // Empty constructor for Firestore deserialization
    constructor() : this(0, "")
}

@Serializable
data class ProductionCompany(
    val id: Int = 0,
    val name: String = "",
    @SerialName("logo_path")
    val logoPath: String? = null,
    @SerialName("origin_country")
    val originCountry: String = ""
) {
    // Empty constructor for Firestore deserialization
    constructor() : this(0, "", null, "")
}

@Serializable
data class ProductionCountry(
    @SerialName("iso_3166_1")
    val iso31661: String = "",
    val name: String = ""
) {
    // Empty constructor for Firestore deserialization
    constructor() : this("", "")
}

@Serializable
data class SpokenLanguage(
    @SerialName("english_name")
    val englishName: String = "",
    @SerialName("iso_639_1")
    val iso6391: String = "",
    val name: String = ""
) {
    // Empty constructor for Firestore deserialization
    constructor() : this("", "", "")
}