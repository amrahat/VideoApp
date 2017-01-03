package net.optimusbs.videoapp.Fragments;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.joanzapata.iconify.widget.IconTextView;

import net.optimusbs.videoapp.Activities.VideoPlayer;
import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeBannerFragment extends Fragment {
    //YouTubePlayerView/* playerView1,*/ ;
    YouTubeThumbnailView playerView1, playerView2, playerView3;
    String[] videoIdArray;

    IconTextView loader1, loader2, loader3, play1, play2, play3;

    public HomeBannerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_banner, container, false);

        initializeView(view);

        getBundleData();

        return view;
    }

    private void getBundleData() {
        Bundle bundle = getArguments();

        videoIdArray = bundle.getStringArray("video_id_array");

       /* getFragmentManager().beginTransaction().replace(R.id.youtube_player_1, new VideoPlayerFragment()).commit();
        getFragmentManager().beginTransaction().replace(R.id.youtube_player_2, new VideoPlayerFragment()).commit();
        getFragmentManager().beginTransaction().replace(R.id.youtube_player_3, new VideoPlayerFragment()).commit();*/


        if (videoIdArray != null) initializeVideos();
    }

    private void initializeVideos() {
        initializePlayerViewAndPlay(videoIdArray[0], playerView1, loader1, play1);
        initializePlayerViewAndPlay(videoIdArray[1], playerView2, loader2, play2);
        initializePlayerViewAndPlay(videoIdArray[2], playerView3, loader3, play3);

    }

    private void initializeView(View view) {
        playerView1 = (YouTubeThumbnailView) view.findViewById(R.id.youtube_player_1);
        playerView2 = (YouTubeThumbnailView) view.findViewById(R.id.youtube_player_2);
        playerView3 = (YouTubeThumbnailView) view.findViewById(R.id.youtube_player_3);

        loader1 = (IconTextView) view.findViewById(R.id.loader1);
        loader2 = (IconTextView) view.findViewById(R.id.loader2);
        loader3 = (IconTextView) view.findViewById(R.id.loader3);
        play1 = (IconTextView) view.findViewById(R.id.play1);
        play2 = (IconTextView) view.findViewById(R.id.play2);
        play3 = (IconTextView) view.findViewById(R.id.play3);

        /*playerView1 = (LinearLayout) view.findViewById(R.id.youtube_player_1);
        playerView2 = (LinearLayout) view.findViewById(R.id.youtube_player_2);
        playerView3 = (LinearLayout) view.findViewById(R.id.youtube_player_3);*/


    }


    private void initializePlayerViewAndPlay(final String videoId, YouTubeThumbnailView playerView, final IconTextView loader, final IconTextView play) {
        /*playerView.initialize(Constants.API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                if(!b) {
                    youTubePlayer.cueVideo(videoId);
                    youTubePlayer.play();
                 //   initializePlayerViewAndPlay(videoIdArray[1],playerView2);

                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });*/

        playerView.initialize(Constants.API_KEY, new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
                youTubeThumbnailLoader.setVideo(videoId);
                youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                    @Override
                    public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                        loader.setVisibility(View.GONE);
                        play.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {

                    }
                });

            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });

        playerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   VideoPlayerFragment videoPlayerFragment = new VideoPlayerFragment();
              //  Toast.makeText(getActivity(), videoId, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), VideoPlayer.class);
                Bundle bundle = new Bundle();
                bundle.putString("video_id",videoId);

                intent.putExtra("bundle",bundle);

                startActivity(intent);

                /*videoPlayerFragment.setArguments(bundle);

                getFragmentManager().beginTransaction().replace(R.id.activity_main,videoPlayerFragment).addToBackStack("video").commit();
 */           }
        });
    }


}
