package net.optimusbs.videoapp.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.optimusbs.videoapp.Adapters.SavedSearchAdapter;
import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.SharedPreferenceClass;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * A simple {@link Fragment} subclass.
 */
public class SavedSearch extends Fragment {

    RecyclerView recyclerView;
    public SavedSearch() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_saved_search, container, false);
        initializeView(view);
        hideSearchEditText();
        return view;
    }
    private void hideSearchEditText() {
        getActivity().findViewById(R.id.search_layout).setVisibility(View.GONE);
        getActivity().findViewById(R.id.title_layout).setVisibility(View.VISIBLE);
        //drawerFragment.showHamburgetIcon();

    }
    private void initializeView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.saved_searches_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        HashSet<String> savedSearchesSet = SharedPreferenceClass.getAllSavedSearch(getContext());

        ArrayList<String> list = new ArrayList<>(savedSearchesSet);

        SavedSearchAdapter savedSearchAdapter = new SavedSearchAdapter(list,getContext());
        recyclerView.setAdapter(savedSearchAdapter);

    }

}
