package net.optimusbs.videoapp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.Constants;
import net.optimusbs.videoapp.UtilityClasses.FireBaseClass;
import net.optimusbs.videoapp.UtilityClasses.ItemDecoration;
import net.optimusbs.videoapp.UtilityClasses.SetUpToolbar;
import net.optimusbs.videoapp.adapters.TagRecyclerViewAdapter;
import net.optimusbs.videoapp.interfaces.DialogDismissListener;
import net.optimusbs.videoapp.interfaces.OnTagFavoriteClickListener;
import net.optimusbs.videoapp.models.Tag;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 */
public class Tags extends Fragment implements OnTagFavoriteClickListener {
    RecyclerView tagRecyclerView;
    ArrayList<Tag> tags;
    TextView tagCount;
    private LoginDialogV4 loginDialog;
    private FireBaseClass fireBaseClass;
    private TagRecyclerViewAdapter tagRecyclerViewAdapter;

    public Tags() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fireBaseClass = new FireBaseClass(getContext());
        loginDialog = new LoginDialogV4();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tags, container, false);
        SetUpToolbar.setTitle("Tags", getActivity());
        hideSearchEditText();

        initializeViews(view);
        findTags();
        return view;
    }

    private boolean isUserLoggedIn() {
        return AccessToken.getCurrentAccessToken() != null;
    }

    private void findTags() {
        tags = new ArrayList<>();
        DatabaseReference tagRef = FirebaseDatabase.getInstance().getReference(Constants.TAG_REF);
        tagRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    DataSnapshot snapshot = iterator.next();
                    String tagName = snapshot.getKey();
                    int videoCount = (int) snapshot.getChildrenCount();

                    final Tag tag = new Tag(tagName, videoCount);

                    fireBaseClass.getFavouriteTagRef().child(tagName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            tag.setFavouriteCount(dataSnapshot.getChildrenCount());
                            if (isUserLoggedIn()) {
                                String profileId = Profile.getCurrentProfile().getId();
                                tag.setFavouriteByCurrentUser(dataSnapshot.hasChild(profileId));
                            } else {
                                tag.setFavouriteByCurrentUser(false);
                            }
                            tags.add(tag);

                            if (!iterator.hasNext()) {
                                tagCount.setText(tags.size() + " tags found");
                                tagRecyclerViewAdapter = new TagRecyclerViewAdapter(tags, getActivity(), getFragmentManager(), false, Tags.this);

                                tagRecyclerView.setAdapter(tagRecyclerViewAdapter);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    /**/
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void hideSearchEditText() {
        getActivity().findViewById(R.id.search_layout).setVisibility(View.GONE);
        getActivity().findViewById(R.id.title_layout).setVisibility(View.VISIBLE);
    }

    private void initializeViews(View view) {
        tagCount = (TextView) view.findViewById(R.id.tag_count);
        tagRecyclerView = (RecyclerView) view.findViewById(R.id.tagRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        tagRecyclerView.setLayoutManager(mLayoutManager);
        tagRecyclerView.setItemAnimator(new DefaultItemAnimator());
        tagRecyclerView.addItemDecoration(new ItemDecoration(getContext()));
    }

    @Override
    public void onFavouriteIconClick(final int position) {
        if (!isUserLoggedIn()) {
            loginDialog.setDialogDismissListener(new DialogDismissListener() {
                @Override
                public void onDismiss(boolean success) {
                    if (success) {
                        doFav(position);
                    } else {
                        Toast.makeText(getContext(), "Failed to log in", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            loginDialog.show(getFragmentManager(), "");
        } else {
            doFav(position);
        }
    }

    private void doFav(int position) {
        String loggedInUserId = Profile.getCurrentProfile().getId();
        long favVideoCount = tags.get(position).getFavouriteCount();
        boolean isFav = tags.get(position).isFavouriteByCurrentUser();
        if (isFav) {
            fireBaseClass.getFavouriteTagRef().child(tags.get(position).getTagName()).child(loggedInUserId).removeValue();
            favVideoCount--;
        } else {
            fireBaseClass.getFavouriteTagRef().child(tags.get(position).getTagName()).child(loggedInUserId).setValue(ServerValue.TIMESTAMP);
            favVideoCount++;
        }

        tags.get(position).setFavouriteByCurrentUser(!isFav);
        tags.get(position).setFavouriteCount(favVideoCount);
        tagRecyclerViewAdapter.notifyItemChanged(position);
    }
}
