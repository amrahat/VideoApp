package net.optimusbs.videoapp.Fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joanzapata.iconify.widget.IconTextView;

import net.optimusbs.videoapp.Adapters.VideoListByTagAdapter;
import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.Constants;
import net.optimusbs.videoapp.UtilityClasses.OnSwipeTouchListener;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    LinearLayout bannerContainer;
    LinearLayout indicatorLayout;
    LinearLayout container2;
    TextView tagName;
    int currentPostitionOfBanner = 0;
    IconTextView[] indicators;
    HomeBannerFragment.OnSwipe onSwipe;
    int indicatorSmallSize = 15,indicatorLargeSize = 20;
    int indicatorSmallColor = Color.parseColor("#998685");
    int indicatorLargeColor = Color.parseColor("#553297");
    ArrayList<String> homeBannerTag;


    FirebaseDatabase firebaseDatabase;
    DatabaseReference homeBannerRef, tagRef;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        initializeView(view);
        setBackGroundColor();
        initializeOnSwipeInterface();
        initializeFireBaseDatabase();
        setSwipeGestureOnBannerContainer();
        getHomeBannerData();
        setOtherTagVideo();

        return view;
    }

    private void setBackGroundColor() {
        getActivity().findViewById(R.id.home).setBackgroundColor(Color.parseColor(Constants.TOP_BAR_BACK_COLOR));

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
    public void doSwipeLeftOp(){
        if (currentPostitionOfBanner != homeBannerTag.size() - 1) {
            int prevPos = currentPostitionOfBanner;
            currentPostitionOfBanner++;
            String tagName = homeBannerTag.get(currentPostitionOfBanner);
            getVideosByTag(tagName,false);
            changeIndicatorSizeAndColor(prevPos,currentPostitionOfBanner);
        }
    }

    public void doSwipeRightOp(){
        if (currentPostitionOfBanner != 0) {
            int prevPos = currentPostitionOfBanner;
            currentPostitionOfBanner--;
            String tagName = homeBannerTag.get(currentPostitionOfBanner);
            getVideosByTag(tagName,true);
            changeIndicatorSizeAndColor(prevPos,currentPostitionOfBanner);

        }
    }

    private void getVideosByTag(final String firstTag, final boolean leftToRight) {
        tagName.setText(firstTag);
        tagRef.child(firstTag).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> videoList = (ArrayList<String>) dataSnapshot.getValue();
                populateHomeBanner(videoList,leftToRight,firstTag);
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

    private void getVideosByTag(final String tag, final RecyclerView recyclerView) {
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

    private void setOtherTagVideo() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.home_special_video_container,null);
        TextView tagTitle = (TextView) view.findViewById(R.id.tag_title);
        RecyclerView videoListRecyclerView = (RecyclerView) view.findViewById(R.id.video_list);
        //
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        videoListRecyclerView.setLayoutManager(mLayoutManager);
        videoListRecyclerView.setItemAnimator(new DefaultItemAnimator());

        String tag = "marriage-ceremony";
        tagTitle.setText(tag);

        getVideosByTag(tag,videoListRecyclerView);
        container2.addView(view);


    }

    private void initializeFireBaseDatabase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        homeBannerRef = firebaseDatabase.getReference(Constants.HOME_BANNER_REF);
        tagRef = firebaseDatabase.getReference(Constants.TAG_REF);

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

                if(homeBannerTag.size()!=0){
                    String firstTag = homeBannerTag.get(0);
                    setIndicator(homeBannerTag.size());
                    getVideosByTag(firstTag,true);
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
        for(int i = 0;i<size;i++){
            IconTextView iconTextView = new IconTextView(getActivity());
            iconTextView.setText("{fa-circle}");
            iconTextView.setTextSize(indicatorSmallSize);
            iconTextView.setTextColor(indicatorSmallColor);
            iconTextView.setPadding(padding,padding,padding,padding);
            indicators[i] = iconTextView;
            indicatorLayout.addView(iconTextView);
        }

        indicators[0].setTextSize(indicatorLargeSize);
        indicators[0].setTextColor(indicatorLargeColor);
    }

    private void populateHomeBanner(ArrayList<String> videoId,boolean leftToRight,String tag) {
        HomeBannerFragment homeBannerFragment = new HomeBannerFragment(onSwipe);
        Bundle bundle = new Bundle();
        // String[] videoIdArray = {"FNb1iB3VqEc","Qro_C8B-zxA","XuEEwRvuu0A"};

        bundle.putStringArrayList("video_id_array", videoId);
        bundle.putString("tag",tag);

        homeBannerFragment.setArguments(bundle);


        //getFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right).replace(R.id.banner_container, homeBannerFragment).addToBackStack("recent").commit();
        if(leftToRight){
            getFragmentManager().
                    beginTransaction().
                    setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right).
                    replace(R.id.banner_container, homeBannerFragment).
                    commit();

        }else {
            getFragmentManager()
                    .beginTransaction().
                    setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left).
                    replace(R.id.banner_container, homeBannerFragment).
                    commit();

        }



    }

}
