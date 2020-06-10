package com.codinguniverse.moviewreview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.codinguniverse.moviewreview.adapters.CastAdapter;
import com.codinguniverse.moviewreview.adapters.MoreImagesAdapter;
import com.codinguniverse.moviewreview.adapters.MovieAdapter;
import com.codinguniverse.moviewreview.adapters.TrailerAdapter;
import com.codinguniverse.moviewreview.models.MovieModel;
import com.codinguniverse.moviewreview.repository.database.AppDatabase;
import com.codinguniverse.moviewreview.utils.AppExecutors;
import com.codinguniverse.moviewreview.utils.GenreList;
import com.codinguniverse.moviewreview.utils.ImagePath;
import com.codinguniverse.moviewreview.viewmodels.MovieDetails;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;

public class MovieActivity extends AppCompatActivity implements MovieAdapter.OnMovieClickHandler, TrailerAdapter.TrailerClickHandler {

    private static final String TAG = "MovieActivity";

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
    private FloatingActionButton mFavoriteBtn;
    private RecyclerView castRecyclerView;
    private RecyclerView moreImagesView;
    private RecyclerView similarMovies;
    private RecyclerView mTrailersView;
    private MovieDetails movieDetailViewModel;
    private MoreImagesAdapter mMoreImagesAdapter;
    private MovieAdapter mSimilarMoviesAdapter;
    private TrailerAdapter mTrailerAdapter;
    private AppDatabase appDatabase;

    //flags
    private boolean isFavourite = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        movieDetail = (MovieModel) Objects.requireNonNull(getIntent().getExtras()).getSerializable(EXTRA_MOVIE);


        movieDetailViewModel = ViewModelProviders.of(this).get(MovieDetails.class);
        // getting database instance
        appDatabase = AppDatabase.getInstance(this);


        movieDetailViewModel.init(movieDetail.getId());
        movieDetailViewModel.initializeFavouriteMovie(appDatabase, movieDetail.getId());

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
        mTrailersView = findViewById(R.id.trailers_view);
        mFavoriteBtn = findViewById(R.id.favorite_btn);


        mCastAdapter = new CastAdapter();
        mMoreImagesAdapter = new MoreImagesAdapter();
        mSimilarMoviesAdapter = new MovieAdapter(this);
        mTrailerAdapter = new TrailerAdapter(this);

