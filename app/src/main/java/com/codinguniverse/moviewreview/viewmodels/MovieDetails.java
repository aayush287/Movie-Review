package com.codinguniverse.moviewreview.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.codinguniverse.moviewreview.models.MoreImagesModel;
import com.codinguniverse.moviewreview.models.MovieCharacters;
import com.codinguniverse.moviewreview.models.MovieModel;
import com.codinguniverse.moviewreview.repository.GetMovieData;

import java.util.List;

import static android.content.ContentValues.TAG;

public class MovieDetails extends ViewModel {

    private MutableLiveData<List<MovieCharacters>> mMovieCharacters;
    private MutableLiveData<List<MoreImagesModel>> mMoreImages;
    private MutableLiveData<List<MovieModel>> mSimilarMovies;

    public void init(int movieId){
        if (mMovieCharacters != null && mMoreImages != null && mSimilarMovies != null){
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
}
