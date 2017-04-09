package net.optimusbs.videoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.joanzapata.iconify.widget.IconTextView;

import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.SetUpToolbar;
import net.optimusbs.videoapp.fragments.Login;
import net.optimusbs.videoapp.fragments.NavigationDrawerFragment;
import net.optimusbs.videoapp.fragments.SavedSearch;
import net.optimusbs.videoapp.fragments.Tags;
import net.optimusbs.videoapp.fragments.VideosUnderTag;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class Activity2 extends AppCompatActivity {
    Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    @InjectView(R.id.title_layout)
    RelativeLayout titleLayout;





    @InjectView(R.id.search_icon)
    IconTextView searchIcon;
    NavigationDrawerFragment drawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.status_bar_color));
        ButterKnife.inject(this);
        setUpToolbarAndDrawer();

        getBundleData();

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity2.this, Search.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void getBundleData() {
        Bundle bundle = getIntent().getBundleExtra("bundle");

        if (bundle.containsKey("fragment_name")) {
            String fragmentName = bundle.getString("fragment_name");
            navigateToFragment(fragmentName,bundle);
        }
    }

    private void navigateToFragment(String fragmentName, Bundle bundle) {
        switch (fragmentName) {
            case "tags":
                getSupportFragmentManager().
                        beginTransaction().
                        setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right).
                        add(R.id.container, new Tags()).
                        commit();
                SetUpToolbar.setTitle("Tags", this);
                //hideSearchEditText();
                break;

            /*case "search":
                getSupportFragmentManager().
                        beginTransaction().
                        setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right).
                        add(R.id.container, new Search()).
                        commit();


                //SetUpToolbar.setTitle("Search",this);
                showSearchEditText();
                break;*/

            case "saved_search":
                getSupportFragmentManager().
                        beginTransaction().
                        setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right).
                        replace(R.id.container, new SavedSearch()).
                        addToBackStack("search").
                        commit();
                //SetUpToolbar.setTitle("Search",this);
                //hideSearchEditText();
                break;

            case "login":
                getSupportFragmentManager().
                        beginTransaction().
                        setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right).
                        replace(R.id.container, new Login()).
                        addToBackStack("search").
                        commit();
                //SetUpToolbar.setTitle("Search",this);
                //hideSearchEditText();

                break;

            case "video_under_tag":
                VideosUnderTag videosUnderTag = new VideosUnderTag();
                videosUnderTag.setArguments(bundle);

                getSupportFragmentManager().
                        beginTransaction().
                        replace(R.id.container, videosUnderTag).
                        commit();
        }
    }

   /* private void showSearchEditText() {
        searchLayout.setVisibility(View.VISIBLE);
        titleLayout.setVisibility(View.GONE);
       *//* getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);*//**//*

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*//*
        //drawerFragment.hideHamburgerIcon();

    }

    private void hideSearchEditText() {
        searchLayout.setVisibility(View.GONE);
        titleLayout.setVisibility(View.VISIBLE);
        //drawerFragment.showHamburgetIcon();

    }*/




    private void setUpToolbarAndDrawer() {
        toolbar = SetUpToolbar.setup("", this);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerFragment =
                (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, mDrawerLayout, toolbar);
    }

    public void showUserLayout() {
        drawerFragment.setOnLoggedIn();
    }

    public void hideUserLayout() {
        drawerFragment.setOnLoggedOut();
    }
}
