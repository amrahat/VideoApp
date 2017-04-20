package net.optimusbs.videoapp.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.Constants;
import net.optimusbs.videoapp.UtilityClasses.FireBaseClass;
import net.optimusbs.videoapp.activities.VideoPlayer;

import java.util.Date;


/**
 * Created by AMRahat on 4/16/2017.
 */

public class FirebaseVideoAddListener extends Service {
    private String TAG = "FirebaseVideoListener";
    private boolean doneLoadingData = false;
    private boolean doneLoadingTagData = false;
    private FireBaseClass fireBaseClass;
    private boolean doneLoadingInitializeData = false;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fireBaseClass = new FireBaseClass(getApplicationContext());
        createServiceForTagChild();
    }

    private void createService() {
        fireBaseClass.getVideoRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: doneloadingdata");
                doneLoadingData = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        fireBaseClass.getVideoRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (doneLoadingData) {
                    String videoId = dataSnapshot.getKey();
                    Log.d(TAG, "onChildAdded: videoid: " + videoId);
                    String title = "", thumbnail = "";
                    if (dataSnapshot.hasChild(Constants.VIDEO_TITLE)) {
                        title = (String) dataSnapshot.child(Constants.VIDEO_TITLE).getValue();
                    }
                    if (dataSnapshot.hasChild("thumbnail")) {
                        thumbnail = (String) dataSnapshot.child("thumbnail").getValue();
                        Log.d(TAG, "onChildAdded: " + thumbnail);
                    }
                    showNotification(videoId, title, thumbnail, "");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void createServiceForTagChild() {
        fireBaseClass.getTagRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final long count = dataSnapshot.getChildrenCount();
                final long[] counter = {0};
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    dataSnapshot1.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            counter[0]++;
                            if (counter[0] == count) {
                                doneLoadingTagData = true;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        fireBaseClass.getTagRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded: " + dataSnapshot.getKey());
                final String tagname = dataSnapshot.getKey();
                dataSnapshot.getRef().addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (doneLoadingTagData) {
                            Log.d(TAG, "onChildAdded: " + dataSnapshot.getKey());
                            showNotification(tagname, (String) dataSnapshot.getValue());
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showNotification(final String tagName, final String videoId) {
        fireBaseClass.getVideoRef().child(videoId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildAdded: videoid: " + videoId);
                String title = "", thumbnail = "";
                if (dataSnapshot.hasChild(Constants.VIDEO_TITLE)) {
                    title = (String) dataSnapshot.child(Constants.VIDEO_TITLE).getValue();
                }
                if (dataSnapshot.hasChild("thumbnail")) {
                    thumbnail = (String) dataSnapshot.child("thumbnail").getValue();
                    Log.d(TAG, "onChildAdded: " + thumbnail);
                }
                showNotification(videoId, title, thumbnail, tagName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showNotification(String videoId, final String title, String thumbnail, String tagName) {
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.beautify_logo_noti_icon_22);
        mBuilder.setContentTitle("New video under " + tagName);


        Intent resultIntent = new Intent(this, VideoPlayer.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(VideoPlayer.class);
        Bundle bundle = new Bundle();
        bundle.putString("video_id", videoId);
        bundle.putString("tag", tagName);
        resultIntent.putExtra("bundle", bundle);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        mBuilder.setSound(uri);
        mBuilder.setAutoCancel(true);

        if (!TextUtils.isEmpty(thumbnail)) {
            Picasso.with(getApplicationContext()).load(thumbnail).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    mBuilder.setLargeIcon(bitmap);
                    android.support.v4.app.NotificationCompat.BigPictureStyle bigPictureStyle = new android.support.v4.app.NotificationCompat.BigPictureStyle();
                    if (!TextUtils.isEmpty(title)) {
                        bigPictureStyle.setSummaryText(title);

                    } else {
                        bigPictureStyle.setSummaryText("Click to see the video");

                    }
                    bigPictureStyle.bigPicture(bitmap);
                    mBuilder.setStyle(bigPictureStyle);
                    final Notification notification = mBuilder.build();
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(getUniqueTime(), notification);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        } else {
            if (!TextUtils.isEmpty(title)) {
                mBuilder.setContentText(title);

            } else {
                mBuilder.setContentText("Click to see the video");

            }
            final Notification notification = mBuilder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(getUniqueTime(), notification);
        }

        String pushKey = fireBaseClass.getNotificationRef().push().getKey();
        fireBaseClass.getNotificationRef().child(pushKey).child(Constants.VIDEO_ID).setValue(videoId);
        fireBaseClass.getNotificationRef().child(pushKey).child(Constants.TAG).setValue(tagName);
        fireBaseClass.getNotificationRef().child(pushKey).child(Constants.TIMESTAMP).setValue(ServerValue.TIMESTAMP);

    }

    private int getUniqueTime() {
        long time = new Date().getTime();
        String tmpStr = String.valueOf(time);
        String last4Str = tmpStr.substring(tmpStr.length() - 5);
        try {
            return Integer.valueOf(last4Str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
