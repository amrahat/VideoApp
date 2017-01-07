package net.optimusbs.videoapp.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.RelativeLayout;

import com.joanzapata.iconify.widget.IconTextView;

import net.optimusbs.videoapp.Activities.Activity2;
import net.optimusbs.videoapp.Activities.HomeActivity;
import net.optimusbs.videoapp.R;

/**
 * Created by Santo on 1/4/2017.
 */

public class NavigationDrawerFragment extends Fragment {
    private DrawerLayout mDrawerLayout;
    private View containerView;
    ActionBarDrawerToggle drawerToggle;

    LinearLayout tagLayout,homeLayout,savedSearchLayout;

    IconTextView searchIcon;

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
                if(activityName.equals(activity2Name)){
                    getFragmentManager().
                            beginTransaction().
                            setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right).
                            replace(R.id.container, new Tags()).
                            commit();
                }else {
                    Intent intent = new Intent(getActivity(),Activity2.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("fragment_name","tags");
                    intent.putExtra("bundle",bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

                mDrawerLayout.closeDrawer(Gravity.LEFT);

            }
        });

        homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),HomeActivity.class);

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
                if(activityName.equals(activity2Name)){
                    getFragmentManager().
                            beginTransaction().
                            setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right).
                            replace(R.id.container, new SavedSearch()).
                            commit();
                }else {
                    Intent intent = new Intent(getActivity(),Activity2.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("fragment_name","saved_search");
                    intent.putExtra("bundle",bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        });




    }

    private void initView(View view) {

        tagLayout = (LinearLayout) view.findViewById(R.id.tags);
        homeLayout = (LinearLayout) view.findViewById(R.id.home);
        savedSearchLayout = (LinearLayout) view.findViewById(R.id.saved_searches);
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
                if(activityName.equals(activity2Name)){
                    getFragmentManager().
                            beginTransaction().
                            setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right).
                            replace(R.id.container, new Search()).
                            commit();
                }else {
                    Intent intent = new Intent(getActivity(),Activity2.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("fragment_name","search");
                    intent.putExtra("bundle",bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        });


    }

    public void hideHamburgerIcon(){
        drawerToggle.setDrawerIndicatorEnabled(false);
    }
    public void showHamburgetIcon(){
        drawerToggle.setDrawerIndicatorEnabled(true);

    }
}
