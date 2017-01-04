package net.optimusbs.videoapp.Activities;

import android.animation.Animator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.widget.IconTextView;

import net.optimusbs.videoapp.Adapters.VideoListByTagAdapter;
import net.optimusbs.videoapp.Fragments.HomeBannerFragment;
import net.optimusbs.videoapp.Fragments.HomeFragment;
import net.optimusbs.videoapp.Fragments.NavigationDrawerFragment;
import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.Constants;
import net.optimusbs.videoapp.UtilityClasses.OnSwipeTouchListener;
import net.optimusbs.videoapp.UtilityClasses.SetUpToolbar;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HomeActivity extends AppCompatActivity {

    Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    @InjectView(R.id.home)
    LinearLayout homeLayout;

    @InjectView(R.id.all_videos)
    LinearLayout allVideoLayout;

    @InjectView(R.id.my_videos)
    LinearLayout myVideoLayout;

    @InjectView(R.id.notification)
    LinearLayout notificationLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Iconify.with(new FontAwesomeModule());
        setContentView(R.layout.activity_home);
        //ButterKnife.inject(this);
        setUpToolbarAndDrawer();
        getSupportFragmentManager().
                beginTransaction().
                //setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right).
                replace(R.id.container, new HomeFragment()).
                commit();



    }

    private void setUpToolbarAndDrawer() {
        toolbar = SetUpToolbar.setup("Home", this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationDrawerFragment drawerFragment =
                (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, mDrawerLayout, toolbar);
    }




}
