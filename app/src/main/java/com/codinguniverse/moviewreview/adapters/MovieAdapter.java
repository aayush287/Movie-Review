package com.codinguniverse.moviewreview.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codinguniverse.moviewreview.R;
import com.codinguniverse.moviewreview.models.MovieModel;
import com.codinguniverse.moviewreview.utils.ImagePath;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<MovieModel> mMovieList;
    private OnMovieClickHandler mOnMovieClickHandler;

    /*
        Interface to handle clicks on movie
     */
    public interface OnMovieClickHandler{
        void onMovieClick(MovieModel movie);
    }

    public MovieAdapter(OnMovieClickHandler onMovieClickHandler) {
        mOnMovieClickHandler = onMovieClickHandler;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_release_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        final MovieModel movie = mMovieList.get(position);

        holder.bind(movie);

    }

    @Override
    public int getItemCount() {
        if (mMovieList == null){
            return 0;
        }
        return mMovieList.size();
    }

    public void setMovieList(List<MovieModel> movieList) {
        mMovieList = movieList;
        notifyDataSetChanged();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View mView;
        private ImagePath mImagePath;

    public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mImagePath = new ImagePath();

            itemView.setOnClickListener(this);
        }

        public void bind(MovieModel movie){
            ImageView imageView = mView.findViewById(R.id.new_release_img);
            TextView title = mView.findViewById(R.id.title);

            String movieTitle = movie.getTitle();

            if (movieTitle.length() > 20){
                movieTitle = movieTitle.substring(0,17) + "...";
            }

            title.setText(movieTitle);

            Picasso.get()
                    .load(mImagePath.movieImagePathBuilder(movie.getPosterPath()))
                    .into(imageView);


        }

        @Override
        public void onClick(View v) {
            if (mOnMovieClickHandler != null){
                mOnMovieClickHandler.onMovieClick(mMovieList.get(getAdapterPosition()));
            }
        }
    }

}
