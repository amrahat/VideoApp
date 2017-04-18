package net.optimusbs.videoapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.widget.IconTextView;
import com.squareup.picasso.Picasso;

import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.activities.Activity2;
import net.optimusbs.videoapp.activities.VideoPlayer;
import net.optimusbs.videoapp.fragments.VideosUnderTag;
import net.optimusbs.videoapp.models.SearchResult;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by AMRahat on 3/16/2017.
 */

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultHolder> {

    private ArrayList<SearchResult> searchResults;
    private Activity context;
    private FragmentManager fragmentManager;

    public SearchResultAdapter(ArrayList<SearchResult> searchResults, Activity context, FragmentManager fragmentManager) {
        this.searchResults = searchResults;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public SearchResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_result_item,parent,false);
        return new SearchResultHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchResultHolder holder, int position) {
        final SearchResult searchResult = searchResults.get(position);
        holder.title.setText(searchResult.getTitle());

        if(searchResult.isTag()){
            holder.type.setText(context.getString(R.string.icon_tags));
            holder.tagFavLayout.setVisibility(View.VISIBLE);
            holder.videoViewLayout.setVisibility(View.GONE);
            holder.favCount.setText(searchResult.getFavouriteCount()+"");
            holder.tagVideoCount.setText(String.valueOf(searchResult.getTagVideoCount())+" videos");
        }else {
            holder.tagFavLayout.setVisibility(View.GONE);
            holder.videoViewLayout.setVisibility(View.VISIBLE);
            holder.viewCount.setText(searchResult.getViewCount());
            holder.type.setText(context.getString(R.string.icon_video));
            holder.likeCount.setText(searchResult.getLikeCount());
            holder.commentCount.setText(searchResult.getCommentCount());
            holder.publishedAtTime.setText(getDurationMessage(searchResult.getPublished_at()));

        }

        IconDrawable drawable = new IconDrawable(context, FontAwesomeIcons.fa_tags);
        drawable.setColorFilter(ContextCompat.getColor(context,R.color.toolbar_color), PorterDuff.Mode.SRC_ATOP);
        if(searchResult.getThumbnail()!=null && !searchResult.getThumbnail().isEmpty()){
            Picasso.with(context).load(searchResult.getThumbnail()).into(holder.videoThumbnail);
        }else {

            holder.videoThumbnail.setImageDrawable(drawable);

        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(searchResult.isTag()){
                    /*VideosUnderTag videosUnderTag = new VideosUnderTag();
                    Bundle bundle = new Bundle();
                    bundle.putString("tag_name", searchResult.getTitle());
                    videosUnderTag.setArguments(bundle);
                    fragmentManager.
                            beginTransaction().
                            add(R.id.container, videosUnderTag).
                            addToBackStack("specific_tag").
                            commit();*/

                    Intent intent = new Intent(context, Activity2.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("tag_name", searchResult.getTitle());
                    bundle.putString("fragment_name", "video_under_tag");
                    intent.putExtra("bundle",bundle);
                    context.startActivity(intent);


                }else {
                    Intent intent = new Intent(context, VideoPlayer.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("video_id", searchResult.getId());
                    intent.putExtra("bundle", bundle);

                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public class SearchResultHolder extends RecyclerView.ViewHolder {
        IconTextView type;
        TextView title;
        ImageView videoThumbnail;
        TextView favCount,viewCount,likeCount,commentCount,tagVideoCount,publishedAtTime;
        RelativeLayout tagFavLayout;
        LinearLayout videoViewLayout;
        public SearchResultHolder(View itemView) {
            super(itemView);
            type = (IconTextView) itemView.findViewById(R.id.type);
            title = (TextView) itemView.findViewById(R.id.title);
            favCount = (TextView) itemView.findViewById(R.id.favcount);
            viewCount = (TextView) itemView.findViewById(R.id.viewCount);
            likeCount = (TextView) itemView.findViewById(R.id.likeCount);
            tagVideoCount = (TextView) itemView.findViewById(R.id.video_count);
            commentCount = (TextView) itemView.findViewById(R.id.commentCount);
            publishedAtTime = (TextView) itemView.findViewById(R.id.published_at_time);
            videoThumbnail = (ImageView) itemView.findViewById(R.id.video_thumbnail);

            tagFavLayout = (RelativeLayout) itemView.findViewById(R.id.tag_fav_count_layout);
            videoViewLayout = (LinearLayout) itemView.findViewById(R.id.video_layout);
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
