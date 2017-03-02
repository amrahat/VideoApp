package net.optimusbs.videoapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.optimusbs.videoapp.adapters.VideoListByTagAdapter;
import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.Constants;
import net.optimusbs.videoapp.UtilityClasses.SetUpToolbar;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllVideos extends Fragment {

    RecyclerView recyclerView;
    int indicatorSmallColor;
    int indicatorLargeColor;

    public AllVideos() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_videos, container, false);
        initializeView(view);
        setBackGroundColor();
        getAllVideos();
        SetUpToolbar.setTitle("All Videos",getActivity());
        return view;
    }

    private void getAllVideos() {
            FirebaseDatabase.getInstance().getReference(Constants.VIDEO_REF).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    ArrayList<String> videoIds = new ArrayList<String>();
                    while (iterator.hasNext()){
                        DataSnapshot snapshot = iterator.next();
                        String videoId = snapshot.getKey();
                        videoIds.add(videoId);
                    }

                    if(videoIds.size()!=0){
                        setUpRecyclerView(videoIds);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }

    private void setUpRecyclerView(ArrayList<String> videoIds) {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        VideoListByTagAdapter videoListByTagAdapter = new VideoListByTagAdapter(videoIds, getActivity(), "");
        recyclerView.setAdapter(videoListByTagAdapter);
    }

    private void initializeView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

    }
    private void setBackGroundColor() {
        indicatorSmallColor = ContextCompat.getColor(getContext(), R.color.topbar_color);
        indicatorLargeColor = ContextCompat.getColor(getContext(), R.color.toolbar_color);
        getActivity().findViewById(R.id.my_videos).setBackgroundColor(indicatorSmallColor);
        getActivity().findViewById(R.id.home).setBackgroundColor(indicatorSmallColor);
        getActivity().findViewById(R.id.all_videos).setBackgroundColor(indicatorLargeColor);
        getActivity().findViewById(R.id.notification).setBackgroundColor(indicatorSmallColor);

    }

}
