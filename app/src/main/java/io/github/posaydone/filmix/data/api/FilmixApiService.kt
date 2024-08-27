package io.github.posaydone.filmix.data.api
import io.github.posaydone.filmix.data.model.MovieCard
import io.github.posaydone.filmix.data.model.MovieDetails
import io.github.posaydone.filmix.data.model.MovieList
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path

interface FilmixApiService {

    @GET("list?category=s7&limit=48")
    suspend fun getMovies(
//        @Path("limit") limit: Int,
    ): MovieList

    @GET("list?search={query}&limit=48")
    suspend fun searchMovies(@Query("query") query: String): List<MovieCard>

    @GET("post/{movieId}/details")
    suspend fun getMovieDetails(@Path("movieId") movieId: Int): MovieDetails

    @GET("post/{movieId}/video-links")
    suspend fun getSeriesOrMovie(
        @Path("movieId") movieId: Int,
    ): Response<ResponseBody>
}
