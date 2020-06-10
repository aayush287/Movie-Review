package com.codinguniverse.moviewreview.viewmodels;

import androidx.core.widget.ListViewAutoScrollHelper;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.codinguniverse.moviewreview.models.MovieModel;
import com.codinguniverse.moviewreview.repository.GetMovieData;
import com.codinguniverse.moviewreview.repository.database.AppDatabase;

import java.util.List;

public class MovieViewModel extends ViewModel {
    private MutableLiveData<List<MovieModel>> mMovies;
    private MutableLiveData<List<MovieModel>> mNewReleases;
    private MutableLiveData<List<MovieModel>> mTopRatedMovies;
    private LiveData<List<MovieModel>> mFavouriteMovies;

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

    public void initializeFavMovie(AppDatabase appDatabase){
        if (mFavouriteMovies != null){
            return;
        }

        mFavouriteMovies = appDatabase.movieDao().loadAllMovies();
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

    public LiveData<List<MovieModel>> getFavouriteMovies(){
        return mFavouriteMovies;
    }
}

