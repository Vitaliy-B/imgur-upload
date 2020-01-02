package engidea.imgurupload.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import engidea.imgurupload.R;
import engidea.imgurupload.fragments.ImageItemListFragment;
import engidea.imgurupload.model.ImageItemContent.ImageItem;
import engidea.imgurupload.utils.aLog;

public class ImageRecyclerViewAdapter extends RecyclerView.Adapter<ImageRecyclerViewAdapter.ViewHolder> {
    private final static String TAG = "ei_i";

    private final List<ImageItem> mValues;
    private final ImageItemListFragment.OnImageListFragmentListener mListener;
    private final Context context;
    private int mColumnCount;

    /**
     * @param items List<ImageItem>
     * @param context should be OnImageListFragmentListener
     */
    public ImageRecyclerViewAdapter(Context context, List<ImageItem> items, int mColumnCount) {
        this.context = context;
        mValues = items;
        this.mColumnCount = mColumnCount;

        if (!(context instanceof ImageItemListFragment.OnImageListFragmentListener)) {
            throw new ClassCastException("ImageRecyclerViewAdapter:"
                    + " activity should be OnImageListFragmentListener");
        } else {
            mListener = (ImageItemListFragment.OnImageListFragmentListener) context;
        }
    }

    @Override @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        int squareSide = context.getResources().getDisplayMetrics().widthPixels / mColumnCount;

        new Picasso.Builder(context)
//                .loggingEnabled(true)
                .listener((picassoOut, uri, exception) -> aLog.w(TAG, uri + " / " + exception))
                .build()
                .load(new File(holder.mItem.path))
                .placeholder(R.drawable.ic_photo_library_black)
                .resize(squareSide, squareSide)
                .centerCrop()
                .into(holder.imageView);

        holder.mView.setOnClickListener(v -> {
            if (holder.progressBar.getVisibility() != View.VISIBLE) {
                holder.progressBar.setVisibility(View.VISIBLE);
                mListener.onImageListInteraction(holder.mItem, (success) ->
                        holder.progressBar.post(() -> {
                            holder.progressBar.setVisibility(View.INVISIBLE);
                            if (success) {
                                mValues.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                                notifyItemRangeChanged(holder.getAdapterPosition(), mValues.size());
                            }
                }));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView imageView;
        final ProgressBar progressBar;
        ImageItem mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            imageView = view.findViewById(R.id.item_image);
            progressBar = view.findViewById(R.id.progressBar);
        }

        @Override @NonNull
        public String toString() {
            return super.toString() + "'" + mItem + "'";
        }
    }
}
