package com.codinguniverse.moviewreview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.codinguniverse.moviewreview.adapters.CastAdapter;
import com.codinguniverse.moviewreview.adapters.MoreImagesAdapter;
import com.codinguniverse.moviewreview.adapters.MovieAdapter;
import com.codinguniverse.moviewreview.models.MoreImagesModel;
import com.codinguniverse.moviewreview.models.MovieCharacters;
import com.codinguniverse.moviewreview.models.MovieModel;
import com.codinguniverse.moviewreview.utils.GenreList;
import com.codinguniverse.moviewreview.utils.ImagePath;
import com.codinguniverse.moviewreview.viewmodels.MovieDetails;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class MovieActivity extends AppCompatActivity implements MovieAdapter.OnMovieClickHandler {


    // Constants
    public static final String EXTRA_MOVIE = "movie";
    private static final int GRID_SPAN_FOR_SIMILAR_MOVIES = 2;

    // Views and Adapters
    private MovieModel movieDetail;
    private CastAdapter mCastAdapter;
    private ImageView backDropImage;
    private ImageView posterImage;
    private TextView movieTitle;
    private TextView releaseDate;
    private TextView rating;
    private TextView genres;
    private TextView overview;
    private RecyclerView castRecyclerView;
    private RecyclerView moreImagesView;
    private RecyclerView similarMovies;
    private MovieDetails movieDetailViewModel;
    private MoreImagesAdapter mMoreImagesAdapter;
    private MovieAdapter mSimilarMoviesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        movieDetail = (MovieModel) Objects.requireNonNull(getIntent().getExtras()).getSerializable(EXTRA_MOVIE);


        movieDetailViewModel = ViewModelProviders.of(this).get(MovieDetails.class);

        movieDetailViewModel.init(movieDetail.getId());

        //    Retrieving the views
        backDropImage = findViewById(R.id.back_drop_image);
        posterImage = findViewById(R.id.poster_image);
        overview = findViewById(R.id.overview);
        movieTitle = findViewById(R.id.movie_title);
        releaseDate = findViewById(R.id.release_date);
        rating = findViewById(R.id.rating);
        genres = findViewById(R.id.genre_text);
        castRecyclerView = findViewById(R.id.cast_view);
        moreImagesView = findViewById(R.id.more_images_view);
        similarMovies = findViewById(R.id.sim_movie_view);


        mCastAdapter = new CastAdapter();
        mMoreImagesAdapter = new MoreImagesAdapter();
        mSimilarMoviesAdapter = new MovieAdapter(this);

        bindViews();


    }


    public void bindViews(){


        //   Setting up the backdrop image
        Picasso.get()
                .load(ImagePath.movieImagePathBuilder(movieDetail.getBackdropPath()))
                .into(backDropImage);

        // Setting up poster image
        Picasso.get()
                .load(ImagePath.movieImagePathBuilder(movieDetail.getPosterPath()))
                .into(posterImage);

        // Setting up Title, Release date, rating
        overview.setText(movieDetail.getOverview());
        movieTitle.setText(movieDetail.getTitle());
        releaseDate.setText(movieDetail.getReleaseDate());
        rating.setText(String.valueOf(movieDetail.getVoteAverage()));

        // Creating the genres string
        StringBuilder genreStringBuilder = new StringBuilder();
        GenreList genreList = new GenreList();
        HashMap<Integer, String> genre = genreList.getGenres();
        Iterator<Integer> iterator = movieDetail.getGenreId().iterator();
        while (iterator.hasNext()){
            genreStringBuilder.append(genre.get(iterator.next()));
            if (iterator.hasNext()){
                genreStringBuilder.append(" | ");
            }
        }

        genres.setText(genreStringBuilder.toString());

        /*
            Calling methods to set cast, more Images and similar movies
         */
        setMovieCharacters();
        setMoreImages();
        setSimilarMovies();


    }

    private void setMovieCharacters(){
        movieDetailViewModel.getCharacters().observe(this, new Observer<List<MovieCharacters>>() {
            @Override
            public void onChanged(List<MovieCharacters> movieCharacters) {
                mCastAdapter.setMovieCharacters(movieCharacters);
            }
        });

        LinearLayoutManager linearLayout  = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        castRecyclerView.setLayoutManager(linearLayout);
        castRecyclerView.setAdapter(mCastAdapter);

    }

    private void setMoreImages(){

        movieDetailViewModel.getMoreImages().observe(this, new Observer<List<MoreImagesModel>>() {
            @Override
            public void onChanged(List<MoreImagesModel> moreImagesModels) {
                mMoreImagesAdapter.setMoreImagesModels(moreImagesModels);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        moreImagesView.setLayoutManager(linearLayoutManager);
        moreImagesView.setAdapter(mMoreImagesAdapter);


    }

    private void setSimilarMovies(){
        movieDetailViewModel.getSimilarMovies().observe(this, new Observer<List<MovieModel>>() {
            @Override
            public void onChanged(List<MovieModel> movieModels) {
                if (movieModels == null){
                    TextView simMoviesTitle = findViewById(R.id.similar_movies_heading);
                    simMoviesTitle.setVisibility(View.GONE);
                }
                mSimilarMoviesAdapter.setMovieList(movieModels);
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,GRID_SPAN_FOR_SIMILAR_MOVIES, RecyclerView.HORIZONTAL, false);
        similarMovies.setLayoutManager(gridLayoutManager);
        similarMovies.setAdapter(mSimilarMoviesAdapter);
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
