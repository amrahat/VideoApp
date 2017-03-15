package net.optimusbs.videoapp.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.optimusbs.videoapp.models.Tag;
import net.optimusbs.videoapp.fragments.VideosUnderTag;
import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.SharedPreferenceClass;

import java.util.ArrayList;


/**
 * Created by Santo on 1/3/2017.
 */

public class TagRecyclerViewAdapter extends RecyclerView.Adapter<TagRecyclerViewAdapter.TagList> {

    ArrayList<Tag> tags;
    Activity activity;
    String tag;
    FragmentManager fragmentManager;
    boolean fromSearch;

    public TagRecyclerViewAdapter(ArrayList<Tag> tags, Activity activity, FragmentManager fragmentManager,boolean fromSearch) {
        this.tags = tags;
        this.activity = activity;
        this.fragmentManager = fragmentManager;
        this.fromSearch = fromSearch;
    }

    @Override
    public TagRecyclerViewAdapter.TagList onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_list, parent, false);

        return new TagList(itemView);
    }

    @Override
    public void onBindViewHolder(final TagRecyclerViewAdapter.TagList holder, int position) {
        final Tag tag = tags.get(position);
        StringBuilder sb = new StringBuilder(tag.getTagName());
        sb.setCharAt(0,Character.toUpperCase(sb.charAt(0)));
        holder.tagName.setText(sb.toString());

        if(tag.getVideoCount()>50){
            holder.videoCount.setText("50+ videos");

        }else {

            holder.videoCount.setText(tag.getVideoCount() + " videos");

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fromSearch){
                    SharedPreferenceClass.addToSavedSearch(tag.getTagName(),activity);
                }
                VideosUnderTag videosUnderTag = new VideosUnderTag();
                Bundle bundle = new Bundle();
                bundle.putString("tag_name", tag.getTagName());
                videosUnderTag.setArguments(bundle);

                fragmentManager.
                        beginTransaction().
                        replace(R.id.container, videosUnderTag).
                        addToBackStack("specific_tag").
                        commit();
            }
        });

    }


    @Override
    public int getItemCount() {
        return tags.size();
    }

    public static class TagList extends RecyclerView.ViewHolder {

        TextView videoCount, tagName;

        public TagList(View itemView) {
            super(itemView);
            videoCount = (TextView) itemView.findViewById(R.id.video_count);
            tagName = (TextView) itemView.findViewById(R.id.tag_name);

        }
    }
}

