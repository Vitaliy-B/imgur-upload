package engidea.imgurupload.adapters;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import engidea.imgurupload.R;
import engidea.imgurupload.fragments.UrlListFragment;
import engidea.imgurupload.model.ImgurDB;
import engidea.imgurupload.model.NameToUrl;

public class UrlRecyclerViewAdapter extends RecyclerView.Adapter<UrlRecyclerViewAdapter.ViewHolder> {
//    private final static String TAG = "ei_i";
    private final List<NameToUrl> mValues;
    private final UrlListFragment.OnImageUrlFragmentListener mListener;
    private final ImgurDB imgurDB;

    public UrlRecyclerViewAdapter(Context context) {
        if (!(context instanceof UrlListFragment.OnImageUrlFragmentListener)) {
            throw new ClassCastException(context.getClass().getSimpleName() + " must implement "
                    + UrlListFragment.OnImageUrlFragmentListener.class.getSimpleName());
        }

        mValues = new ArrayList<>();
        mListener = (UrlListFragment.OnImageUrlFragmentListener) context;
        imgurDB = new ImgurDB(context);

        ((Activity) context).getLoaderManager().restartLoader(0, null,
                new LoaderManager.LoaderCallbacks<Cursor>() {
                    @Override
                    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                        return new DbUrlLoader(context, imgurDB);
                    }

                    @Override
                    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                        if (data == null || data.getCount() == 0) {
                            Toast.makeText(context, R.string.msg_no_records, Toast.LENGTH_LONG).show();
                            return;
                        }

                        int colName = data.getColumnIndex(ImgurDB.TabImgUrl.COL_NAME);
                        int colUrl = data.getColumnIndex(ImgurDB.TabImgUrl.COL_URL);
                        mValues.clear();

                        if (data.moveToFirst()) {
                            do {
                                mValues.add(new NameToUrl(data.getString(colName), data.getString(colUrl)));
                            } while (data.moveToNext());
                        }

                        notifyDataSetChanged();
                    }

                    @Override
                    public void onLoaderReset(Loader<Cursor> loader) {
                    }
                });
    }

    @Override @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_imageurl, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.tvName.setText(mValues.get(position).name);
        holder.tvUrl.setText(mValues.get(position).url);

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onImageUrlFragmentInteraction(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView tvName;
        final TextView tvUrl;
        NameToUrl mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            tvName = view.findViewById(R.id.tv_title);
            tvUrl = view.findViewById(R.id.tv_url);
        }

        @Override @NonNull
        public String toString() {
            return super.toString() + " '" + mItem + "'";
        }
    }

    public static class DbUrlLoader extends AsyncTaskLoader<Cursor> {
        private final ImgurDB imgurDB;

        DbUrlLoader(@NonNull Context context, ImgurDB imgurDB) {
            super(context);
            this.imgurDB = imgurDB;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }

        @Nullable @Override
        public Cursor loadInBackground() {
            return imgurDB.queryUrls();
        }
    }
}
