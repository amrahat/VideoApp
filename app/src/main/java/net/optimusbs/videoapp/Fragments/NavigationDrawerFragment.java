package net.optimusbs.videoapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.joanzapata.iconify.widget.IconTextView;
import com.squareup.picasso.Picasso;

import net.optimusbs.videoapp.Activities.Activity2;
import net.optimusbs.videoapp.Activities.HomeActivity;
import net.optimusbs.videoapp.Classes.User;
import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.Constants;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Santo on 1/4/2017.
 */

public class NavigationDrawerFragment extends Fragment implements FacebookCallback<LoginResult>{
    private DrawerLayout mDrawerLayout;
    private View containerView;
    ActionBarDrawerToggle drawerToggle;

    LinearLayout tagLayout, homeLayout, savedSearchLayout, logInLayout, userLayout;

    TextView userName, loginText;

    IconTextView searchIcon;

    CircleImageView userImage;



    LoginButton loginButton;
    CallbackManager callbackManager;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDb;
    AccessTokenTracker accessTokenTracker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nav_drawer, container, false);

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
                String activity2Name = "Activities.Activity2";
                if (activityName.equals(activity2Name)) {
                    getFragmentManager().
                            beginTransaction().
                            setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right).
                            replace(R.id.container, new Tags()).
                            commit();
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

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                mDrawerLayout.closeDrawer(Gravity.LEFT);


            }
        });

        savedSearchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String activityName = getActivity().getLocalClassName();
                String activity2Name = "Activities.Activity2";
                if (activityName.equals(activity2Name)) {
                    getFragmentManager().
                            beginTransaction().
                            setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right).
                            replace(R.id.container, new SavedSearch()).
                            commit();
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
               /* String activityName = getActivity().getLocalClassName();
                String activity2Name = "Activities.Activity2";
                if (activityName.equals(activity2Name)) {
                    getFragmentManager().
                            beginTransaction().
                            setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right).
                            replace(R.id.container, new Login()).
                            commit();
                } else {
                    Intent intent = new Intent(getActivity(), Activity2.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("fragment_name", "login");
                    intent.putExtra("bundle", bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

                mDrawerLayout.closeDrawer(Gravity.LEFT);*/

                loginButton.performClick();
            }
        });


    }

    private void initView(View view) {

        tagLayout = (LinearLayout) view.findViewById(R.id.tags);
        homeLayout = (LinearLayout) view.findViewById(R.id.home);
        savedSearchLayout = (LinearLayout) view.findViewById(R.id.saved_searches);
        logInLayout = (LinearLayout) view.findViewById(R.id.login);
        userLayout = (LinearLayout) view.findViewById(R.id.userLayout);
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
                String activity2Name = "Activities.Activity2";
                if (activityName.equals(activity2Name)) {
                    getFragmentManager().
                            beginTransaction().
                            setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right).
                            replace(R.id.container, new Search()).
                            commit();
                } else {
                    Intent intent = new Intent(getActivity(), Activity2.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("fragment_name", "search");
                    intent.putExtra("bundle", bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        getLoggedInData();


    }

    private void getLoggedInData() {
        if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {
            setOnLoggedIn();
        } else {
            userLayout.setVisibility(View.GONE);
            loginText.setText("Log In");
        }

    }

    public void setOnLoggedIn() {
        loginText.setText("Log Out");

        userLayout.setVisibility(View.VISIBLE);
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
        userLayout.setVisibility(View.GONE);
        loginText.setText("Log In");

    }

    private void initializeFireBase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDb = firebaseDatabase.getReference(Constants.USERDB);
    }

    private void initializeFacebookButton(View view){
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        // If using in a fragment
        loginButton.setFragment(this);

        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager,this);

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(currentAccessToken==null){
                    setOnLoggedOut();
                }else {
                    setOnLoggedIn();

                }
//                mDrawerLayout.closeDrawer(Gravity.LEFT);

               /* Intent intent = new Intent(getContext(), HomeActivity.class);
                startActivity(intent);*/


            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        Profile profile = Profile.getCurrentProfile();
        User user = new User(profile.getId(),profile.getName(),profile.getProfilePictureUri(500,500).toString());
        userDb.child(profile.getId()).setValue(user);




    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);


    }
    @Override
    public void onCancel() {

    }

    @Override
    public void onError(FacebookException error) {

    }
}
