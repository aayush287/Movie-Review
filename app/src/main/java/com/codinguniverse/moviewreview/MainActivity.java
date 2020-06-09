package com.codinguniverse.moviewreview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.codinguniverse.moviewreview.adapters.MovieAdapter;
import com.codinguniverse.moviewreview.models.MovieModel;
import com.codinguniverse.moviewreview.viewmodels.MovieViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnMovieClickHandler{
    private MovieViewModel mMovieViewModel;
    private MovieAdapter mNewReleaseAdapter;
    private MovieAdapter mPopularMoviesAdapter;
    private MovieAdapter mTopRatedAdapter;
    private RecyclerView mNewReleaseView;
    private RecyclerView mPopularView;
    private RecyclerView mTopRatedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMovieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        mNewReleaseView = findViewById(R.id.new_release_list);
        mPopularView = findViewById(R.id.popular_movies_list);
        mTopRatedView = findViewById(R.id.top_rated_list);

        mNewReleaseAdapter = new MovieAdapter(this);
        mPopularMoviesAdapter = new MovieAdapter(this);
        mTopRatedAdapter = new MovieAdapter(this);


        mMovieViewModel.init();

        /*
            Setting all the views
         */
        setNewReleases();
        setPopularView();
        setTopRatedView();


    }

    private void setNewReleases(){
        mMovieViewModel.getNewReleases().observe(this, new Observer<List<MovieModel>>() {
            @Override
            public void onChanged(List<MovieModel> movieModels) {
                mNewReleaseAdapter.setMovieList(movieModels);
            }
        });

        LinearLayoutManager linearLayout  = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        mNewReleaseView.setLayoutManager(linearLayout);
        mNewReleaseView.setAdapter(mNewReleaseAdapter);
    }

    private void setPopularView(){
        mMovieViewModel.getMovies().observe(this, new Observer<List<MovieModel>>() {
            @Override
            public void onChanged(List<MovieModel> movieModels) {
                mPopularMoviesAdapter.setMovieList(movieModels);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        mPopularView.setAdapter(mPopularMoviesAdapter);
        mPopularView.setLayoutManager(linearLayoutManager);

    }

    private void setTopRatedView(){
        mMovieViewModel.getTopRated().observe(this, new Observer<List<MovieModel>>() {
            @Override
            public void onChanged(List<MovieModel> movieModels) {
                mTopRatedAdapter.setMovieList(movieModels);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        mTopRatedView.setAdapter(mTopRatedAdapter);
        mTopRatedView.setLayoutManager(linearLayoutManager);
    }


    @Override
    public void onMovieClick(MovieModel movie) {
        Intent movieDetail = new Intent(this, MovieActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MovieActivity.EXTRA_MOVIE,movie);
        movieDetail.putExtras(bundle);
        startActivity(movieDetail);
    }
}
