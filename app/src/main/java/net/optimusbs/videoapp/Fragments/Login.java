package net.optimusbs.videoapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

import net.optimusbs.videoapp.Activities.Activity2;
import net.optimusbs.videoapp.Activities.HomeActivity;
import net.optimusbs.videoapp.Classes.User;
import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.Constants;

import java.util.Set;


public class Login extends Fragment implements FacebookCallback<LoginResult> {
    LoginButton loginButton;
    CallbackManager callbackManager;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDb;
    public Login() {
    }

    AccessTokenTracker accessTokenTracker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initialize(view);
        initializeFireBase();
        return view;
    }

    private void initializeFireBase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDb = firebaseDatabase.getReference(Constants.USERDB);
    }

    private void initialize(View view) {
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
                    ((Activity2) getActivity()).hideUserLayout();
                }else {
                    ((Activity2) getActivity()).showUserLayout();

                }
                Intent intent = new Intent(getContext(), HomeActivity.class);
                startActivity(intent);


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
      //  User user = new User(profile.getId(),profile.getName(),profile.getProfilePictureUri(500,500).toString());
        userDb.child(profile.getId()).child(Constants.USER_IMAGE).setValue(profile.getProfilePictureUri(500,500).toString());
        userDb.child(profile.getId()).child(Constants.USER_NAME).setValue(profile.getName());




    }

    @Override
    public void onCancel() {
        Log.d("permission","cancel");
        //Toast.makeText(getContext(), "cancel", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onError(FacebookException error) {
        Log.d("permission",error.toString());
       // Toast.makeText(getContext(), error.toString()+"cancel", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);


    }
}
