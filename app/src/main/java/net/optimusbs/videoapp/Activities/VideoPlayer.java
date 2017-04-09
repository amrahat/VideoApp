package net.optimusbs.videoapp.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.widget.IconTextView;

import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.Constants;
import net.optimusbs.videoapp.UtilityClasses.FireBaseClass;
import net.optimusbs.videoapp.UtilityClasses.TagLayoutContainer;
import net.optimusbs.videoapp.UtilityClasses.UtilsMethod;
import net.optimusbs.videoapp.UtilityClasses.VolleyRequest;
import net.optimusbs.videoapp.adapters.VideoListByTagAdapter2;
import net.optimusbs.videoapp.facebookmodels.CommentsResponse;
import net.optimusbs.videoapp.facebookutils.FacebookApi;
import net.optimusbs.videoapp.fragments.LoginDialog;
import net.optimusbs.videoapp.interfaces.DialogDismissListener;
import net.optimusbs.videoapp.models.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Santo on 1/2/2017.
 */

public class VideoPlayer extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, View.OnClickListener {
    @InjectView(R.id.title)
    TextView title;

    @InjectView(R.id.back_button)
    IconTextView backButton;

    @InjectView(R.id.likeCount)
    TextView likeCount;

    @InjectView(R.id.description)
    TextView description;

    @InjectView(R.id.viewCount)
    TextView viewCount;

    @InjectView(R.id.commentCount)
    TextView commentCount;

    @InjectView(R.id.like_count_facebook)
    TextView likeCountFb;
    YouTubePlayer mYouTubePlayer;

    /*@InjectView(R.id.indicator_description_visibility)
    IconTextView indicatorDescription;*/

    @InjectView(R.id.titleLayout)
    RelativeLayout titleLayout;

    private CommentsResponse facebookCommentResponse;

    @InjectView(R.id.related_videos_list)
    RecyclerView relatedVideosList;

    @InjectView(R.id.like)
    IconTextView like;
    @InjectView(R.id.comment)
    IconTextView comment;
    @InjectView(R.id.share)
    IconTextView share;
    @InjectView(R.id.favourite)
    IconTextView favourite;

    @InjectView(R.id.tag_layout)
    TagLayoutContainer tagContainer;

    @InjectView(R.id.comment_count_facebook)
    TextView commentCountFb;

    @InjectView(R.id.like_layout)
    LinearLayout likeLayout;
    @InjectView(R.id.comment_layout)
    LinearLayout commentLayout;
    @InjectView(R.id.share_layout)
    LinearLayout shareLayout;
    @InjectView(R.id.favourite_layout)
    LinearLayout favouriteLayout;
    FireBaseClass fireBaseClass;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDb, videoDb, favVideoDbRef;

    String videoId;
    Video video;

    boolean isLikedByCurrentUser = false;
    boolean isFavouriteByCurrentUser = false;
    boolean isUserLoggedIn;
    String loggedInUserId;

    boolean description_layout_visible = false;
    String tag;

    String favouriteIcon, nonFavouriteIcon;

    int iconNormalColor, iconSelectedColor;

