package net.optimusbs.videoapp.activities;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.joanzapata.iconify.widget.IconTextView;

import net.optimusbs.videoapp.fragments.Login;
import net.optimusbs.videoapp.fragments.NavigationDrawerFragment;
import net.optimusbs.videoapp.fragments.SavedSearch;
import net.optimusbs.videoapp.fragments.Search;
import net.optimusbs.videoapp.fragments.Tags;
import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.SetUpToolbar;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class Activity2 extends AppCompatActivity {
    Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    @InjectView(R.id.title_layout)
    RelativeLayout titleLayout;

    @InjectView(R.id.search_layout)
    RelativeLayout searchLayout;

    @InjectView(R.id.search_edittext)
    EditText searchEditTest;

    @InjectView(R.id.back_button)
    IconTextView backButton;
    NavigationDrawerFragment drawerFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        ButterKnife.inject(this);
        setUpToolbarAndDrawer();

        getBundleData();
    }

    private void getBundleData() {
        Bundle bundle = getIntent().getBundleExtra("bundle");

        if(bundle.containsKey("fragment_name")){
            String fragmentName = bundle.getString("fragment_name");
            navigateToFragment(fragmentName);
        }
    }

    private void navigateToFragment(String fragmentName) {
        switch (fragmentName){
            case "tags":
                getSupportFragmentManager().
                        beginTransaction().
                        setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right).
                        replace(R.id.container, new Tags()).
                        addToBackStack("tags").
                        commit();
                SetUpToolbar.setTitle("Tags",this);
                hideSearchEditText();
                break;

            case "search":
                getSupportFragmentManager().
                        beginTransaction().
                        setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right).
                        replace(R.id.container, new Search()).
                        addToBackStack("search").
                        commit();
                //SetUpToolbar.setTitle("Search",this);
                showSearchEditText();
                break;

            case "saved_search":
                getSupportFragmentManager().
                        beginTransaction().
                        setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right).
                        replace(R.id.container, new SavedSearch()).
                        addToBackStack("search").
                        commit();
                //SetUpToolbar.setTitle("Search",this);
                hideSearchEditText();
                break;

            case "login":
                getSupportFragmentManager().
                        beginTransaction().
                        setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right).
                        replace(R.id.container, new Login()).
                        addToBackStack("search").
                        commit();
                //SetUpToolbar.setTitle("Search",this);
                hideSearchEditText();
                break;
        }
    }

    private void showSearchEditText() {
        searchLayout.setVisibility(View.VISIBLE);
        titleLayout.setVisibility(View.GONE);
       /* getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);*//*

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/
        //drawerFragment.hideHamburgerIcon();

    }
    private void hideSearchEditText() {
        searchLayout.setVisibility(View.GONE);
        titleLayout.setVisibility(View.VISIBLE);
        //drawerFragment.showHamburgetIcon();

    }


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

    public void showUserLayout(){
        drawerFragment.setOnLoggedIn();
    }

    public void hideUserLayout(){
        drawerFragment.setOnLoggedOut();
    }
}
