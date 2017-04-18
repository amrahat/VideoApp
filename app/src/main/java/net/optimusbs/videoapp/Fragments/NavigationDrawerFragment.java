package net.optimusbs.videoapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.joanzapata.iconify.widget.IconTextView;
import com.squareup.picasso.Picasso;

import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.Constants;
import net.optimusbs.videoapp.activities.Activity2;
import net.optimusbs.videoapp.activities.HomeActivity;
import net.optimusbs.videoapp.activities.Search;

import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Santo on 1/4/2017.
 */

public class NavigationDrawerFragment extends Fragment implements FacebookCallback<LoginResult>, View.OnClickListener {
    private DrawerLayout mDrawerLayout;
    private View containerView;
    ActionBarDrawerToggle drawerToggle;

    LinearLayout tagLayout, homeLayout, savedSearchLayout, logInLayout, userLayout, allVideosLayout, myFavLayout;

    TextView userName, loginText;

    IconTextView searchIcon;

    CircleImageView userImage;

    private FirebaseAuth firebaseAuth;
    LoginButton loginButton;
    CallbackManager callbackManager;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDb;
    AccessTokenTracker accessTokenTracker;

    String login, logout;
    private String TAG = "navdrawer";
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProfileTracker mProfileTracker;

    private LinearLayout selectedLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    setOnLoggedOut();
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_nav_drawer, container, false);
        callbackManager = CallbackManager.Factory.create();
        login = getString(R.string.login_facebook);
        logout = getString(R.string.logout_facebook);
        initView(view);
        setOnClickListeners();

        return view;
    }


    private void setOnClickListeners() {
        tagLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("activity_name",getActivity().getLocalClassName());
                String activityName = getActivity().getLocalClassName();
                String activity2Name = "activities.Activity2";
                if (activityName.equals(activity2Name)) {
                    getFragmentManager().
                            beginTransaction().
                            setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right).
                            replace(R.id.container, new Tags()).
                            commit();
                    setSelected(tagLayout);
                } else {
                    Intent intent = new Intent(getActivity(), Activity2.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("fragment_name", "tags");
                    intent.putExtra("bundle", bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

                mDrawerLayout.closeDrawer(Gravity.LEFT);

            }
        });

        homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.putExtra("showloader", false);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                mDrawerLayout.closeDrawer(Gravity.LEFT);


            }
        });


        savedSearchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String activityName = getActivity().getLocalClassName();
                String activity2Name = "activities.Activity2";
                if (activityName.equals(activity2Name)) {
                    getFragmentManager().
                            beginTransaction().
                            setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right).
                            replace(R.id.container, new SavedSearch()).
                            commit();
                    setSelected(savedSearchLayout);
                } else {
                    Intent intent = new Intent(getActivity(), Activity2.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("fragment_name", "saved_search");
                    intent.putExtra("bundle", bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        logInLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                loginButton.performClick();


            }
        });

        userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        allVideosLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String activityName = getActivity().getLocalClassName();
                String activity2Name = "activities.HomeActivity";
                if (activityName.equals(activity2Name)) {
                    getFragmentManager().
                            beginTransaction().
                            //setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right).
                                    replace(R.id.container, new AllVideos()).
                            commit();
                    setSelected(allVideosLayout);
                } else {
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    intent.putExtra("fragment_name", "all_videos");
                    intent.putExtra("showloader", false);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        myFavLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String activityName = getActivity().getLocalClassName();
                String activity2Name = "activities.HomeActivity";
                if (activityName.equals(activity2Name)) {
                    getFragmentManager().
                            beginTransaction().
                            //setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right).
                                    replace(R.id.container, new MyVideosFragment()).
                            commit();
                    setSelected(myFavLayout);

                } else {
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    intent.putExtra("fragment_name", "my_videos");
                    intent.putExtra("showloader", false);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        });


    }

    private void initializeFacebookButton(View view) {
        /*LoginManager.getInstance().logInWithPublishPermissions(
                getActivity(),
                Arrays.asList("publish_actions"));*/
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        // loginButton.setPublishPermissions("publish_actions");
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        // If using in a fragment
        loginButton.setFragment(NavigationDrawerFragment.this);

        loginButton.registerCallback(callbackManager, NavigationDrawerFragment.this);

        /*accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    setOnLoggedOut();
                } else {
                    setOnLoggedIn();

                }
//                mDrawerLayout.closeDrawer(Gravity.LEFT);

               *//* Intent intent = new Intent(getContext(), HomeActivity.class);
                startActivity(intent);*//*


            }
        };*/
    }

    private void initView(View view) {

        tagLayout = (LinearLayout) view.findViewById(R.id.tags);
        homeLayout = (LinearLayout) view.findViewById(R.id.home);
        savedSearchLayout = (LinearLayout) view.findViewById(R.id.saved_searches);
        logInLayout = (LinearLayout) view.findViewById(R.id.login);
        userLayout = (LinearLayout) view.findViewById(R.id.userLayout);
        allVideosLayout = (LinearLayout) view.findViewById(R.id.all_videos);
        myFavLayout = (LinearLayout) view.findViewById(R.id.my_videos);
        userName = (TextView) view.findViewById(R.id.user_name);
        loginText = (TextView) view.findViewById(R.id.login_text);
        userImage = (CircleImageView) view.findViewById(R.id.user_image);


        initializeFireBase();
        initializeFacebookButton(view);
    }

    public void setUp(int fragmentId, final DrawerLayout layout, Toolbar toolbar) {
        mDrawerLayout = layout;
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout.closeDrawer(Gravity.LEFT);
        drawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };


        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                drawerToggle.syncState();
            }
        });

        mDrawerLayout.addDrawerListener(drawerToggle);

        searchIcon = (IconTextView) getActivity().findViewById(R.id.search_icon);

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String activityName = getActivity().getLocalClassName();
                Log.d(TAG, "onClick: " + activityName);

                Intent intent = new Intent(getActivity(), Search.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        getLoggedInData();


    }

    private void getLoggedInData() {
        if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {
            setOnLoggedIn();
            Log.d("accesstoken", AccessToken.getCurrentAccessToken().getToken());

        } else {
            // userLayout.setVisibility(View.GONE);
            setOnLoggedOut();
        }

    }

    public void setOnLoggedIn(Profile profile) {
        loginText.setText(logout);

        //userLayout.setVisibility(View.VISIBLE);

        if (profile != null) {
            userName.setText(profile.getName());
            String profilePic = profile.getProfilePictureUri(500, 500).toString();
            if (profilePic != null) {
                Log.d("propic", profilePic);
                Picasso.with(getContext()).load(profilePic).into(userImage);
            }

        }
    }

    public void setOnLoggedIn() {
        loginText.setText(logout);

        //userLayout.setVisibility(View.VISIBLE);
        Profile profile = Profile.getCurrentProfile();

        if (profile != null) {
            userName.setText(profile.getName());
            String profilePic = profile.getProfilePictureUri(500, 500).toString();
            if (profilePic != null) {
                Log.d("propic", profilePic);
                Picasso.with(getContext()).load(profilePic).into(userImage);
            }

        }
    }

    public void setOnLoggedOut() {
        //userLayout.setVisibility(View.GONE);
        userName.setText(login);
        loginText.setText(login);

        userImage.setImageResource(R.drawable.user_placeholder);

    }

    private void initializeFireBase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDb = firebaseDatabase.getReference(Constants.USERDB);
    }



    /*@Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }*/

    @Override
    public void onSuccess(LoginResult loginResult) {
        // Profile profile = Profile.getCurrentProfile();
        //  User user = new User(profile.getId(),profile.getName(),profile.getProfilePictureUri(500,500).toString());


        if (Profile.getCurrentProfile() == null) {
            mProfileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                    // profile2 is the new profile
                    Log.d("facebook", profile2.getFirstName() + "asled");
                    mProfileTracker.stopTracking();
                    setProfileValueInFirebase(profile2);
                    setOnLoggedIn(profile2);
                }
            };
            // no need to call startTracking() on mProfileTracker
            // because it is called by its constructor, internally.
        } else {
            Profile profile = Profile.getCurrentProfile();
            Log.d("facebook", profile.getFirstName());
            Log.d("idaslkdfjal", profile.getFirstName() + AccessToken.getCurrentAccessToken().getToken());
            setOnLoggedIn(profile);

            setProfileValueInFirebase(profile);

        }

        // handleFacebookAccessToken(AccessToken.getCurrentAccessToken());
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoggedInData();
    }

    private void setProfileValueInFirebase(Profile profile) {
        userDb.child(profile.getId()).child(Constants.USER_IMAGE).setValue(profile.getProfilePictureUri(500, 500).toString());
        userDb.child(profile.getId()).child(Constants.USER_NAME).setValue(profile.getName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    public void onCancel() {
        Log.d(TAG, "onError: facebookerror " + "cancel");

    }

    @Override
    public void onError(FacebookException error) {
        Log.d(TAG, "onError: facebookerror " + error.getMessage());
        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_drawer_root:
                //do nothing
                break;
        }
    }

    public void setSelected(String fragment_name) {
        switch (fragment_name) {
            case Constants.HOME:
                setSelected(homeLayout);
                break;
            case Constants.ALL_VIDEOS:
                setSelected(allVideosLayout);
                break;
            case Constants.MY_VIDEOS:
                setSelected(myFavLayout);
                break;
            case Constants.TAGS:
                setSelected(tagLayout);
                break;
            case Constants.SAVED_SEARCH:
                setSelected(savedSearchLayout);
                break;
            default:
                if (selectedLayout != null) {
                    makeLayoutUnselected(selectedLayout);
                    selectedLayout = null;
                }

        }
    }

    private void setSelected(LinearLayout layout) {
        if (selectedLayout != null) {
            makeLayoutUnselected(selectedLayout);
        }
        selectedLayout = layout;
        makeLayoutSelected(layout);

    }

    private void makeLayoutSelected(LinearLayout layout) {
        changeLayoutChildColor(layout, R.color.toolbar_color);
    }

    private void changeLayoutChildColor(LinearLayout layout, int color) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View v = layout.getChildAt(i);
            if (v instanceof TextView) {
                TextView t = (TextView) v;
                t.setTextColor(ContextCompat.getColor(getContext(), color));
            }
        }
    }

    private void makeLayoutUnselected(LinearLayout selectedLayout) {
        changeLayoutChildColor(selectedLayout, R.color.nav_drawer_icon_color);
    }
}
