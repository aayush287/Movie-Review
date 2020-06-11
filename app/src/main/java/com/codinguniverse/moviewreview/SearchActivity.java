package com.codinguniverse.moviewreview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codinguniverse.moviewreview.adapters.MovieAdapter;
import com.codinguniverse.moviewreview.models.MovieModel;
import com.codinguniverse.moviewreview.repository.GetMovieData;

import java.util.List;

public class SearchActivity extends AppCompatActivity implements MovieAdapter.OnMovieClickHandler {

    private SearchView mSearchView;
    private GetMovieData mGetMovieData;
    private RecyclerView mSearchedMovies;
    private MovieAdapter mSearchedAdapter;
    private static final int PORTRAIT_MODE_SPAN = 2;
    private static final int LANDSCAPE_MODE_SPAN = 4;
    private LifecycleOwner mLifecycleOwner;
    private ProgressBar mProgressBar;
    private TextView noMovieFoundText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mLifecycleOwner = this;

        mGetMovieData = GetMovieData.getInstance();

        mSearchedMovies = findViewById(R.id.searched_movies);
        mProgressBar = findViewById(R.id.search_pb_indicator);
        noMovieFoundText = findViewById(R.id.no_movies_view);

        mSearchedAdapter = new MovieAdapter(this);
        GridLayoutManager gridLayoutManager;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            gridLayoutManager = new GridLayoutManager(this, PORTRAIT_MODE_SPAN);
        }else {
            gridLayoutManager = new GridLayoutManager(this, LANDSCAPE_MODE_SPAN);
        }

        mSearchedMovies.setLayoutManager(gridLayoutManager);
        mSearchedMovies.setAdapter(mSearchedAdapter);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) findViewById(R.id.search_view);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());

        mSearchView.setSearchableInfo(searchableInfo);
        mSearchView.setIconifiedByDefault(false);



        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchMovie searchMovie = new SearchMovie();
                searchMovie.execute(query);
                mSearchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }


    class SearchMovie extends AsyncTask<String, Void, LiveData<List<MovieModel>>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
            noMovieFoundText.setVisibility(View.INVISIBLE);
        }

        @Override
        protected LiveData<List<MovieModel>> doInBackground(String... strings) {
            String search = strings[0];

            return mGetMovieData.getMoviesBySearch(search);
        }

        @Override
        protected void onPostExecute(LiveData<List<MovieModel>> movieModels) {
            super.onPostExecute(movieModels);
            movieModels.observe(mLifecycleOwner, (Observer<List<MovieModel>>) movieModels1 -> {
                if (movieModels1 == null || movieModels1.size() == 0){
                    noMovieFoundText.setVisibility(View.VISIBLE);
                }
                mProgressBar.setVisibility(View.INVISIBLE);
                mSearchedAdapter.setMovieList(movieModels1);
            });
        }
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