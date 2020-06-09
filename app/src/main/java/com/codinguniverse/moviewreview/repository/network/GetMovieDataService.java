package com.codinguniverse.moviewreview.repository.network;

import com.codinguniverse.moviewreview.models.MoreImagesResponse;
import com.codinguniverse.moviewreview.models.MovieCharacterResponse;
import com.codinguniverse.moviewreview.models.MovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetMovieDataService {
    /**
     * Query to get popular movies
     * @param apiKey provided by TmDb
     * @return movie response with top 20 movies of popular type
     */
    @GET("movie/popular")
    Call<MovieResponse> getPopularMovie( @Query("api_key") String apiKey);

    /**
     * Query to get top rated movies
     * @param apiKey provided by TmDb
     * @return movie response with top 20 movies of top rated type
     */
    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies( @Query("api_key") String apiKey);

    /**
     * Query to get upcoming movies
     * @param apiKey provided by TmDb
     * @return movie response with top 20 movies of upcoming type
     */
    @GET("movie/upcoming")
    Call<MovieResponse> getNewReleases( @Query("api_key") String apiKey);

    /**
     * Query to get similar movies of a specific movies
     * @param movieId id of the selected movie
     * @param apiKey provided by TmDb
     * @return list of similar movies
     */
    @GET("movie/{movie_id}/similar")
    Call<MovieResponse> getSimilarMovies(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

    /**
     * Query to get Character
     * @param movieId id of movie to get movieId
     * @param apiKey provided by TmDb
     * @return Character response of requested movie
     */
    @GET("movie/{movie_id}/credits")
    Call<MovieCharacterResponse> getCharacter(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

    /**
     * Query to get more Images
     * @param movieId id of movie
     * @param apiKey provided by TmDb
     * @return More Images from requested movie
     */
    @GET("movie/{movie_id}/images")
    Call<MoreImagesResponse> getMoreImages(@Path("movie_id") int movieId, @Query("api_key") String apiKey);


}
