package net.optimusbs.videoapp.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.optimusbs.videoapp.Adapters.SearchYoutubeVideoRecyclerView;
import net.optimusbs.videoapp.Adapters.TagRecyclerViewAdapter;
import net.optimusbs.videoapp.Classes.Tag;
import net.optimusbs.videoapp.Classes.Video;
import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.Constants;
import net.optimusbs.videoapp.UtilityClasses.VolleyRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 */
public class Search extends Fragment {
    EditText searchEditText;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference tagsRef;
    Button loadMore;
    RecyclerView tagRecyclerView,searchYouTubeRecyclerView;
    String nextPageToken;
    SearchYoutubeVideoRecyclerView searchYoutubeVideoRecyclerView;
    ArrayList<Video> videos;
    public Search() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search, container, false);
        showSearchEditText();
        initializeView(view);
        initializeFirebase();

        return view;
    }

    private void showSearchEditText() {
        getActivity().findViewById(R.id.search_layout).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.title_layout).setVisibility(View.GONE);
    }

    private void initializeFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        tagsRef = firebaseDatabase.getReference(Constants.TAG_REF);

    }

    private void initializeView(View view) {
        searchEditText = (EditText) getActivity().findViewById(R.id.search_edittext);
        tagRecyclerView = (RecyclerView) view.findViewById(R.id.search_recycler_view);
        searchYouTubeRecyclerView = (RecyclerView) view.findViewById(R.id.search_youtube_video_recycler_view);
        loadMore = (Button) view.findViewById(R.id.load_more);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        tagRecyclerView.setLayoutManager(mLayoutManager);
        tagRecyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getActivity());

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
                if(!searchText.isEmpty()){
                    searchAndPopulateView(searchText);

                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void searchAndPopulateView(final String searchText) {
        Query queryRef = tagsRef.orderByKey().startAt(searchText).endAt(searchText+"z");
        final ArrayList<Tag> tags = new ArrayList<Tag>();
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()>0){
                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()){
                        DataSnapshot snapshot = iterator.next();
                        String tagName = snapshot.getKey();
                        int videoCount = (int) snapshot.getChildrenCount();

                        Tag tag = new Tag(tagName,videoCount);
                        tags.add(tag);

                    }


                    loadMore.setVisibility(View.GONE);
                    TagRecyclerViewAdapter tagRecyclerViewAdapter = new TagRecyclerViewAdapter(tags,getActivity(),getFragmentManager(),true);
                    tagRecyclerView.setAdapter(tagRecyclerViewAdapter);

                }else {
                    searchFromYoutube(searchText);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void searchFromYoutube(final String searchText) {
        videos = new ArrayList<>();
        searchYoutubeVideoRecyclerView = new SearchYoutubeVideoRecyclerView(videos,getActivity(),searchText);
        searchYouTubeRecyclerView.setAdapter(searchYoutubeVideoRecyclerView);
        String searchUrlYoutube = Constants.getSearchUrl(searchText);
        VolleyRequest.sendRequestGet(getContext(), searchUrlYoutube, new VolleyRequest.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                parseJson(result,searchText);
            }
        });
    }

    private void parseJson(String result, final String searchText) {

        try {
            JSONObject jsonObject = new JSONObject(result);
            nextPageToken = jsonObject.getString("nextPageToken");
            JSONArray itemsArray = jsonObject.getJSONArray("items");
            for(int i = 0;i<itemsArray.length();i++){
                Video video = new Video();
                JSONObject item = itemsArray.getJSONObject(i);
                video.setId(item.getJSONObject("id").getString("videoId"));
                video.setTitle(item.getJSONObject("snippet").getString("title"));
                video.setThumbnail(item.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("default").getString("url"));
                videos.add(video);
            }

            searchYoutubeVideoRecyclerView.notifyDataSetChanged();

            loadMore.setVisibility(View.VISIBLE);

            loadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(nextPageToken!=null && !nextPageToken.isEmpty()){
                        String nextPageUrl = Constants.getSearchUrl(searchText,nextPageToken);
                        VolleyRequest.sendRequestGet(getContext(), nextPageUrl, new VolleyRequest.VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {
                                parseJson(result,searchText);
                            }
                        });
                    }
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
