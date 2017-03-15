package net.optimusbs.videoapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.facebookmodels.FacebookComment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by AMRahat on 3/14/2017.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {
    private ArrayList<FacebookComment> comments;
    private Context context;

    public CommentAdapter(ArrayList<FacebookComment> comments, Context context) {
        this.comments = comments;
        this.context = context;
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item,parent,false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, int position) {
        FacebookComment facebookComment = comments.get(position);
        holder.name.setText(facebookComment.getFrom().getName());
        holder.message.setText(facebookComment.getMessage());
        holder.date.setText(convertedDate(facebookComment.getCreatedTime()));

    }

    private String convertedDate(String inputStr){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
        try {
            Long timestamp =  dateFormat.parse(inputStr).getTime();
            Date d = new Date(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM hh:mm a");
            return sdf.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentHolder extends RecyclerView.ViewHolder {
        TextView name,message,date;
        public CommentHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            message = (TextView) itemView.findViewById(R.id.message);
            date = (TextView) itemView.findViewById(R.id.time);
        }
    }
}
