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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.Constants;
import net.optimusbs.videoapp.UtilityClasses.FireBaseClass;
import net.optimusbs.videoapp.UtilityClasses.SetUpToolbar;
import net.optimusbs.videoapp.adapters.NotificationAdapter;
import net.optimusbs.videoapp.models.FirebaseNotification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    private FireBaseClass fireBaseClass;
    private ArrayList<FirebaseNotification> notifications;
    private String TAG = "NotificationFragment";
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fireBaseClass = new FireBaseClass(getContext());
        notifications = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        setBackGroundColor();
        SetUpToolbar.setTitle("Notifications", getActivity());
        initView(view);
        getNotifications();

        return view;
    }

    private void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        notificationAdapter = new NotificationAdapter(getContext(), notifications);

        recyclerView.setAdapter(notificationAdapter);

    }

    private void getNotifications() {
        final ArrayList<FirebaseNotification> newNotifications = new ArrayList<>();
        fireBaseClass.getNotificationRef().orderByKey().limitToLast(2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final long childCount = dataSnapshot.getChildrenCount();
                final long[] counter = {0};
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    counter[0]++;
                    final FirebaseNotification firebaseNotification = new FirebaseNotification();
                    final String tagName = (String) dataSnapshot1.child(Constants.TAG).getValue();
                    final Long timeStamp = (Long) dataSnapshot1.child(Constants.TIMESTAMP).getValue();
                    final String videoId = (String) dataSnapshot1.child(Constants.VIDEO_ID).getValue();
                    fireBaseClass.getVideoRef().child(videoId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String videoTitle = (String) dataSnapshot.child(Constants.VIDEO_TITLE).getValue();
                            String thumbnail = (String) dataSnapshot.child(Constants.VIDEO_THUMBNAIL).getValue();
                            Log.d(TAG, "onDataChange: " + videoTitle);
                            firebaseNotification.setTagName(tagName);
                            firebaseNotification.setVideoId(videoId);
                            firebaseNotification.setVideoThumbnail(thumbnail);
                            firebaseNotification.setVideoTitle(videoTitle);
                            firebaseNotification.setTimestamp(timeStamp);

                            newNotifications.add(firebaseNotification);

                            if (counter[0] == childCount) {
                                //notifyrecyclerview
                                DatabaseReference timestampRef = FirebaseDatabase.getInstance().getReference("timestampref");
                                timestampRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Long currentTimestamp = (Long) dataSnapshot.getValue();
                                     //   Collections.reverse(notifications);
                                      //  notifications.addAll(newNotifications);
                                        notificationAdapter.setTimeStamp(currentTimestamp);
                                        notificationAdapter.notifyDataSetChanged();

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                timestampRef.setValue(ServerValue.TIMESTAMP);
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

    private void setBackGroundColor() {
        getActivity().findViewById(R.id.my_videos_bar).setVisibility(View.GONE);
        getActivity().findViewById(R.id.all_videos_bar).setVisibility(View.GONE);
        getActivity().findViewById(R.id.notification_bar).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.recent_bar).setVisibility(View.GONE);

    }

}
