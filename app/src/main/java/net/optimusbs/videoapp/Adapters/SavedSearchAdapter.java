package net.optimusbs.videoapp.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.optimusbs.videoapp.R;

import java.util.ArrayList;

/**
 * Created by Sohel on 1/7/2017.
 */

public class SavedSearchAdapter extends RecyclerView.Adapter<SavedSearchAdapter.SavedSearch> {
    ArrayList<String> savedSearch;
    Context context;

    public SavedSearchAdapter(ArrayList<String> savedSearch, Context context) {
        this.savedSearch = savedSearch;
        this.context = context;
    }

    @Override
    public SavedSearch onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_search_list, parent, false);

        return new SavedSearchAdapter.SavedSearch(itemView);
    }

    @Override
    public void onBindViewHolder(SavedSearch holder, int position) {
        String tag = savedSearch.get(position);
        holder.title.setText(tag);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return savedSearch.size();
    }

    public static class SavedSearch extends RecyclerView.ViewHolder {

        TextView title;

        public SavedSearch(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tag_name);
        }
    }
}
