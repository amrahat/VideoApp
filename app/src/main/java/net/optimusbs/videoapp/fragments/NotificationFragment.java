package net.optimusbs.videoapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.SetUpToolbar;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {


    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setBackGroundColor();
        SetUpToolbar.setTitle("Notifications",getActivity());

        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    private void setBackGroundColor() {
        getActivity().findViewById(R.id.my_videos_bar).setVisibility(View.GONE);
        getActivity().findViewById(R.id.all_videos_bar).setVisibility(View.GONE);
        getActivity().findViewById(R.id.notification_bar).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.recent_bar).setVisibility(View.GONE);

    }

}
