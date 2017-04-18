package net.optimusbs.videoapp.adapters;

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

import net.optimusbs.videoapp.activities.VideoPlayer;
import net.optimusbs.videoapp.models.Video;
import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.SharedPreferenceClass;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
        holder.publishedAtTime.setText(getDurationMessage(video.getPublished_time()));
        //holder.viewCount.setText(video.getViewCount());
        Picasso.with(context).load(video.getThumbnail()).stableKey(video.getThumbnail()).into(holder.thumnail);

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
        TextView publishedAtTime/*,viewCount*/;

        public VideoList(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            thumnail = (ImageView) itemView.findViewById(R.id.video_thumbnail);
            publishedAtTime = (TextView) itemView.findViewById(R.id.published_at_time);
           // viewCount = (TextView) itemView.findViewById(R.id.viewCount);
        }
    }

    private String getDurationMessage(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
        try {
            Date date1 = formatter.parse(date);
            Log.d("date", "onCreate: " + date1.getTime());
            Date date2 = new Date();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Log.d("date", "current: " + timestamp.getTime());

            String text = getDifferenceText(timestamp.getTime(), date1.getTime());
            Log.d("differencemessage", "onBindViewHolder: " + text);

            return text;
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        } catch (NullPointerException e) {
            return "";
        }

    }

    private String getDifferenceText(Long currentTimeStamp, Long commentTimeStamp) {
        try {
            Long difference = currentTimeStamp - commentTimeStamp;

            int differenceInSec = (int) (difference / 1000);
            Log.d("getDifferenceText", "getDifferenceText: " + differenceInSec);
            String message = "";
            //int differenceInSec = difference/1000;
            //Log.d("differenceInSec",differenceInSec+","+difference);
            if (differenceInSec < 60) { //1min
                message = "Just Now";
            } else if (differenceInSec < 3600) { //1hr
                int diffInMin = differenceInSec / 60;
                message = diffInMin + getSingularOrPluralText(diffInMin, " min") + " ago";
            } else if (differenceInSec < 86400) { //1day 60*24
                int diffInHour = differenceInSec / 3600;
                message = diffInHour + getSingularOrPluralText(diffInHour, " hour") + " ago";
            } else if (differenceInSec < (86400 * 30)) { //1week
                int diffInDay = differenceInSec / 86400;
                message = diffInDay + getSingularOrPluralText(diffInDay, " day") + " ago";
            } else if (differenceInSec < (86400 * 30 * 12)) {
                int diffInMonth = differenceInSec / (86400 * 30);
                message = diffInMonth + getSingularOrPluralText(diffInMonth, " month") + " ago";
            } else {
                int diffInYear = differenceInSec / (86400 * 30 * 12);
                message = diffInYear + getSingularOrPluralText(diffInYear, " year") + " ago";
            }
            return message;
        } catch (NumberFormatException e) {
            return "";
        }

    }

    private String getSingularOrPluralText(int diff, String text) {
        if (diff <= 1) {
            return text;
        }
        return text + "s";

    }
}
