package com.codinguniverse.moviewreview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.codinguniverse.moviewreview.adapters.MovieAdapter;
import com.codinguniverse.moviewreview.models.MovieModel;
import com.codinguniverse.moviewreview.repository.database.AppDatabase;
import com.codinguniverse.moviewreview.viewmodels.MovieViewModel;


public class MainActivity extends AppCompatActivity implements MovieAdapter.OnMovieClickHandler{
    private MovieViewModel mMovieViewModel;
    private MovieAdapter mNewReleaseAdapter;
    private MovieAdapter mPopularMoviesAdapter;
    private MovieAdapter mTopRatedAdapter;
    private MovieAdapter mFavoriteAdapter;
    private RecyclerView mNewReleaseView;
    private RecyclerView mPopularView;
    private RecyclerView mTopRatedView;
    private RecyclerView mFavoriteView;
    private ScrollView mMainView;
    private ProgressBar mProgressBar;

    // Boolean to check if all data is there or not
    private static boolean UP_COMING_FLAG =  false;
    private static boolean POPULAR_FLAG = false;
    private static boolean TOP_RATED_FLAG = false;
    private static boolean FAVORITE_FLAG  = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMovieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        mNewReleaseView = findViewById(R.id.new_release_list);
        mPopularView = findViewById(R.id.popular_movies_list);
        mTopRatedView = findViewById(R.id.top_rated_list);
        mProgressBar = findViewById(R.id.pb_loading_bar);
        mMainView = findViewById(R.id.main_scroll_view);
        mFavoriteView = findViewById(R.id.favorite_movies_list);

        AppDatabase appDatabase = AppDatabase.getInstance(this);

        mNewReleaseAdapter = new MovieAdapter(this);
        mPopularMoviesAdapter = new MovieAdapter(this);
        mTopRatedAdapter = new MovieAdapter(this);
        mFavoriteAdapter = new MovieAdapter(this);


        mMovieViewModel.init();
        mMovieViewModel.initializeFavMovie(appDatabase);

        showProgressBar();

        /*
            Setting all the views
         */
        setNewReleases();
        setPopularView();
        setTopRatedView();
        setFavoriteMovies();


    }
    //________________** Setting all views i.e. New release, popular, top rated, fav**_____________
    private void setNewReleases(){
        mMovieViewModel.getNewReleases().observe(this, movieModels -> {
            if(movieModels == null || movieModels.size() == 0){
                TextView newReleaseTitle = findViewById(R.id.new_release);
                newReleaseTitle.setVisibility(View.GONE);
            }
            mNewReleaseAdapter.setMovieList(movieModels);
            UP_COMING_FLAG = true;
            hideProgressBar();
        });

        LinearLayoutManager linearLayout  = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        mNewReleaseView.setLayoutManager(linearLayout);
        mNewReleaseView.setAdapter(mNewReleaseAdapter);
    }

    private void setPopularView(){
        mMovieViewModel.getMovies().observe(this, movieModels -> {

            if(movieModels == null || movieModels.size() == 0){
                TextView popularTitle = findViewById(R.id.popular_movies);
                popularTitle.setVisibility(View.GONE);
            }
            mPopularMoviesAdapter.setMovieList(movieModels);

            POPULAR_FLAG = true;
            hideProgressBar();
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        mPopularView.setAdapter(mPopularMoviesAdapter);
        mPopularView.setLayoutManager(linearLayoutManager);

    }

    private void setTopRatedView(){
        mMovieViewModel.getTopRated().observe(this, movieModels -> {
            if(movieModels == null || movieModels.size() == 0){
                TextView topRatedTitle = findViewById(R.id.top_rated_title);
                topRatedTitle.setVisibility(View.GONE);
            }
            mTopRatedAdapter.setMovieList(movieModels);

            TOP_RATED_FLAG = true;
            hideProgressBar();
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        mTopRatedView.setAdapter(mTopRatedAdapter);
        mTopRatedView.setLayoutManager(linearLayoutManager);
    }


    private void setFavoriteMovies(){
        mMovieViewModel.getFavouriteMovies().observe(this, movieModels -> {
            if (movieModels == null || movieModels.size() == 0){
                TextView favMovieTitle = findViewById(R.id.favorite_movies);
                favMovieTitle.setVisibility(View.GONE);
            }
            FAVORITE_FLAG = true;
            hideProgressBar();
            mFavoriteAdapter.setMovieList(movieModels);
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        mFavoriteView.setAdapter(mFavoriteAdapter);
        mFavoriteView.setLayoutManager(linearLayoutManager);
    }
    //__________________________________** Ending of views setup**__________________________________

    // __________________** Show and Hide method for progress bar for loading data **_______________

    /**
     * Hide Progress Method is called from three places because we don't know which
     * type of movie data loads in the last. So each type of data calling hide progress bar
     * by setting it's flag to true. When all flags are true then progress bar
     * will hide.
     */
    private void hideProgressBar(){
        if (UP_COMING_FLAG && POPULAR_FLAG && TOP_RATED_FLAG && FAVORITE_FLAG){

            mProgressBar.setVisibility(View.GONE);

            mMainView.setVisibility(View.VISIBLE);

        }
    }

    private void showProgressBar(){
        mProgressBar.setVisibility(View.VISIBLE);
        mMainView.setVisibility(View.INVISIBLE);
    }

    // _____________________** End of progress bar methods **_______________________________________

    @Override
    public void onMovieClick(MovieModel movie) {
        Intent movieDetail = new Intent(this, MovieActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MovieActivity.EXTRA_MOVIE,movie);
        movieDetail.putExtras(bundle);
        startActivity(movieDetail);
    }
}
