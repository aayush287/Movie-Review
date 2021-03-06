package com.codinguniverse.moviewreview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.codinguniverse.moviewreview.adapters.MovieAdapter;
import com.codinguniverse.moviewreview.models.MovieModel;
import com.codinguniverse.moviewreview.repository.database.AppDatabase;
import com.codinguniverse.moviewreview.viewmodels.MovieViewModel;
import com.codinguniverse.moviewreview.widgets.MovieReviewWidget;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;


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
    private AdView mBannerAd;
    private TextView favMovieTitle;
    private TextView popularTitle;
    private TextView newReleaseTitle;
    private TextView topRatedTitle;

    /*
        module scope because we need to dismiss it in onStop
        e.g. when orientation changes to avoid memory leaks.
    */
    private AlertDialog mDialog = null;


    private FirebaseAnalytics mFirebaseAnalytics;

    // Boolean to check if all data is there or not
    private static boolean UP_COMING_FLAG =  false;
    private static boolean POPULAR_FLAG = false;
    private static boolean TOP_RATED_FLAG = false;
    private static boolean FAVORITE_FLAG  = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setting toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        //getting instance of fire
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Initializing the Mobile ad
        MobileAds.initialize(this, initializationStatus -> {
        });


        mMovieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        mNewReleaseView = findViewById(R.id.new_release_list);
        mPopularView = findViewById(R.id.popular_movies_list);
        mTopRatedView = findViewById(R.id.top_rated_list);
        mProgressBar = findViewById(R.id.pb_loading_bar);
        mMainView = findViewById(R.id.main_scroll_view);
        mFavoriteView = findViewById(R.id.favorite_movies_list);
        mBannerAd = findViewById(R.id.banner_ad);
        newReleaseTitle = findViewById(R.id.new_release);
        popularTitle = findViewById(R.id.popular_movies);
        topRatedTitle = findViewById(R.id.top_rated_title);
        favMovieTitle = findViewById(R.id.favorite_movies);

        AppDatabase appDatabase = AppDatabase.getInstance(this);

        mNewReleaseAdapter = new MovieAdapter(this);
        mPopularMoviesAdapter = new MovieAdapter(this);
        mTopRatedAdapter = new MovieAdapter(this);
        mFavoriteAdapter = new MovieAdapter(this);

        // Adding ads;
        AdRequest adRequest = new AdRequest.Builder().build();
        mBannerAd.loadAd(adRequest);


        mMovieViewModel.init();
        mMovieViewModel.initializeFavMovie(appDatabase);

        showProgressBar();

        fireBaseLogs("2", "App started");

        /*
            Setting all the views
         */
        setNewReleases();
        setPopularView();
        setTopRatedView();
        setFavoriteMovies();

        // Firebase message Topic subscribe
        FirebaseMessaging.getInstance().subscribeToTopic("update");

    }
    //________________** Setting all views i.e. New release, popular, top rated, fav**_____________
    private void setNewReleases(){
        mMovieViewModel.getNewReleases().observe(this, movieModels -> {
            if(movieModels == null || movieModels.size() == 0){
                newReleaseTitle.setVisibility(View.GONE);
            }else {
                newReleaseTitle.setVisibility(View.VISIBLE);
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
                popularTitle.setVisibility(View.GONE);
            }else {
                popularTitle.setVisibility(View.VISIBLE);
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
                topRatedTitle.setVisibility(View.GONE);
            }else {
                topRatedTitle.setVisibility(View.VISIBLE);
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
                favMovieTitle.setVisibility(View.GONE);
            }else {
                favMovieTitle.setVisibility(View.VISIBLE);
            }

            updateWidgets();
            FAVORITE_FLAG = true;
            hideProgressBar();
            mFavoriteAdapter.setMovieList(movieModels);
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        mFavoriteView.setAdapter(mFavoriteAdapter);
        mFavoriteView.setLayoutManager(linearLayoutManager);
    }
    //__________________________________** Ending of views setup**__________________________________

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_about:
                showAbout();
                return true;
            case R.id.action_twitter:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name="+getString(R.string.twitter_username))));
                }catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/"+getString(R.string.twitter_username))));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * This method shows the about dialog of the app
     */
    private void showAbout() {
        View messageView = getLayoutInflater().inflate(R.layout.about,null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);

        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            if ((mDialog != null) && (mDialog.isShowing())){
                mDialog.dismiss();
            }
        });

        builder.setView(messageView);

        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(true);


        TextView tv = messageView.findViewById(R.id.about_version);
        String versionText = "v" + BuildConfig.VERSION_NAME;
        tv.setText(versionText);

        mDialog.show();
    }

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

            mBannerAd.setVisibility(View.VISIBLE);

        }
    }

    private void showProgressBar(){
        mProgressBar.setVisibility(View.VISIBLE);
        mMainView.setVisibility(View.INVISIBLE);
        mBannerAd.setVisibility(View.INVISIBLE);
    }

    // _____________________** End of progress bar methods **_______________________________________

    /**
     * method to log events of app
     * @param id id of item
     * @param log string
     */
    private void fireBaseLogs(String id, String log){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, log);

        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @Override
    public void onMovieClick(MovieModel movie) {
        fireBaseLogs("1", movie.getTitle() + " clicked");
        Intent movieDetail = new Intent(this, MovieActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MovieActivity.EXTRA_MOVIE,movie);
        movieDetail.putExtras(bundle);
        startActivity(movieDetail);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void updateWidgets(){
        ComponentName componentName = new ComponentName(this, MovieReviewWidget.class);
        int[] ids = AppWidgetManager.getInstance(this).getAppWidgetIds(componentName);
        Intent intent = new Intent(this, MovieReviewWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }
}
