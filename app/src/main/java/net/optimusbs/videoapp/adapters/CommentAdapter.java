package net.optimusbs.videoapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.models.FirebaseComment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by AMRahat on 3/14/2017.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {
    private ArrayList<FirebaseComment> comments;
    private Context context;
    private String currentTimeStamp;

    public CommentAdapter(ArrayList<FirebaseComment> comments, Context context) {
        this.comments = comments;
        this.context = context;
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, int position) {
        FirebaseComment firebaseComment = comments.get(position);
        holder.name.setText(firebaseComment.getUserName());
        holder.message.setText(firebaseComment.getComment());
        holder.date.setText(getDifferenceText(currentTimeStamp, firebaseComment.getTimeStamp()));
        if (firebaseComment.getUserImage() != null && !firebaseComment.getUserImage().isEmpty()) {
            Picasso.with(context).load(firebaseComment.getUserImage()).stableKey(firebaseComment.getUserImage()).into(holder.userImage);
        }

    }

    private String convertedDate(String inputStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
        try {
            Long timestamp = dateFormat.parse(inputStr).getTime();
            Date d = new Date(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM hh:mm a");
            return sdf.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getDifferenceText(String currentTimeStamp, String commentTimeStamp) {
        try {
            Long difference = Long.parseLong(currentTimeStamp) - Long.parseLong(commentTimeStamp);

            int differenceInSec = Integer.parseInt(String.valueOf(difference))/1000;
            String message = "";
            //int differenceInSec = difference/1000;
            //Log.d("differenceInSec",differenceInSec+","+difference);
            if (differenceInSec < 60) { //1min
                message = "Just Now";
            } else if (differenceInSec < 3600) { //1hr
                int diffInMin = differenceInSec / 60;
                message = diffInMin + "min ago";
            } else if (differenceInSec < 86400) { //1day 60*24
                int diffInHour = differenceInSec / 3600;
                message = diffInHour + "h ago";
            } else if (differenceInSec < (86400 * 30)) { //1week
                int diffInDay = differenceInSec / 86400;
                message = diffInDay + "d ago";
            } else if (differenceInSec < (86400 * 30 * 12)) {
                int diffInMonth = differenceInSec / (86400 * 30);
                message = diffInMonth + "m ago";
            } else {
                int diffInYear = differenceInSec / (86400 * 30 * 12);
                message = diffInYear + "y ago";
            }
            return message;
        } catch (NumberFormatException e) {
            return "";
        }

    }


    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void setTimeStamp(String currentTimestamp) {
        this.currentTimeStamp = currentTimestamp;
    }

    public class CommentHolder extends RecyclerView.ViewHolder {
        TextView name, message, date;
        ImageView userImage;

        public CommentHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            message = (TextView) itemView.findViewById(R.id.message);
            date = (TextView) itemView.findViewById(R.id.time);
            userImage = (ImageView) itemView.findViewById(R.id.user_image);
        }
    }
}
