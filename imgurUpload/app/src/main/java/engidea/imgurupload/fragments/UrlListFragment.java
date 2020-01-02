package engidea.imgurupload.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import engidea.imgurupload.R;
import engidea.imgurupload.adapters.UrlRecyclerViewAdapter;
import engidea.imgurupload.model.NameToUrl;

public class UrlListFragment extends Fragment {
//    private final static String TAG = "ei_i";
    private static final int COLUMN_COUNT = 1;

    public UrlListFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof OnImageUrlFragmentListener)) {
            throw new RuntimeException(context.toString()
                    + " must implement OnImageUrlFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
                actionBar.setTitle(R.string.title_uploaded_images);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_imageurl_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (COLUMN_COUNT <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, COLUMN_COUNT));
            }
            recyclerView.setAdapter(new UrlRecyclerViewAdapter(view.getContext()));
        }
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnImageUrlFragmentListener {
        void onImageUrlFragmentInteraction(NameToUrl item);
    }
}
