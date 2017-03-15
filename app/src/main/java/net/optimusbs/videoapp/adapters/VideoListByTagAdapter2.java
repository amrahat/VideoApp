package net.optimusbs.videoapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.FireBaseClass;
import net.optimusbs.videoapp.activities.VideoPlayer;
import net.optimusbs.videoapp.models.Video;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by AMRahat on 3/7/2017.
 */

public class VideoListByTagAdapter2 extends RecyclerView.Adapter<VideoListByTagAdapter2.VideoList> {

    private ArrayList<Video> videos;
    private Context context;
    private String tag;
    private FireBaseClass fireBaseClass;

    public VideoListByTagAdapter2(ArrayList<Video> videos, Context context, String tag) {
        this.videos = videos;
        this.context = context;
        this.tag = tag;
        fireBaseClass = new FireBaseClass(context);
    }

    @Override
    public VideoListByTagAdapter2.VideoList onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list_by_tag, parent, false);

        return new VideoList(itemView);
    }

    @Override
    public void onBindViewHolder(final VideoListByTagAdapter2.VideoList holder, int position) {
        final Video video = videos.get(position);

        holder.title.setText(video.getTitle());
        if (video.getViewCount() == null) {
            holder.viewCountLayout.setVisibility(View.GONE);
        } else {
            holder.viewCountLayout.setVisibility(View.VISIBLE);
            holder.viewCount.setText(video.getViewCount());
        }
        Picasso.with(context).load(video.getThumbnail()).stableKey(video.getThumbnail()).into(holder.thumbnail);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoPlayer.class);
                Bundle bundle = new Bundle();
                bundle.putString("video_id", video.getId());
                bundle.putString("tag", tag);

                intent.putExtra("bundle", bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        /*String url = Constants.getDataUrl(videoId);
        Log.d("url", url);
        VolleyRequest.sendRequestGet(context, url, new VolleyRequest.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                //Log.d("result", result);
                final Video video = parseJson(result);
                if (video != null) {
                    holder.title.setText(video.getTitle());
                    holder.viewCount.setText(video.getViewCount());
                    // holder.likeCount.setText(video.getLikeCount());
                    //holder.commentCount.setText(video.getCommentCount());
                    Picasso.with(context).load(video.getThumbnail()).stableKey(video.getThumbnail()).into(holder.thumbnail);

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, VideoPlayer.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("video", video);
                            bundle.putString("tag", tag);

                            intent.putExtra("bundle", bundle);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    });

                    fireBaseClass.addVideoToDatabase(videoId, Constants.VIDEO_TITLE, video.getTitle());
                    fireBaseClass.addVideoToDatabase(videoId, Constants.VIDEO_THUMBNAIL, video.getThumbnail());
                    fireBaseClass.addVideoToDatabase(videoId, Constants.VIDEO_DESCRIPTION, video.getDescription());

                }
            }
        });*/


        //

    }

    private Video parseJson(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject itemsObject = jsonObject.getJSONArray("items").getJSONObject(0);
            JSONObject snippetObject = itemsObject.getJSONObject("snippet");
            JSONObject statisticsObject = itemsObject.getJSONObject("statistics");
            String videoId = itemsObject.getString("id");
            String thumbnail = snippetObject.getJSONObject("thumbnails").getJSONObject("default").getString("url");
            String publishedAt = snippetObject.getString("publishedAt");
            String title = snippetObject.getString("title");
            String description = snippetObject.getString("description");
            String viewCount = statisticsObject.getString("viewCount");
            String likeCount;
            if (statisticsObject.has("likeCount")) {
                likeCount = statisticsObject.getString("likeCount");
            } else {
                likeCount = "0";
            }
            String commentCount = statisticsObject.getString("commentCount");


            Video video = new Video(videoId, title, description, publishedAt, viewCount, likeCount, commentCount, thumbnail);
            return video;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public static class VideoList extends RecyclerView.ViewHolder {

        private final LinearLayout viewCountLayout;
        ImageView thumbnail;
        TextView title, viewCount/*,likeCount,commentCount*/;

        public VideoList(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.video_thumbnail);
            title = (TextView) itemView.findViewById(R.id.title);
            viewCount = (TextView) itemView.findViewById(R.id.viewCount);
            viewCountLayout = (LinearLayout) itemView.findViewById(R.id.view_count_layout);
            //likeCount = (TextView) itemView.findViewById(R.id.likeCount);
            //commentCount = (TextView) itemView.findViewById(R.id.commentCount);
        }
    }
}
