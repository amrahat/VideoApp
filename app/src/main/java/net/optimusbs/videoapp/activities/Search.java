package net.optimusbs.videoapp.activities;


import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.joanzapata.iconify.widget.IconTextView;

import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.Constants;
import net.optimusbs.videoapp.UtilityClasses.FireBaseClass;
import net.optimusbs.videoapp.UtilityClasses.SetUpToolbar;
import net.optimusbs.videoapp.UtilityClasses.VolleyRequest;
import net.optimusbs.videoapp.adapters.SearchResultAdapter;
import net.optimusbs.videoapp.adapters.SearchYoutubeVideoRecyclerView;
import net.optimusbs.videoapp.adapters.VideoListByTagAdapter2;
import net.optimusbs.videoapp.models.SearchResult;
import net.optimusbs.videoapp.models.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Search extends AppCompatActivity implements View.OnClickListener {
    EditText searchEditText;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference tagsRef;
    Button loadMore;
    RecyclerView tagRecyclerView, searchYouTubeRecyclerView;
    String nextPageToken;
    SearchYoutubeVideoRecyclerView searchYoutubeVideoRecyclerView;
    ArrayList<Video> videos;
    private DatabaseReference searchRef,videosRef;
    private FireBaseClass fireBaseClass;
    private Toolbar toolbar;

    @InjectView(R.id.back_button)
    IconTextView backButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search);
        ButterKnife.inject(this);
        initializeView();
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.status_bar_color));
        //setUpToolbarAndDrawer();
        initializeFirebase();

        backButton.setOnClickListener(this);
    }



    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        showSearchEditText();


        return view;
    }*/

    /*private void showSearchEditText() {
        getActivity().findViewById(R.id.search_layout).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.title_layout).setVisibility(View.GONE);
    }*/


    private void setUpToolbarAndDrawer() {
        toolbar = SetUpToolbar.setupToolbarForSearch(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }
    private void initializeFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        tagsRef = firebaseDatabase.getReference(Constants.TAG_REF);
        videosRef = firebaseDatabase.getReference(Constants.VIDEO_REF);
        searchRef = firebaseDatabase.getReference(Constants.SEARCH_REF);
        fireBaseClass = new FireBaseClass(this);

    }

    @Override
    public void onResume() {
        super.onResume();
       // showSearchEditText();
    }

    private void initializeView() {
        searchEditText = (EditText) findViewById(R.id.search_edittext);
        tagRecyclerView = (RecyclerView) findViewById(R.id.search_recycler_view);
        searchYouTubeRecyclerView = (RecyclerView) findViewById(R.id.search_youtube_video_recycler_view);
        loadMore = (Button) findViewById(R.id.load_more);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        tagRecyclerView.setLayoutManager(mLayoutManager);
        tagRecyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(this);

        searchYouTubeRecyclerView.setLayoutManager(mLayoutManager2);
        searchYouTubeRecyclerView.setItemAnimator(new DefaultItemAnimator());

        setSearchEditTextOnClick();

    }

    private void setSearchEditTextOnClick() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, final int i, int i1, int i2) {
                // Log.d("change", String.valueOf(charSequence) );
                final String searchText = String.valueOf(charSequence);
                if (!searchText.isEmpty()) {
                    searchAndPopulateView(searchText);

                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void searchAndPopulateView(final String searchText) {
        Query queryRef = searchRef.orderByChild("sort_value").startAt(searchText.toLowerCase()).endAt(searchText + "z");


        final ArrayList<SearchResult> searchResults = new ArrayList<>();
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    final Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        DataSnapshot snapshot = iterator.next();
                        final SearchResult searchResult = new SearchResult();
                        final String id = snapshot.getKey();
                        searchResult.setId(id);
                        searchResult.setTitle(snapshot.child("original_value").getValue().toString());
                        searchResult.setIsTag(isInt(id));

                        if(!searchResult.isTag()){
                            videosRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    searchResult.setThumbnail(String.valueOf(dataSnapshot.child(Constants.VIDEO_THUMBNAIL).getValue()));
                                    searchResult.setViewCount(String.valueOf(dataSnapshot.child(Constants.VIEW_COUNT).getValue()));
                                    searchResult.setPublished_at((String) dataSnapshot.child(Constants.VIDEO_PUBLISHED_AT).getValue());
                                    fireBaseClass.getCommentRef().child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            searchResult.setCommentCount(String.valueOf(dataSnapshot.getChildrenCount()));

                                            fireBaseClass.getLikeRef().child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    searchResult.setLikeCount(String.valueOf(dataSnapshot.getChildrenCount()));
                                                    searchResults.add(searchResult);

                                                    if(!iterator.hasNext()){
                                                        SearchResultAdapter tagRecyclerViewAdapter = new SearchResultAdapter(searchResults, Search.this,getSupportFragmentManager());
                                                        tagRecyclerView.setAdapter(tagRecyclerViewAdapter);
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

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }else {
                            tagsRef.child(searchResult.getTitle()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    searchResult.setThumbnail("");
                                    searchResult.setTagVideoCount(dataSnapshot.getChildrenCount());


                                    fireBaseClass.getFavouriteTagRef().child(searchResult.getTitle()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            searchResult.setFavouriteCount(dataSnapshot.getChildrenCount());
                                            if (isUserLoggedIn()) {
                                                String profileId = Profile.getCurrentProfile().getId();
                                                searchResult.setFavouriteByCurrentUser(dataSnapshot.hasChild(profileId));
                                            } else {
                                                searchResult.setFavouriteByCurrentUser(false);
                                            }

                                            searchResults.add(searchResult);

                                            if(!iterator.hasNext()){
                                                SearchResultAdapter tagRecyclerViewAdapter = new SearchResultAdapter(searchResults, Search.this,getSupportFragmentManager());
                                                tagRecyclerView.setAdapter(tagRecyclerViewAdapter);
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


                    loadMore.setVisibility(View.GONE);

                    tagRecyclerView.setVisibility(View.VISIBLE);
                    searchYouTubeRecyclerView.setVisibility(View.GONE);

                } else {
                    tagRecyclerView.setVisibility(View.GONE);
                    searchYouTubeRecyclerView.setVisibility(View.VISIBLE);
                    searchFromYoutube(searchText);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private boolean isUserLoggedIn() {
        return AccessToken.getCurrentAccessToken() != null;
    }
    private boolean isInt(String id) {
        try {
            int intId = Integer.parseInt(id);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    private void searchFromYoutube(final String searchText) {
        videos = new ArrayList<>();
        searchYoutubeVideoRecyclerView = new SearchYoutubeVideoRecyclerView(videos, this, searchText);
        searchYouTubeRecyclerView.setAdapter(searchYoutubeVideoRecyclerView);
        String searchUrlYoutube = Constants.getSearchUrl(searchText);
        Log.d("searchFromYoutube", "searchUrlFromYoutube: "+searchUrlYoutube);
        VolleyRequest.sendRequestGet(this, searchUrlYoutube, new VolleyRequest.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                parseJson(result, searchText);
            }
        });
    }

    private void parseJson(String result, final String searchText) {

        try {
            JSONObject jsonObject = new JSONObject(result);
            nextPageToken = jsonObject.getString("nextPageToken");
            JSONArray itemsArray = jsonObject.getJSONArray("items");
            for (int i = 0; i < itemsArray.length(); i++) {
                Video video = new Video();
                JSONObject item = itemsArray.getJSONObject(i);
                video.setId(item.getJSONObject("id").getString("videoId"));
                video.setTitle(item.getJSONObject("snippet").getString("title"));
                video.setThumbnail(item.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("default").getString("url"));
                String publishedAt = item.getJSONObject("snippet").getString("publishedAt");
                video.setPublished_time(publishedAt);
                String viewCount="",likeCount="",commentCount="";
                if(item.has("statistics")){
                    JSONObject statisticsObject = item.getJSONObject("statistics");
                    viewCount = statisticsObject.getString("viewCount");
                    video.setViewCount(viewCount);
                    video.setLikeCount("0");
                    video.setCommentCount("0");
                }
                videos.add(video);
            }

            searchYoutubeVideoRecyclerView.notifyDataSetChanged();

            loadMore.setVisibility(View.VISIBLE);

            loadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (nextPageToken != null && !nextPageToken.isEmpty()) {
                        String nextPageUrl = Constants.getSearchUrl(searchText, nextPageToken);
                        VolleyRequest.sendRequestGet(Search.this, nextPageUrl, new VolleyRequest.VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {
                                parseJson(result, searchText);
                            }
                        });
                    }
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if(view==backButton){
            onBackPressed();
        }
    }
}
