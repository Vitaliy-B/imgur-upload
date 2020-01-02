package engidea.imgurupload.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import engidea.imgurupload.R;
import engidea.imgurupload.adapters.ImageRecyclerViewAdapter;
import engidea.imgurupload.model.ImageItemContent;

public class ImageItemListFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 3;

    public ImageItemListFragment() {
    }

    public static ImageItemListFragment newInstance(int columnCount) {
        ImageItemListFragment fragment = new ImageItemListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof OnImageListFragmentListener)) {
            throw new RuntimeException(context.toString()
                    + " must implement OnImageListFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new ImageRecyclerViewAdapter(context,
                    new ImageItemContent(context).ITEMS, mColumnCount));
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_images, menu);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnInteractionDone {
        void onInteractionDone(boolean success);
    }

    public interface OnImageListFragmentListener {
        void onImageListInteraction(ImageItemContent.ImageItem item, OnInteractionDone callback);
    }
}
