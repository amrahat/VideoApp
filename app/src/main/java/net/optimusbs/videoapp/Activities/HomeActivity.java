package net.optimusbs.videoapp.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import net.optimusbs.videoapp.fragments.AllVideos;
import net.optimusbs.videoapp.fragments.HomeFragment;
import net.optimusbs.videoapp.fragments.MyVideosFragment;
import net.optimusbs.videoapp.fragments.NavigationDrawerFragment;
import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.SetUpToolbar;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    @InjectView(R.id.home)
    RelativeLayout homeLayout;

    @InjectView(R.id.all_videos)
    RelativeLayout allVideoLayout;

    @InjectView(R.id.my_videos)
    RelativeLayout myVideoLayout;

    @InjectView(R.id.notification)
    RelativeLayout notificationLayout;

    @InjectView(R.id.loader_layout)
    LinearLayout loaderLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Iconify.with(new FontAwesomeModule());
        setContentView(R.layout.activity_home);
        ButterKnife.inject(this);
        setUpToolbarAndDrawer();
        if(getIntent().hasExtra("fragment_name")){
            if(getIntent().getStringExtra("fragment_name").equals("all_videos")){
                navigateToAllVideos();
            }else if(getIntent().getStringExtra("fragment_name").equals("my_videos")){
                navigateToMyVideos();
            }
            else {
                navigateToHome();
            }
        }else {
            navigateToHome();
        }
        if(!getIntent().hasExtra("showloader")){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loaderLayout.setVisibility(View.GONE);
                }
            },4000);
        }else {
            loaderLayout.setVisibility(View.GONE);

        }

        /*getSupportFragmentManager().
                beginTransaction().
                //setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right).
                replace(R.id.container, new HomeFragment()).
                commit();*/


        homeLayout.setOnClickListener(this);
        allVideoLayout.setOnClickListener(this);
        myVideoLayout.setOnClickListener(this);
        notificationLayout.setOnClickListener(this);



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


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.home:
                navigateToHome();
                break;
            case R.id.all_videos:
                navigateToAllVideos();
                break;
            case R.id.my_videos:
                navigateToMyVideos();
                break;
        }
    }

    private void navigateToAllVideos() {
        getSupportFragmentManager().
                beginTransaction().
                //setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right).
                        replace(R.id.container, new AllVideos()).
                commit();}

    private void navigateToHome() {
        getSupportFragmentManager().
                beginTransaction().
                //setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right).
                        replace(R.id.container, new HomeFragment()).
                commit();

    }

    private void navigateToMyVideos() {
        getSupportFragmentManager().
                beginTransaction().
                //setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right).
                        replace(R.id.container, new MyVideosFragment()).
                commit();
    }
}
