package net.optimusbs.videoapp.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joanzapata.iconify.widget.IconTextView;

import net.optimusbs.videoapp.Adapters.VideoListByTagAdapter;
import net.optimusbs.videoapp.Classes.GetHashCode;
import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.Constants;
import net.optimusbs.videoapp.UtilityClasses.FireBaseClass;
import net.optimusbs.videoapp.UtilityClasses.OnSwipeTouchListener;
import net.optimusbs.videoapp.UtilityClasses.SetUpToolbar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    LinearLayout bannerContainer;
    LinearLayout indicatorLayout;
    LinearLayout container2;
    TextView tagName;
    FireBaseClass fireBaseClass;
    int currentPostitionOfBanner = 0;
    IconTextView[] indicators;
    HomeBannerFragment.OnSwipe onSwipe;
    int indicatorSmallSize = 10, indicatorLargeSize = 13;
    int indicatorSmallColor;
    int indicatorLargeColor;
    ArrayList<String> homeBannerTag;


    FirebaseDatabase firebaseDatabase;
    DatabaseReference homeBannerRef, tagRef, homeCategoryRef;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        fireBaseClass = new FireBaseClass(getContext());
        initializeView(view);
        getLoggedInStatus();
        indicatorSmallColor = ContextCompat.getColor(getContext(), R.color.topbar_color);
        indicatorLargeColor = ContextCompat.getColor(getContext(), R.color.toolbar_color);
        setBackGroundColor();
        initializeOnSwipeInterface();
        initializeFireBaseDatabase();
        setSwipeGestureOnBannerContainer();
        getHomeBannerData();
        setOtherTagVideo();
        GetHashCode.printHashCode(getContext());

        SetUpToolbar.setTitle("Home",getActivity());
        return view;
    }

    private void getLoggedInStatus() {
        if(AccessToken.getCurrentAccessToken()!=null && !AccessToken.getCurrentAccessToken().isExpired()){
            String propicUrl = Profile.getCurrentProfile().getProfilePictureUri(500,500).toString();
        }
        else {
            Log.d("nai","nai");
        }

    }


    private void setBackGroundColor() {
        getActivity().findViewById(R.id.my_videos).setBackgroundColor(indicatorSmallColor);
        getActivity().findViewById(R.id.all_videos).setBackgroundColor(indicatorSmallColor);
        getActivity().findViewById(R.id.notification).setBackgroundColor(indicatorSmallColor);
        getActivity().findViewById(R.id.home).setBackgroundColor(indicatorLargeColor);

    }

    private void initializeView(View view) {
        bannerContainer = (LinearLayout) view.findViewById(R.id.banner_container);
        indicatorLayout = (LinearLayout) view.findViewById(R.id.indicator_layout);
        container2 = (LinearLayout) view.findViewById(R.id.container2);
        tagName = (TextView) view.findViewById(R.id.tagName);
    }

    private void initializeOnSwipeInterface() {
        onSwipe = new HomeBannerFragment.OnSwipe() {
            @Override
            public void onSwipeLeftFromFragment() {
                doSwipeLeftOp();
            }

            @Override
            public void onSwipeRightFromFragment() {
                doSwipeRightOp();
            }
        };
    }

    public void doSwipeLeftOp() {
        if (currentPostitionOfBanner != homeBannerTag.size() - 1) {
            int prevPos = currentPostitionOfBanner;
            currentPostitionOfBanner++;
            String tagName = homeBannerTag.get(currentPostitionOfBanner);
            getVideosByTag(tagName, false);
            changeIndicatorSizeAndColor(prevPos, currentPostitionOfBanner);
        }
    }

    public void doSwipeRightOp() {
        if (currentPostitionOfBanner != 0) {
            int prevPos = currentPostitionOfBanner;
            currentPostitionOfBanner--;
            String tagName = homeBannerTag.get(currentPostitionOfBanner);
            getVideosByTag(tagName, true);
            changeIndicatorSizeAndColor(prevPos, currentPostitionOfBanner);

        }
    }

    private void getVideosByTag(final String firstTag, final boolean leftToRight) {
        tagName.setText(firstTag);
        tagRef.child(firstTag).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> videoList = (ArrayList<String>) dataSnapshot.getValue();
                populateHomeBanner(videoList, leftToRight, firstTag);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void changeIndicatorSizeAndColor(int prevPos, int currentPostitionOfBanner) {
        indicators[prevPos].setTextSize(indicatorSmallSize);
        indicators[prevPos].setTextColor(indicatorSmallColor);
        indicators[currentPostitionOfBanner].setTextSize(indicatorLargeSize);
        indicators[currentPostitionOfBanner].setTextColor(indicatorLargeColor);
    }

    private void getVideosByTag(final String tag, final RecyclerView recyclerView, final int count) {
        tagRef.child(tag).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> videoList = (ArrayList<String>) dataSnapshot.getValue();

                ArrayList<String> sizedVideoList = new ArrayList<String>(videoList.subList(0, count));

                VideoListByTagAdapter videoListByTagAdapter = new VideoListByTagAdapter(sizedVideoList, getActivity(), tag);
                recyclerView.setAdapter(videoListByTagAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setOtherTagVideo() {

        homeCategoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();

                while (iterator.hasNext()) {
                    DataSnapshot snapshot = iterator.next();
                    String tagName = snapshot.getKey();
                    Long count = (Long) snapshot.getValue();

                    View view = getActivity().getLayoutInflater().inflate(R.layout.home_special_video_container, null);
                    TextView tagTitle = (TextView) view.findViewById(R.id.tag_title);
                    RecyclerView videoListRecyclerView = (RecyclerView) view.findViewById(R.id.video_list);
                    //
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    videoListRecyclerView.setLayoutManager(mLayoutManager);
                    videoListRecyclerView.setItemAnimator(new DefaultItemAnimator());

                    //  String tag = "marriage-ceremony";
                    tagTitle.setText(tagName);

                    getVideosByTag(tagName, videoListRecyclerView, Integer.parseInt(String.valueOf(count)));
                    container2.addView(view);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void initializeFireBaseDatabase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        homeBannerRef = firebaseDatabase.getReference(Constants.HOME_BANNER_REF);
        tagRef = firebaseDatabase.getReference(Constants.TAG_REF);
        homeCategoryRef = firebaseDatabase.getReference(Constants.HOME_CAT_REF);

    }

    private void setSwipeGestureOnBannerContainer() {
        bannerContainer.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                doSwipeRightOp();

            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                doSwipeLeftOp();
            }


        });

    }

    private void getHomeBannerData() {
        homeBannerTag = new ArrayList<>();
        homeBannerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    DataSnapshot snapshot = iterator.next();
                    String tagName = snapshot.getKey();
                    homeBannerTag.add(tagName);
                }

                if (homeBannerTag.size() != 0) {
                    String firstTag = homeBannerTag.get(0);
                    setIndicator(homeBannerTag.size());
                    getVideosByTag(firstTag, true);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setIndicator(int size) {
        indicators = new IconTextView[size];
        int padding = 5;
        for (int i = 0; i < size; i++) {
            IconTextView iconTextView = new IconTextView(getActivity());
            iconTextView.setText("{fa-circle}");
            iconTextView.setTextSize(indicatorSmallSize);
            iconTextView.setTextColor(indicatorSmallColor);
            iconTextView.setPadding(padding, padding, padding, padding);
            indicators[i] = iconTextView;
            indicatorLayout.addView(iconTextView);
        }

        indicators[0].setTextSize(indicatorLargeSize);
        indicators[0].setTextColor(indicatorLargeColor);
    }

    private void populateHomeBanner(ArrayList<String> videoId, final boolean leftToRight, String tag) {
        final HomeBannerFragment homeBannerFragment = new HomeBannerFragment(onSwipe);

    //    WeakReference<HomeBannerFragment> homeBannerFragment = new WeakReference<>(new HomeBannerFragment(onSwipe));
        Bundle bundle = new Bundle();
        // String[] videoIdArray = {"FNb1iB3VqEc","Qro_C8B-zxA","XuEEwRvuu0A"};

        bundle.putStringArrayList("video_id_array", videoId);
        bundle.putString("tag", tag);

        homeBannerFragment.setArguments(bundle);


        //getFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right).replace(R.id.banner_container, homeBannerFragment).addToBackStack("recent").commit();


        if (leftToRight) {
            getFragmentManager().
                    beginTransaction().
                    setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right).
                    replace(R.id.banner_container, homeBannerFragment).
                    commitAllowingStateLoss();

        } else {
            getFragmentManager()
                    .beginTransaction().
                    setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).
                    replace(R.id.banner_container, homeBannerFragment).
                    commitAllowingStateLoss();


        }
    }


}


