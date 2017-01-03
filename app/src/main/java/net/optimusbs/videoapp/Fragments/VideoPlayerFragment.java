package net.optimusbs.videoapp.Fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerView;

import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoPlayerFragment extends Fragment implements YouTubePlayer.OnInitializedListener {


    public VideoPlayerFragment() {
        // Required empty public constructor
    }
    String videoId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_youtube_player, container, false);
        videoId = getArguments().getString("video_id");
        YouTubePlayerView playerView = (YouTubePlayerView) view.findViewById(R.id.player);
        playerView.initialize(Constants.API_KEY,this);
        return view;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.cueVideo(videoId);
        youTubePlayer.play();


    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
    }
}
