package net.optimusbs.videoapp.fragments;


import android.content.Context;
import android.content.Intent;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.Constants;
import net.optimusbs.videoapp.UtilityClasses.UtilsMethod;
import net.optimusbs.videoapp.adapters.CommentAdapter;
import net.optimusbs.videoapp.facebookmodels.CommentsResponse;
import net.optimusbs.videoapp.facebookutils.FacebookApi;
import net.optimusbs.videoapp.facebookutils.OnCommentPostListener;
import net.optimusbs.videoapp.interfaces.OnCommentLoadListener;
import net.optimusbs.videoapp.interfaces.OnFirebaseCommentLoadListener;
import net.optimusbs.videoapp.models.FirebaseComment;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentFragment extends Fragment implements OnCommentLoadListener, View.OnClickListener, OnFirebaseCommentLoadListener, OnCommentPostListener, FacebookCallback<LoginResult> {

    private FacebookApi facebookApi;
    private String TAG = "commentfragment";
    private CommentAdapter commentAdapter;
    private ArrayList<FirebaseComment> comments;
    private RecyclerView recyclerView;
    private EditText postEt;
    private String videoId;
    private RelativeLayout postCommentLayout;
    private LoginButton loginButton;
    private LinearLayout loginLayout;
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDb, commentRef;
    private TextView postTv;
    private ProfileTracker mProfileTracker;

    public CommentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        comments = new ArrayList<>();
        facebookApi = new FacebookApi(getContext());
        Log.d(TAG, "timestamp:"+ ServerValue.TIMESTAMP);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        initViews(view);
        initializeFireBase();
        getBundles();
        checkLoginStatus();
        return view;
    }

    private void initializeFireBase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDb = firebaseDatabase.getReference(Constants.USERDB);
        commentRef = firebaseDatabase.getReference(Constants.COMMENT_REF);
    }

    private void checkLoginStatus() {
        if (AccessToken.getCurrentAccessToken() != null) {
            postCommentLayout.setVisibility(View.VISIBLE);
            loginLayout.setVisibility(View.GONE);
        } else {
            postCommentLayout.setVisibility(View.GONE);
            loginLayout.setVisibility(View.VISIBLE);
        }
    }

    private void initializeLoginCallback() {
        loginButton.setFragment(this);

        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, this);

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
//                Log.d("here",oldAccessToken.getToken()+currentAccessToken.getToken());


            }
        };
    }

    private void getBundles() {
        Bundle bundle = getArguments();
        videoId = bundle.getString("video_id");
        // CommentsResponse commentsResponse = (CommentsResponse) bundle.getSerializable("comments");
        //comments = commentsResponse.getData();
        if (videoId != null && !videoId.isEmpty()) {
            /*if (AccessToken.getCurrentAccessToken() != null) {
               // facebookApi.getCommentsOfPost(videoId, AccessToken.getCurrentAccessToken().getToken(), this);
                // facebookApi.likeAPost(videoId);
            }*/

            getCommentsFromFirebase(this);
        }

    }

    private void getCommentsFromFirebase(final OnFirebaseCommentLoadListener onFirebaseCommentLoadListener) {
        final ArrayList<FirebaseComment> firebaseComments = new ArrayList<>();
        commentRef.child(videoId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                if (!iterator.hasNext()) {
                    onFirebaseCommentLoadListener.onFirebaseCommentLoad(firebaseComments);
                    return;
                }

                while (iterator.hasNext()) {

                    final FirebaseComment firebaseComment = new FirebaseComment();
                    DataSnapshot commentSnapShot = iterator.next();
                    String timestamp = String.valueOf((Long) commentSnapShot.child("timestamp").getValue());
                    firebaseComment.setTimeStamp(timestamp);
                    String userid = (String) commentSnapShot.child("profileid").getValue();
                    firebaseComment.setUserid(userid);
                    String commentText = (String) commentSnapShot.child("comment").getValue();
                    firebaseComment.setComment(commentText);
                   /* Iterator<DataSnapshot> commentSnapshotIterator = commentSnapShot.getChildren().iterator();
                    // if (commentSnapshotIterator.hasNext()) {
                    DataSnapshot comment = commentSnapshotIterator.next();
                    String userid = comment.getKey();
                    firebaseComment.setUserid(userid);*/

                    userDb.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild("image")) {
                                String image = (String) dataSnapshot.child("image").getValue();
                                firebaseComment.setUserImage(image);
                            }


                            if (dataSnapshot.hasChild("name")) {
                                String name = (String) dataSnapshot.child("name").getValue();
                                firebaseComment.setUserName(name);
                            }

                            firebaseComments.add(firebaseComment);

                            if (!iterator.hasNext()) {
                                Log.d(TAG, "onDataChange: " + firebaseComments.size());
                                onFirebaseCommentLoadListener.onFirebaseCommentLoad(firebaseComments);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    //   }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initViews(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new ItemDecoration(getContext()));
        commentAdapter = new CommentAdapter(comments, getContext());
        recyclerView.setAdapter(commentAdapter);
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginLayout = (LinearLayout) view.findViewById(R.id.loginLayout);
        postCommentLayout = (RelativeLayout) view.findViewById(R.id.post_comment_layout);

        postEt = (EditText) view.findViewById(R.id.post_et);
        postTv = (TextView) view.findViewById(R.id.post);
        postTv.setOnClickListener(this);
        initializeLoginCallback();
    }


    @Override
    public void onCommentLoad(CommentsResponse commentsResponse) {
        Log.d(TAG, "onCommentLoad: " + commentsResponse.getData().size());
        // loadCommentsInList(commentsResponse);

    }

   /* private void loadCommentsInList(CommentsResponse commentsResponse) {
        comments.addAll(commentsResponse.getData());
        commentAdapter.notifyDataSetChanged();

    }*/

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.post) {
            if (postEt.getText().toString().trim().length() > 0) {

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(postEt.getWindowToken(), 0);
                postComment(postEt.getText().toString().trim());
            }
        }
    }

    private void postComment(String comment) {
        //facebookApi.postAComment(videoId, AccessToken.getCurrentAccessToken().getToken(), comment, this);
        String key = commentRef.push().getKey();
        commentRef.child(videoId).child(key).child("comment").setValue(comment);
        commentRef.child(videoId).child(key).child("profileid").setValue(Profile.getCurrentProfile().getId());
        commentRef.child(videoId).child(key).child("timestamp").setValue(ServerValue.TIMESTAMP);

        FirebaseComment firebaseComment = new FirebaseComment();
        firebaseComment.setUserName(Profile.getCurrentProfile().getName());
        firebaseComment.setUserImage(Profile.getCurrentProfile().getProfilePictureUri(200, 200).toString());
        firebaseComment.setComment(comment);
        firebaseComment.setUserid(Profile.getCurrentProfile().getId());
        firebaseComment.setTimeStamp(String.valueOf(UtilsMethod.getCurrentTimeStamp()));

        comments.add(firebaseComment);
        commentAdapter.notifyItemInserted(comments.size() - 1);

        postEt.setText("");
    }

    @Override
    public void onCommentPost(String message, String id) {
        /*FacebookComment facebookComment = new FacebookComment();
        facebookComment.setMessage(message);
        facebookComment.setId(id);
        From from = new From();
        from.setId(Profile.getCurrentProfile().getId());
        from.setName(Profile.getCurrentProfile().getName());
        facebookComment.setFrom(from);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
        Date date = new Date();
        facebookComment.setCreatedTime(dateFormat.format(date));
        comments.add(facebookComment);
        commentAdapter.notifyItemInserted(comments.size()-1);

        postEt.setText("");*/


    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        if (Profile.getCurrentProfile() == null) {
            mProfileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                    // profile2 is the new profile
                    Log.d("facebook", profile2.getFirstName() + "asled");
                    mProfileTracker.stopTracking();
                    setProfileValueInFirebase(profile2);
                }
            };
            // no need to call startTracking() on mProfileTracker
            // because it is called by its constructor, internally.
        } else {
            Profile profile = Profile.getCurrentProfile();
            Log.d("facebook", profile.getFirstName());
            Log.d("idaslkdfjal", profile.getFirstName() + AccessToken.getCurrentAccessToken().getToken());

            setProfileValueInFirebase(profile);

        }
        loginLayout.setVisibility(View.GONE);
        postCommentLayout.setVisibility(View.VISIBLE);
        facebookApi.getCommentsOfPost(videoId, AccessToken.getCurrentAccessToken().getToken(), this);

    }

    private void setProfileValueInFirebase(Profile profile) {
        userDb.child(profile.getId()).child(Constants.USER_IMAGE).setValue(profile.getProfilePictureUri(500, 500).toString());
        userDb.child(profile.getId()).child(Constants.USER_NAME).setValue(profile.getName());
    }

    @Override
    public void onCancel() {
        Log.d("here", "cancel");

    }

    @Override
    public void onError(FacebookException error) {
        Log.d("here", "error" + error.toString());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onFirebaseCommentLoad(ArrayList<FirebaseComment> firebaseComments) {
        comments.clear();
        comments.addAll(firebaseComments);
        commentAdapter.notifyDataSetChanged();

    }
}
