package net.optimusbs.videoapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.optimusbs.videoapp.adapters.VideoListByTagAdapter;
import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.Constants;
import net.optimusbs.videoapp.UtilityClasses.SetUpToolbar;

import java.util.ArrayList;

public class VideosUnderTag extends Fragment {
    RecyclerView videoListUnderTag;
    String tagName;
    public VideosUnderTag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos_under_tag, container, false);
        initializeView(view);
        getArgumentData();
        return view;
    }

    private void getArgumentData() {
        tagName = getArguments().getString("tag_name");
        SetUpToolbar.setTitle(tagName,getActivity());
        getVideosByTag(tagName,videoListUnderTag);
    }

    private void initializeView(View view) {
        videoListUnderTag = (RecyclerView) view.findViewById(R.id.video_list_under_tag);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        videoListUnderTag.setLayoutManager(mLayoutManager);
        videoListUnderTag.setItemAnimator(new DefaultItemAnimator());

        getActivity().findViewById(R.id.search_edittext).setVisibility(View.GONE);
        getActivity().findViewById(R.id.title_layout).setVisibility(View.VISIBLE);
    }

    private void getVideosByTag(final String tag, final RecyclerView recyclerView) {
        DatabaseReference tagRef = FirebaseDatabase.getInstance().getReference(Constants.TAG_REF);
        tagRef.child(tag).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> videoList = (ArrayList<String>) dataSnapshot.getValue();
                VideoListByTagAdapter videoListByTagAdapter = new VideoListByTagAdapter(videoList, getActivity(),tag);
                recyclerView.setAdapter(videoListByTagAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