    LoginDialog loginDialog;
    private DatabaseReference videoListRef;
    private boolean fullScreen = false;
    private int likeNormalColor;
    private String facebookPostId;
    private FacebookApi facebookApi;
    private DatabaseReference likeDb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Iconify.with(new FontAwesomeModule());
        setContentView(R.layout.fragment_youtube_player);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.status_bar_color));
        loginDialog = new LoginDialog();
        fireBaseClass = new FireBaseClass(this);
        facebookApi = new FacebookApi(getApplicationContext());
        iconNormalColor = ContextCompat.getColor(this, R.color.video_player_bottom_icon_color);
        iconSelectedColor = ContextCompat.getColor(this, R.color.toolbar_color);
        likeNormalColor = ContextCompat.getColor(this, R.color.divider);
        favouriteIcon = getResources().getString(R.string.icon_favourite_full);
        nonFavouriteIcon = getResources().getString(R.string.icon_favourite_empty);


        ButterKnife.inject(this);
        initializeFireBase();
        YouTubePlayerView playerView = (YouTubePlayerView) findViewById(R.id.player);
        playerView.initialize(Constants.API_KEY, this);

        initializeRecyclerView();
        getIntentData();

        if (AccessToken.getCurrentAccessToken() != null) {
            isUserLoggedIn = true;
            loggedInUserId = Profile.getCurrentProfile().getId();
            setIsLikedByCurrentUser();
            setIsFavouriteByCurrentUser();

        } else {
            isUserLoggedIn = false;
        }

        setFacebookPostId();


        likeLayout.setOnClickListener(this);
        commentLayout.setOnClickListener(this);
        shareLayout.setOnClickListener(this);
        favouriteLayout.setOnClickListener(this);
        backButton.setOnClickListener(this);


        /*LikeView likeView = (LikeView) findViewById(R.id.likeView);
        likeView.setLikeViewStyle(LikeView.Style.STANDARD);
        likeView.setAuxiliaryViewPosition(LikeView.AuxiliaryViewPosition.INLINE);

        likeView.setObjectIdAndType(
                "https://www.facebook.com/mannitsolutionsltd/posts/252982411817304",
                LikeView.ObjectType.OPEN_GRAPH);*/

    }

    private void setFacebookPostId() {
        firebaseDatabase.getReference("videos").child(videoId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(Constants.FACEBOOK_POST_ID)) {
                    if (!dataSnapshot.child(Constants.FACEBOOK_POST_ID).getValue().toString().equals("null")) {
                        facebookPostId = dataSnapshot.child(Constants.FACEBOOK_POST_ID).getValue().toString();
                        /*if(facebookPostId!=null){
                            facebookApi.getCommentsOfPost(facebookPostId, AccessToken.getCurrentAccessToken().getToken(), VideoPlayer.this);
                            facebookApi.getReactionsOfPost(facebookPostId,AccessToken.getCurrentAccessToken().getToken(),VideoPlayer.this);
                        }*/
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setIsFavouriteByCurrentUser() {
        favVideoDbRef.child(videoId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                isFavouriteByCurrentUser = dataSnapshot.hasChild(loggedInUserId);
                if (isFavouriteByCurrentUser) {
                    favourite.setTextColor(iconSelectedColor);
                    favourite.setText(favouriteIcon);
                } else {
                    favourite.setTextColor(iconNormalColor);
                    favourite.setText(nonFavouriteIcon);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setIsLikedByCurrentUser() {


        likeDb.child(videoId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                isLikedByCurrentUser = dataSnapshot.hasChild(loggedInUserId);
                if (isLikedByCurrentUser) {
                    like.setTextColor(iconSelectedColor);
                } else {
                    like.setTextColor(likeNormalColor);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
       /* userDb.child(loggedInUserId).child(Constants.LIKED).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                isLikedByCurrentUser = dataSnapshot.hasChild(videoId);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/
    }

    private void initializeFireBase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDb = firebaseDatabase.getReference(Constants.USERDB);
        favVideoDbRef = firebaseDatabase.getReference(Constants.FAVOURITE_REF);
        videoDb = firebaseDatabase.getReference(Constants.VIDEO_REF);
        videoListRef = firebaseDatabase.getReference(Constants.VIDEO_REF);
        likeDb = firebaseDatabase.getReference(Constants.LIKE_REF);
    }

    private void initializeRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        relatedVideosList.setLayoutManager(mLayoutManager);
        relatedVideosList.setItemAnimator(new DefaultItemAnimator());
    }

    private void getIntentData() {
        Bundle bundle = getIntent().getBundleExtra("bundle");
        if (bundle.containsKey("video_id")) {
            videoId = getIntent().getBundleExtra("bundle").getString("video_id");
            getVideoData();
        } else if (bundle.containsKey("video")) {
            video = (Video) bundle.getSerializable("video");
            videoId = video.getId();
            setUpViews(video);
        }
        tag = bundle.getString("tag");

        if (tag != null && !tag.isEmpty()) {
            getRelatedVideosByTag(tag);
        } else {
            loadRelatedVideoFromYoutube(videoId);

        }


    }

    private void getRelatedVideosByTag(final String tag) {
        DatabaseReference tagRef = FirebaseDatabase.getInstance().getReference(Constants.TAG_REF);

        tagRef.child(tag).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> videoList = (ArrayList<String>) dataSnapshot.getValue();
                videoList.remove(videoId);
                //  Log.d("videoremove", String.valueOf(videoList.contains(video)));
                if (videoList.size() > 0) {
                    final ArrayList<Video> videos = new ArrayList<Video>();
                    for (int i = 0; i < videoList.size(); i++) {
                        final int finalI = i;
                        videoListRef.child(videoList.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Video video = dataSnapshot.getValue(Video.class);
                                if (video != null) {
                                    video.setId(videoList.get(finalI));
                                    videos.add(video);
                                }


                                if (finalI == videoList.size() - 1) {
                                    //setadapter
                                    VideoListByTagAdapter2 videoListByTagAdapter = new VideoListByTagAdapter2(videos, getApplicationContext(), tag);
                                    relatedVideosList.setAdapter(videoListByTagAdapter);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    Log.d("sizebooro", "choto");

                    loadRelatedVideoFromYoutube(videoId);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadRelatedVideoFromYoutube(String videoId) {
        String relatedVideoUrl = Constants.getRelatedVideoUrl(videoId);
        Log.d("related videos url", "loadRelatedVideoFromYoutube: " + relatedVideoUrl);
        VolleyRequest.sendRequestGet(getApplicationContext(), relatedVideoUrl, new VolleyRequest.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                ArrayList<String> videoList = new ArrayList<String>();
                ArrayList<Video> videos = new ArrayList<Video>();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray itemsArray = jsonObject.getJSONArray("items");
                    for (int i = 0; i < itemsArray.length(); i++) {
                        Video video = new Video();

                        JSONObject item = itemsArray.getJSONObject(i);
                        String videoId = item.getJSONObject("id").getString("videoId");
                        video.setId(videoId);
                        video.setTitle(item.getJSONObject("snippet").getString("title"));
                        video.setThumbnail(item.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("default").getString("url"));

                        videos.add(video);
                        //videoList.add(videoId);
                    }

                    VideoListByTagAdapter2 videoListByTagAdapter = new VideoListByTagAdapter2(videos, getApplicationContext(), tag);

                    relatedVideosList.setAdapter(videoListByTagAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void getVideoData() {
        String url = Constants.getDataUrl(videoId);
        Log.d("url", url);
        VolleyRequest.sendRequestGet(this, url, new VolleyRequest.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                //Log.d("result", result);
                parseJson(result);
            }
        });
    }

    private void parseJson(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject itemsObject = jsonObject.getJSONArray("items").getJSONObject(0);
            JSONObject snippetObject = itemsObject.getJSONObject("snippet");
            String videoId = itemsObject.getString("id");
            String thumbnail = snippetObject.getJSONObject("thumbnails").getJSONObject("default").getString("url");
            String publishedAt = snippetObject.getString("publishedAt");
            String title = snippetObject.getString("title");
            String description = snippetObject.getString("description");
            String viewCount="",likeCount="",commentCount="";
            if(itemsObject.has("statistics")){
                JSONObject statisticsObject = itemsObject.getJSONObject("statistics");
                viewCount = statisticsObject.getString("viewCount");

                if (statisticsObject.has("likeCount")) {
                    likeCount = statisticsObject.getString("likeCount");
                } else {
                    likeCount = "0";
                }
                commentCount = statisticsObject.getString("commentCount");
            }



            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            ArrayList<String> tagsList = new Gson().fromJson(snippetObject.getJSONArray("tags").toString(), type);
            showTagsInTagContainer(tagsList);

            Video video = new Video(videoId, title, description, publishedAt, viewCount, likeCount, commentCount, thumbnail);

            setUpViews(video);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void showTagsInTagContainer(ArrayList<String> tagsList) {
        tagContainer.addTags(this, tagsList);
    }

    private void setUpViews(Video video) {
        title.setText(video.getTitle());
        description.setText(video.getDescription());
        UtilsMethod.makeTextViewResizable(description,2,getString(R.string.see_more_ggs),true,getApplicationContext());

        /*titleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (description_layout_visible) {
                    description.setVisibility(View.GONE);
                    description_layout_visible = false;
                    indicatorDescription.setText("{fa-caret-down}");
                } else {
                    description.setVisibility(View.VISIBLE);
                    description_layout_visible = true;
                    indicatorDescription.setText("{fa-caret-up}");

                }
            }
        });*/

        viewCount.setText(video.getViewCount());
        likeCount.setText(video.getLikeCount());
        commentCount.setText(video.getCommentCount());

        fireBaseClass.addVideoToDatabase(videoId, Constants.VIDEO_TITLE, video.getTitle());
        fireBaseClass.addVideoToDatabase(videoId, Constants.VIDEO_THUMBNAIL, video.getThumbnail());
        fireBaseClass.addVideoToDatabase(videoId, Constants.VIDEO_DESCRIPTION, video.getDescription());
        fireBaseClass.addVideoToDatabase(videoId, Constants.VIEW_COUNT, video.getViewCount());
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        mYouTubePlayer = youTubePlayer;
        youTubePlayer.cueVideo(videoId);
        youTubePlayer.play();


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = newConfig.orientation;
        if (mYouTubePlayer != null) {
            switch (orientation) {
                case Configuration.ORIENTATION_LANDSCAPE:
                    mYouTubePlayer.setFullscreen(true);
                    fullScreen = true;
                    break;
                case Configuration.ORIENTATION_PORTRAIT:
                    mYouTubePlayer.setFullscreen(false);
                    fullScreen = false;

                    break;

            }
//            mYouTubePlayer.play();

        }
    }

    @Override
    public void onBackPressed() {
        if (fullScreen) {

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.like_layout:
                //Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
                doLike();
                break;
            case R.id.share_layout:
                doShare();
                break;
            case R.id.comment_layout:
                showCommentFragment();
                break;
            case R.id.favourite_layout:
                doFavourite();
                break;
            case R.id.back_button:
                onBackPressed();
                break;
        }

    }

    private void showCommentFragment() {
        /*if (facebookPostId != null) {
            Intent intent = new Intent(this, CommentActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("post_id", facebookPostId);

            intent.putExtra("bundle", bundle);

            startActivity(intent);
        }else {
            Toast.makeText(this, "No facebook post found", Toast.LENGTH_SHORT).show();
        }*/

        Intent intent = new Intent(this, CommentActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("video_id", videoId);

        intent.putExtra("bundle", bundle);

        startActivity(intent);
    }

    private void doShare() {
    }

    private void doLike() {
        if (isUserLoggedIn) {
            if (isLikedByCurrentUser) {
                likeDb.child(videoId).child(loggedInUserId).removeValue();
                /*userDb.child(loggedInUserId).child(Constants.LIKED).child(videoId).removeValue();*/
                videoDb.child(videoId).child(Constants.USER_WHO_LIKED).child(loggedInUserId).removeValue();
                like.setTextColor(likeNormalColor);
                isLikedByCurrentUser = false;
            } else {
                likeDb.child(videoId).child(loggedInUserId).setValue(ServerValue.TIMESTAMP);
                /*userDb.child(loggedInUserId).child(Constants.LIKED).child(videoId).setValue(1);
*/
                videoDb.child(videoId).child(Constants.USER_WHO_LIKED).child(loggedInUserId).setValue(1);
                like.setTextColor(iconSelectedColor);
                isLikedByCurrentUser = true;
            }
        } else {
            loginDialog.setDialogDismissListener(new DialogDismissListener() {
                @Override
                public void onDismiss(boolean success) {
                    if (success) {
                        isUserLoggedIn = true;
                        loggedInUserId = Profile.getCurrentProfile().getId();
                        doLike();
                    }
                }
            });
            loginDialog.show(getFragmentManager(), "");

            //dologin
        }


    }

    private void doFavourite() {
        if (isUserLoggedIn) {
            if (isFavouriteByCurrentUser) {
                favVideoDbRef.child(videoId).child(loggedInUserId).removeValue();
                // userDb.child(loggedInUserId).child(Constants.FAVOURITE).child(videoId).removeValue();
                videoDb.child(videoId).child(Constants.USER_WHO_FAVOURITE).child(loggedInUserId).removeValue();
                userDb.child(loggedInUserId).child(Constants.FAVOURITE).child(videoId).removeValue();
                favourite.setTextColor(iconSelectedColor);
                favourite.setText(nonFavouriteIcon);
                isFavouriteByCurrentUser = false;
            } else {
                favVideoDbRef.child(videoId).child(loggedInUserId).setValue(ServerValue.TIMESTAMP);
                //userDb.child(loggedInUserId).child(Constants.FAVOURITE).child(videoId).setValue(1);
                videoDb.child(videoId).child(Constants.USER_WHO_FAVOURITE).child(loggedInUserId).setValue(UtilsMethod.getCurrentTimeStamp());
                userDb.child(loggedInUserId).child(Constants.FAVOURITE).child(videoId).setValue(ServerValue.TIMESTAMP);

                favourite.setTextColor(iconSelectedColor);
                favourite.setText(favouriteIcon);

                isFavouriteByCurrentUser = true;
            }


        } else {
            loginDialog.setDialogDismissListener(new DialogDismissListener() {
                @Override
                public void onDismiss(boolean success) {
                    if (success) {
                        isUserLoggedIn = true;
                        loggedInUserId = Profile.getCurrentProfile().getId();
                        doFavourite();
                    }
                }
            });
            loginDialog.show(getFragmentManager(), "");


        }
    }

}
