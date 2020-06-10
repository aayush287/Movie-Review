package com.codinguniverse.moviewreview.viewmodels;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.codinguniverse.moviewreview.models.MoreImagesModel;
import com.codinguniverse.moviewreview.models.MovieCharacters;
import com.codinguniverse.moviewreview.models.MovieModel;
import com.codinguniverse.moviewreview.models.TrailerModel;
import com.codinguniverse.moviewreview.repository.GetMovieData;
import com.codinguniverse.moviewreview.repository.database.AppDatabase;

import java.util.List;


public class MovieDetails extends ViewModel {

    private MutableLiveData<List<MovieCharacters>> mMovieCharacters;
    private MutableLiveData<List<MoreImagesModel>> mMoreImages;
    private MutableLiveData<List<MovieModel>> mSimilarMovies;
    private MutableLiveData<List<TrailerModel>> mTrailerLiveData;
    private LiveData<MovieModel> mMovieById;

    public void init(int movieId){
        if (mMovieCharacters != null && mMoreImages != null && mSimilarMovies != null && mTrailerLiveData == null){
            return;
        }

        GetMovieData getMovieData = GetMovieData.getInstance();

        if (mMovieCharacters == null){
            mMovieCharacters = getMovieData.getCredits(movieId);
        }

        if (mMoreImages == null){
            mMoreImages = getMovieData.getMoreImages(movieId);
        }

        if (mSimilarMovies == null){
            mSimilarMovies = getMovieData.getSimilarMovies(movieId);
        }

        if (mTrailerLiveData == null){
            mTrailerLiveData = getMovieData.getMoviesTrailer(movieId);
        }


    }

    public void initializeFavouriteMovie(AppDatabase appDatabase, int id){
        if (mMovieById != null){
            return;
        }

        mMovieById = appDatabase.movieDao().loadMovieById(id);
    }

    public LiveData<List<MovieCharacters>> getCharacters(){
        return this.mMovieCharacters;
    }

    public LiveData<List<MoreImagesModel>> getMoreImages(){
        return this.mMoreImages;
    }

    public LiveData<List<MovieModel>> getSimilarMovies(){
        return this.mSimilarMovies;
    }

    public LiveData<List<TrailerModel>> getTrailerLiveData() {
        return mTrailerLiveData;
    }

    public LiveData<MovieModel> getMovieById(){
        return mMovieById;
    }
}
