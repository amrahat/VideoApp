package net.optimusbs.videoapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.Constants;
import net.optimusbs.videoapp.UtilityClasses.FireBaseClass;
import net.optimusbs.videoapp.UtilityClasses.ItemDecoration;
import net.optimusbs.videoapp.UtilityClasses.SetUpToolbar;
import net.optimusbs.videoapp.adapters.VideoListByTagAdapter2;
import net.optimusbs.videoapp.models.Video;

import java.util.ArrayList;

public class VideosUnderTag extends Fragment {
    RecyclerView videoListUnderTag;
    String tagName;
    TextView tagCount;
    private DatabaseReference videoListRef;
    private FireBaseClass fireBaseClass;

    public VideosUnderTag() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("getactialsdf", getActivity().getLocalClassName());
        if (getActivity().getLocalClassName().equals("activities.HomeActivity")) {
            getActivity().findViewById(R.id.top_bar).setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos_under_tag, container, false);
        initializeView(view);
        fireBaseClass = new FireBaseClass(getContext());
        videoListRef = FirebaseDatabase.getInstance().getReference(Constants.VIDEO_REF);
        getArgumentData();
        return view;
    }

    private void getArgumentData() {
        tagName = getArguments().getString("tag_name");
        SetUpToolbar.setTitle(tagName, getActivity());
        getVideosByTag(tagName, videoListUnderTag);
    }

    private void initializeView(View view) {
        videoListUnderTag = (RecyclerView) view.findViewById(R.id.video_list_under_tag);
        tagCount = (TextView) view.findViewById(R.id.tag_count);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        videoListUnderTag.setLayoutManager(mLayoutManager);
        videoListUnderTag.setItemAnimator(new DefaultItemAnimator());
        videoListUnderTag.addItemDecoration(new ItemDecoration(getContext()));

        getActivity().findViewById(R.id.search_edittext).setVisibility(View.GONE);
        getActivity().findViewById(R.id.title_layout).setVisibility(View.VISIBLE);
    }

    private void getVideosByTag(final String tag, final RecyclerView recyclerView) {
        DatabaseReference tagRef = FirebaseDatabase.getInstance().getReference(Constants.TAG_REF);
        tagRef.child(tag).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> videoList = (ArrayList<String>) dataSnapshot.getValue();
                final ArrayList<Video> videos = new ArrayList<Video>();
                for (int i = 0; i < videoList.size(); i++) {
                    final int finalI = i;
                    videoListRef.child(videoList.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final Video video = dataSnapshot.getValue(Video.class);
                            if (video != null) {
                                video.setId(videoList.get(finalI));
                                fireBaseClass.getCommentRef().child(video.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        video.setCommentCount(String.valueOf(dataSnapshot.getChildrenCount()));

                                        fireBaseClass.getLikeRef().child(video.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                video.setLikeCount(String.valueOf(dataSnapshot.getChildrenCount()));
                                                videos.add(video);

                                                if (finalI == videoList.size() - 1) {
                                                    //setadapter
                                                    tagCount.setText(videos.size() + " videos found for " + tagName);

                                                    VideoListByTagAdapter2 videoListByTagAdapter = new VideoListByTagAdapter2(videos, getActivity(), tag);
                                                    recyclerView.setAdapter(videoListByTagAdapter);
                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
