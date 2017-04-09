package net.optimusbs.videoapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.squareup.picasso.Picasso;

import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.activities.VideoPlayer;
import net.optimusbs.videoapp.fragments.VideosUnderTag;
import net.optimusbs.videoapp.models.SearchResult;

import java.util.ArrayList;

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
        }else {
            holder.tagFavLayout.setVisibility(View.GONE);
            holder.videoViewLayout.setVisibility(View.VISIBLE);
            holder.viewCount.setText(searchResult.getViewCount());
            holder.type.setText(context.getString(R.string.icon_video));
        }

        if(searchResult.getThumbnail()!=null && !searchResult.getThumbnail().isEmpty()){
            Picasso.with(context).load(searchResult.getThumbnail()).into(holder.videoThumbnail);
        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(searchResult.isTag()){
                    VideosUnderTag videosUnderTag = new VideosUnderTag();
                    Bundle bundle = new Bundle();
                    bundle.putString("tag_name", searchResult.getTitle());
                    videosUnderTag.setArguments(bundle);
                    fragmentManager.
                            beginTransaction().
                            add(R.id.container, videosUnderTag).
                            addToBackStack("specific_tag").
                            commit();
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
        TextView favCount,viewCount;
        RelativeLayout tagFavLayout;
        LinearLayout videoViewLayout;
        public SearchResultHolder(View itemView) {
            super(itemView);
            type = (IconTextView) itemView.findViewById(R.id.type);
            title = (TextView) itemView.findViewById(R.id.title);
            favCount = (TextView) itemView.findViewById(R.id.favcount);
            viewCount = (TextView) itemView.findViewById(R.id.viewcount);
            videoThumbnail = (ImageView) itemView.findViewById(R.id.video_thumbnail);

            tagFavLayout = (RelativeLayout) itemView.findViewById(R.id.tag_fav_count_layout);
            videoViewLayout = (LinearLayout) itemView.findViewById(R.id.video_layout);
        }
    }
}
