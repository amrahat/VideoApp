package net.optimusbs.videoapp.fragments;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

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

import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.Constants;
import net.optimusbs.videoapp.interfaces.DialogDismissListener;

/**
 * Created by AMRahat on 1/25/2017.
 */


public class LoginDialog extends DialogFragment implements FacebookCallback<LoginResult> {
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    LoginButton loginButton;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDb;
    DialogDismissListener dialogDismissListener;

    public LoginDialog() {
    }

    public void setDialogDismissListener(DialogDismissListener dialogDismissListener) {
        this.dialogDismissListener = dialogDismissListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_dialog, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        initializeView(view);
        initializeFireBase();
        return view;
    }
    private void initializeFireBase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDb = firebaseDatabase.getReference(Constants.USERDB);
    }

    private void initializeView(View view) {
        loginButton = (LoginButton) view.findViewById(R.id.login_button_other);

        initializeLoginCallback();

    }

    private void initializeLoginCallback() {
        loginButton.setFragment(this);

        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager,this);

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
//                Log.d("here",oldAccessToken.getToken()+currentAccessToken.getToken());


            }
        };
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        Profile profile = Profile.getCurrentProfile();
        //  User user = new User(profile.getId(),profile.getName(),profile.getProfilePictureUri(500,500).toString());
        Log.d("idaslkdfjal",profile.getFirstName());
        userDb.child(profile.getId()).child(Constants.USER_IMAGE).setValue(profile.getProfilePictureUri(500,500).toString());
        userDb.child(profile.getId()).child(Constants.USER_NAME).setValue(profile.getName());
        dismiss();

        dialogDismissListener.onDismiss(true);
    }

    @Override
    public void onCancel() {
        Log.d("here","cancel");
        dismiss();
        dialogDismissListener.onDismiss(false);

    }

    @Override
    public void onError(FacebookException error) {
        Log.d("here","error"+error.toString());
        dismiss();
        dialogDismissListener.onDismiss(false);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);


    }
}
