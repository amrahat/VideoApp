package net.optimusbs.videoapp.Fragments;


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

import net.optimusbs.videoapp.Adapters.TagRecyclerViewAdapter;
import net.optimusbs.videoapp.Classes.Tag;
import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.Constants;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 */
public class Tags extends Fragment {
    RecyclerView tagRecyclerView;
    ArrayList<Tag> tags;

    public Tags() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tags, container, false);
        initializeViews(view);
        findTags();
        return view;
    }

    private void findTags() {
        tags = new ArrayList<>();
        DatabaseReference tagRef = FirebaseDatabase.getInstance().getReference(Constants.TAG_REF);
        tagRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    DataSnapshot snapshot = iterator.next();
                    String tagName = snapshot.getKey();
                    int videoCount = (int) snapshot.getChildrenCount();

                    Tag tag = new Tag(tagName,videoCount);
                    tags.add(tag);
                }

                TagRecyclerViewAdapter tagRecyclerViewAdapter = new TagRecyclerViewAdapter(tags,getActivity(),getFragmentManager(),false);
                tagRecyclerView.setAdapter(tagRecyclerViewAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initializeViews(View view) {
        tagRecyclerView = (RecyclerView) view.findViewById(R.id.tagRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        tagRecyclerView.setLayoutManager(mLayoutManager);
        tagRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

}
