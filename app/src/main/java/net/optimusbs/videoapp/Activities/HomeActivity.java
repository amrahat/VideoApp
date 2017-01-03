package net.optimusbs.videoapp.Activities;

import android.os.Bundle;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import net.optimusbs.videoapp.Fragments.HomeBannerFragment;
import net.optimusbs.videoapp.R;

public class HomeActivity extends YouTubeBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Iconify.with(new FontAwesomeModule());
        setContentView(R.layout.activity_home);

        HomeBannerFragment homeBannerFragment = new HomeBannerFragment();
        Bundle bundle = new Bundle();
        String[] videoIdArray = {"FNb1iB3VqEc","Qro_C8B-zxA","XuEEwRvuu0A"};

        bundle.putStringArray("video_id_array",videoIdArray);

        homeBannerFragment.setArguments(bundle);


        getFragmentManager().beginTransaction().replace(R.id.banner_container, homeBannerFragment).addToBackStack("recent").commit();



    }
}
