package com.codinguniverse.moviewreview.widgets;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;


import com.codinguniverse.moviewreview.R;
import com.codinguniverse.moviewreview.models.MovieModel;
import com.codinguniverse.moviewreview.repository.database.AppDatabase;
import com.codinguniverse.moviewreview.utils.AppExecutors;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MovieWidgetListProvider implements RemoteViewsService.RemoteViewsFactory {

    private AppDatabase mAppDatabase;
    private List<MovieModel> favMovieList;
    private Context mContext;

    public MovieWidgetListProvider(Context context, Intent intent){
        this.mContext = context;
       
    }

    @Override
    public void onCreate() {
        mAppDatabase = AppDatabase.getInstance(mContext);
        favMovieList = new ArrayList<>();
        updateFavList();

    }

    private void updateFavList(){

        final long identityToken = Binder.clearCallingIdentity();
        AppExecutors.getInstance().diskIO().execute(() -> {
           favMovieList =  mAppDatabase.movieDao().loadAllMoviesForWidget();
            Log.d(TAG, "updateFavList: size "+favMovieList.size());
        });

        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDataSetChanged() {
        if (favMovieList != null){
            favMovieList.clear();
        }
        Log.d(TAG, "onDataSetChanged: changed");
        updateFavList();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (favMovieList == null){
            return 0;
        }
        return favMovieList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.item_widget);
        Log.d(TAG, "getViewAt: here at getViews Item "+favMovieList.size());
        if (favMovieList != null && favMovieList.size() != 0){
            remoteViews.setTextViewText(R.id.widget_list_item, favMovieList.get(position).getTitle());
        }

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
