package net.optimusbs.videoapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.optimusbs.videoapp.Activities.VideoPlayer;
import net.optimusbs.videoapp.Classes.Video;
import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.SharedPreferenceClass;

import java.util.ArrayList;

/**
 * Created by Sohel on 1/7/2017.
 */

public class SearchYoutubeVideoRecyclerView extends RecyclerView.Adapter<SearchYoutubeVideoRecyclerView.VideoList> {

    ArrayList<Video> videos;
    Context context;

    String searchText;
    public SearchYoutubeVideoRecyclerView(ArrayList<Video> videos, Context context,String searchText) {
        this.videos = videos;
        this.context = context;
        this.searchText = searchText;
    }

    @Override
    public VideoList onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.youtube_video_list, parent, false);

        return new SearchYoutubeVideoRecyclerView.VideoList(itemView);
    }

    @Override
    public void onBindViewHolder(VideoList holder, int position) {
        final Video video = videos.get(position);
        holder.title.setText(video.getTitle());
        Picasso.with(context).load(video.getThumbnail()).into(holder.thumnail);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferenceClass.addToSavedSearch(searchText,context);
                Intent intent = new Intent(context, VideoPlayer.class);
                Bundle bundle = new Bundle();
                bundle.putString("video_id",video.getId());
                intent.putExtra("bundle",bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public static class VideoList extends RecyclerView.ViewHolder {

        TextView title;
        ImageView thumnail;

        public VideoList(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            thumnail = (ImageView) itemView.findViewById(R.id.video_thumbnail);
        }
    }
}
