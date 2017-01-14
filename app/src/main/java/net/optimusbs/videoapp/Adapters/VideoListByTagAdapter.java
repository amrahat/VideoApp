package net.optimusbs.videoapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.optimusbs.videoapp.Activities.VideoPlayer;
import net.optimusbs.videoapp.Classes.Video;
import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.Constants;
import net.optimusbs.videoapp.UtilityClasses.FireBaseClass;
import net.optimusbs.videoapp.UtilityClasses.VolleyRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Santo on 1/3/2017.
 */

public class VideoListByTagAdapter extends RecyclerView.Adapter<VideoListByTagAdapter.VideoList> {

    ArrayList<String> videoIds;
    Context context;
    String tag;
    FireBaseClass fireBaseClass;

    public VideoListByTagAdapter(ArrayList<String> videoIds, Context context,String tag) {
        this.videoIds = videoIds;
        this.context = context;
        this.tag = tag;
        fireBaseClass = new FireBaseClass(context);
    }

    @Override
    public VideoListByTagAdapter.VideoList onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list_by_tag, parent, false);

        return new VideoList(itemView);
    }

    @Override
    public void onBindViewHolder(final VideoListByTagAdapter.VideoList holder, int position) {
        final String videoId = videoIds.get(position);
        String url = Constants.getDataUrl(videoId);
        Log.d("url",url);
        VolleyRequest.sendRequestGet(context, url, new VolleyRequest.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                //Log.d("result", result);
                final Video video = parseJson(result);
                if(video!=null){
                    holder.title.setText(video.getTitle());
                    holder.viewCount.setText(video.getViewCount());
                    holder.likeCount.setText(video.getLikeCount());
                    holder.commentCount.setText(video.getCommentCount());
                    Picasso.with(context).load(video.getThumbnail()).into(holder.thumbnail);

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, VideoPlayer.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("video", video);
                            bundle.putString("tag",tag);

                            intent.putExtra("bundle", bundle);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    });

                    fireBaseClass.addVideoToDatabase(videoId,Constants.VIDEO_TITLE,video.getTitle());
                    fireBaseClass.addVideoToDatabase(videoId,Constants.VIDEO_THUMBNAIL,video.getThumbnail());
                    fireBaseClass.addVideoToDatabase(videoId,Constants.VIDEO_DESCRIPTION,video.getDescription());

                }
            }
        });


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
            if(statisticsObject.has("likeCount")){
                likeCount = statisticsObject.getString("likeCount");
            }else{
                likeCount = "0";
            }
            String commentCount = statisticsObject.getString("commentCount");


            Video video = new Video(videoId, title, description, publishedAt, viewCount, likeCount, commentCount,thumbnail);
            return video;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public int getItemCount() {
        return videoIds.size();
    }

    public static class VideoList extends RecyclerView.ViewHolder {

        ImageView thumbnail;
        TextView title,viewCount,likeCount,commentCount;

        public VideoList(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.video_thumbnail);
            title = (TextView) itemView.findViewById(R.id.title);
            viewCount = (TextView) itemView.findViewById(R.id.viewCount);
            likeCount = (TextView) itemView.findViewById(R.id.likeCount);
            commentCount = (TextView) itemView.findViewById(R.id.commentCount);
        }
    }
}
