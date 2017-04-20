package net.optimusbs.videoapp.adapters;

import android.app.Notification;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.models.FirebaseNotification;

import java.util.ArrayList;

/**
 * Created by AMRahat on 4/20/2017.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {
    private Context context;
    private ArrayList<FirebaseNotification> notifications;
    private Long currentTimeStamp;

    public NotificationAdapter(Context context, ArrayList<FirebaseNotification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @Override
    public NotificationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        return new NotificationHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationHolder holder, int position) {
        FirebaseNotification firebaseNotification = notifications.get(position);
        holder.introTextTv.setText("New video under " + firebaseNotification.getTagName());
        holder.videoTitleTv.setText(firebaseNotification.getVideoTitle());
        holder.durationTv.setText(getDifferenceText(String.valueOf(currentTimeStamp),String.valueOf(firebaseNotification.getTimestamp())));
        Picasso.with(context).load(firebaseNotification.getVideoThumbnail()).stableKey(firebaseNotification.getVideoThumbnail()).into(holder.videoThumbnail);

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public void setTimeStamp(Long currentTimestamp) {
        this.currentTimeStamp = currentTimestamp;
    }

    public class NotificationHolder extends RecyclerView.ViewHolder {
        TextView introTextTv, videoTitleTv, durationTv;
        ImageView videoThumbnail;

        public NotificationHolder(View itemView) {
            super(itemView);
            introTextTv = (TextView) itemView.findViewById(R.id.intro_text);
            videoTitleTv = (TextView) itemView.findViewById(R.id.title);
            durationTv = (TextView) itemView.findViewById(R.id.duration);
            videoThumbnail = (ImageView) itemView.findViewById(R.id.video_thumbnail);
        }
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
}