        if (movieDetail != null){
            bindViews();
        }

    }


    public void bindViews() {

        checkForFavorite();

        //   Setting up the backdrop image
        Picasso.get()
                .load(ImagePath.movieImagePathBuilder(movieDetail.getBackdropPath()))
                .placeholder(R.drawable.default_image_placeholder)
                .into(backDropImage);

        // Setting up poster image
        Picasso.get()
                .load(ImagePath.movieImagePathBuilder(movieDetail.getPosterPath()))
                .placeholder(R.drawable.default_image_placeholder)
                .into(posterImage);

        // Setting up Title, Release date, rating
        overview.setText(movieDetail.getOverview());
        movieTitle.setText(movieDetail.getTitle());

        //_________________** Converting date in month,dd yyyy format **______________________________
        String date = "";
        if (movieDetail.getReleaseDate() != null && !movieDetail.getReleaseDate().isEmpty()) {
            try {
                Date stringToDate = new SimpleDateFormat("yyyy-mm-dd", Locale.getDefault()).parse(movieDetail.getReleaseDate());
                if (stringToDate != null) {
                    long milliseconds = stringToDate.getTime();
                    date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(milliseconds);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //__________________________________________________________________________________________
        releaseDate.setText(date);

        rating.setText(String.valueOf(movieDetail.getVoteAverage()));

        // Creating the genres string
        StringBuilder genreStringBuilder = new StringBuilder();
        GenreList genreList = new GenreList();
        HashMap<Integer, String> genre = genreList.getGenres();
        Iterator<Integer> iterator = movieDetail.getGenreId().iterator();
        while (iterator.hasNext()) {
            genreStringBuilder.append(genre.get(iterator.next()));
            if (iterator.hasNext()) {
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
        setTrailersView();

        mFavoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFavouriteStatus();
            }
        });


    }

    private void checkForFavorite(){
        movieDetailViewModel.getMovieById().observe(this, movieModel -> {
            if (movieModel == null){
               mFavoriteBtn.setImageDrawable(getDrawable(R.drawable.ic_outline_favorite_border_24));
                isFavourite = false;
            }else {
                mFavoriteBtn.setImageDrawable(getDrawable(R.drawable.ic_baseline_favorite_24));
                isFavourite = true;
            }
        });
    }

    private void setTrailersView() {
        movieDetailViewModel.getTrailerLiveData().observe(this, movieTrailers -> {
            if (movieTrailers == null || movieTrailers.size() == 0){
                TextView trailersTitle = findViewById(R.id.trailers_heading);
                trailersTitle.setVisibility(View.GONE);
            }
            mTrailerAdapter.setTrailersList(movieTrailers);
        });
        LinearLayoutManager linearLayout = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        mTrailersView.setLayoutManager(linearLayout);
        mTrailersView.setAdapter(mTrailerAdapter);

    }

    private void setMovieCharacters() {
        movieDetailViewModel.getCharacters().observe(this, movieCharacters -> {
            if (movieCharacters == null || movieCharacters.size() == 0) {
                TextView movieCharacterTitle = findViewById(R.id.cast_heading);
                movieCharacterTitle.setVisibility(View.GONE);
            }
            mCastAdapter.setMovieCharacters(movieCharacters);
        });

        LinearLayoutManager linearLayout = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        castRecyclerView.setLayoutManager(linearLayout);
        castRecyclerView.setAdapter(mCastAdapter);

    }

    private void setMoreImages() {

        movieDetailViewModel.getMoreImages().observe(this, moreImagesModels -> {
            if (moreImagesModels == null || moreImagesModels.size() == 0) {
                TextView moreImagesTitle = findViewById(R.id.more_images_heading);
                moreImagesTitle.setVisibility(View.GONE);
            }
            mMoreImagesAdapter.setMoreImagesModels(moreImagesModels);
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        moreImagesView.setLayoutManager(linearLayoutManager);
        moreImagesView.setAdapter(mMoreImagesAdapter);


    }

    private void setSimilarMovies() {
        movieDetailViewModel.getSimilarMovies().observe(this, movieModels -> {
            if (movieModels == null || movieModels.size() == 0) {
                TextView simMoviesTitle = findViewById(R.id.similar_movies_heading);
                simMoviesTitle.setVisibility(View.GONE);
            }
            mSimilarMoviesAdapter.setMovieList(movieModels);
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, GRID_SPAN_FOR_SIMILAR_MOVIES, RecyclerView.HORIZONTAL, false);
        similarMovies.setLayoutManager(gridLayoutManager);
        similarMovies.setAdapter(mSimilarMoviesAdapter);
    }

    /**
     * This method adds or delete the favorite movie from the database
     * It uses executors to run this task on background thread
     */
    private void changeFavouriteStatus(){
        if (isFavourite){
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    appDatabase.movieDao().deleteMovie(movieDetail);
                    isFavourite = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mFavoriteBtn.setImageDrawable(getDrawable(R.drawable.ic_outline_favorite_border_24));
                        }
                    });
                }
            });
        }else {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    appDatabase.movieDao().insertMovie(movieDetail);
                    isFavourite = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mFavoriteBtn.setImageDrawable(getDrawable(R.drawable.ic_baseline_favorite_24));
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onMovieClick(MovieModel movie) {
        Intent movieDetail = new Intent(this, MovieActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MovieActivity.EXTRA_MOVIE, movie);
        movieDetail.putExtras(bundle);
        startActivity(movieDetail);
    }

    @Override
    public void onTrailerClickHandle(String videoId) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + videoId));
        try {
            this.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            this.startActivity(webIntent);
        }
    }
}
