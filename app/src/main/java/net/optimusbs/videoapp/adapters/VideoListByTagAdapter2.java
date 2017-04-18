package net.optimusbs.videoapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.squareup.picasso.Picasso;

import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.FireBaseClass;
import net.optimusbs.videoapp.activities.VideoPlayer;
import net.optimusbs.videoapp.models.Video;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

        if (video.getCommentCount() == null) {
        } else {
            holder.commentCount.setText(video.getCommentCount());

        }

        if (video.getLikeCount() == null) {

        } else {
            holder.likeCount.setText(video.getLikeCount());

        }


        String date = video.getPublished_time();
        Log.d("date", "onBindViewHolder: " + video.getPublished_time());
        holder.duration.setText(getDurationMessage(date));


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


    @Override
    public int getItemCount() {
        return videos.size();
    }

    public static class VideoList extends RecyclerView.ViewHolder {

        private final LinearLayout viewCountLayout;
        private TextView duration;
        ImageView thumbnail;
        TextView title, viewCount, likeCount, commentCount;

        public VideoList(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.video_thumbnail);
            title = (TextView) itemView.findViewById(R.id.title);
            viewCount = (TextView) itemView.findViewById(R.id.viewCount);
            viewCountLayout = (LinearLayout) itemView.findViewById(R.id.view_count_layout);
            likeCount = (TextView) itemView.findViewById(R.id.likeCount);
            commentCount = (TextView) itemView.findViewById(R.id.commentCount);
            duration = (TextView) itemView.findViewById(R.id.duration);
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
