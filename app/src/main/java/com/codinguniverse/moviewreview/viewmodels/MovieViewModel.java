package com.codinguniverse.moviewreview.viewmodels;

import androidx.core.widget.ListViewAutoScrollHelper;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.codinguniverse.moviewreview.models.MovieModel;
import com.codinguniverse.moviewreview.repository.GetMovieData;

import java.util.List;

public class MovieViewModel extends ViewModel {
    private static final String TAG = "MovieViewModel";

    private MutableLiveData<List<MovieModel>> mMovies;
    private MutableLiveData<List<MovieModel>> mNewReleases;
    private MutableLiveData<List<MovieModel>> mTopRatedMovies;

    public void init(){
        if (mMovies != null && mNewReleases != null && mTopRatedMovies != null){
            return;
        }
        GetMovieData getMovieData = GetMovieData.getInstance();


        if (mMovies == null){
            mMovies = getMovieData.getMovies();
        }

        if (mNewReleases == null){
            mNewReleases = getMovieData.getNewReleases();
        }

        if (mTopRatedMovies == null){
            mTopRatedMovies = getMovieData.getTopRated();
        }


    }

    public LiveData<List<MovieModel>> getMovies(){
        return this.mMovies;
    }

    public LiveData<List<MovieModel>> getNewReleases(){
        return this.mNewReleases;
    }

    public LiveData<List<MovieModel>> getTopRated(){
        return this.mTopRatedMovies;
    }
}

