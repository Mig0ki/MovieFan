package com.example.moviefan.api

import com.example.moviefan.api.models.GenreResponse
import com.squareup.moshi.Moshi
import com.example.moviefan.api.models.MovieResponse
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

//obiekt
object RetrofitClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    //funkcja tworzaca i zwracajaca instancje klienta Retrofit
    private fun getClient(): Retrofit {
        //tworzenie instancji Moshi(konwerter JSON)
        val moshi = Moshi.Builder().build()

        //deklaracja klienta
        val client = OkHttpClient.Builder()
            .addInterceptor { chain -> //przechwytywanie żądania
                //chain.request (pobiera oryginalne żądanie, które ma być wysłane)
                val original = chain.request()
                val originalUrl = original.url

                //nowy URL z dodatkowym parametrem: API KEY
                val url = originalUrl.newBuilder()
                    .addQueryParameter("api_key", "ADD_API_KEY_HERE") //klucz API
                    .build()

                //nowe żądanie
                val request = original.newBuilder()
                    .url(url)
                    .build()

                chain.proceed(request)
            }
            .build()

        //tworzenie instancji Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        return retrofit
    }

    val apiServiceInstance: ApiMovie by lazy {
        getClient().create(ApiMovie::class.java)
    }
}

//interfejs API
interface ApiMovie {
    //deklaracje metod opdowiadających zapytaniom HTTP
    @GET("discover/movie")
    fun getUpcomingMovies(
        @Query("primary_release_date.gte") fromDate: String,
        @Query("primary_release_date.lte") toDate: String,
        @Query("region") region: String = "PL",
//        @Query("with_release_type") releaseType: String = "1",
//        @Query("sort_by") sortBy: String = "primary_release_date.asc"
    ): Call<MovieResponse>

    @GET("genre/movie/list")
    suspend fun getGenres(): GenreResponse

    @GET("movie/popular")
    suspend fun getRandomMovies(
        @Query("page") page: Int = 1
    ) : MovieResponse

    @GET("discover/movie")
    suspend fun getMoviesByGenre(
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int
    ): MovieResponse
}
