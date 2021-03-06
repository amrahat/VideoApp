package net.optimusbs.videoapp.UtilityClasses;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Santo on 1/2/2017.
 */

public class Constants {
    public static final String HOME_BANNER_REF = "home-banners";
    public static final String TAG_REF = "tags";
    public static final String VIDEO_REF = "videos";
    public static final String API_KEY = "AIzaSyDzm8kggHcd1t4rC9_SbGBg1CfO71za0gM";
    public static final String USERDB = "users_db";
    public static final String LIKED = "liked_videos" ;
    public static final String FAVOURITE = "favorite_videos" ;
    public static final String USER_WHO_LIKED = "liked_users" ;
    public static final String USER_WHO_FAVOURITE = "favourite_users" ;
    public static final String USER_NAME = "name" ;
    public static final String USER_IMAGE = "image" ;
    public static final String SEARCH_REF = "search";
    public static final String LIKE_REF = "likes";
    public static final String VIDEO_ID = "video_id";
    public static final String TAG = "tag";
    public static String FACEBOOK_LIKE_COUNT = "facebook_like_count";
    public static String FACEBOOK_COMMENT_COUNT = "facebook_comment_count";
    public static String FACEBOOK_POST_ID = "facebook_post_id";
    public static final String VIEW_COUNT = "viewCount" ;
    public static String HOME_CAT_REF = "home-categories";
    public static String VIDEO_TITLE = "title";
    public static String VIDEO_THUMBNAIL = "thumbnail";
    public static String VIDEO_PUBLISHED_AT = "published_time";
    public static String VIDEO_DESCRIPTION = "description";
    public static String FAVOURITE_REF = "fav_videos";
    public static String COMMENT_REF = "comments";
    public static String FAVOURITE_TAG_REF = "fav_tag";
    public static String NOTIFICATION_REF = "notifications";
    public static String TIMESTAMP = "timestamp";
    String dataUrl = "https://www.googleapis.com/youtube/v3/videos?id=I6eH0ewFyCM" +
            "&key=AIzaSyDzm8kggHcd1t4rC9_SbGBg1CfO71za0gM" +
            "&part=contentDetails,snippet,,statistics,status";

    String relatedVideoUrl = "https://www.googleapis.com/youtube/v3/search?part=snippet" +
            "&key=AIzaSyDzm8kggHcd1t4rC9_SbGBg1CfO71za0gM" +
            "&safeSearch=strict&relatedToVideoId=PmSACbXO6Bw&type=video";
    //public static final String API_KEY = "AIzaSyDzm8kggHcd1t4rC9_SbGBg1CfO71za0gM";
    //,
    static String firstPartUrl = "https://www.googleapis.com/youtube/v3/videos?id=";
    static String searcgFirstPartUrl = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=";
    static String relatedVideoFirstPartUrl = "https://www.googleapis.com/youtube/v3/search?part=snippet";
    public static String TOP_BAR_BACK_COLOR = "#044D70";

    public static String getDataUrl(String videoId){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(firstPartUrl);
        stringBuilder.append(videoId);
        stringBuilder.append("&key=");
        stringBuilder.append(API_KEY);
        stringBuilder.append("&part=snippet,statistics");

        return stringBuilder.toString();
    }

    public static String getSearchUrl(String query){

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(searcgFirstPartUrl);
        stringBuilder.append(query);
        stringBuilder.append("&key=");
        stringBuilder.append(API_KEY);
        stringBuilder.append("&safeSearch=strict");
        stringBuilder.append("&type=video");

        return stringBuilder.toString().replaceAll("\\s","%20");

    }

    public static String getSearchUrl(String query,String pageToken){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(searcgFirstPartUrl);
        stringBuilder.append(query);
        stringBuilder.append("&key=");
        stringBuilder.append(API_KEY);
        stringBuilder.append("&safeSearch=strict");
        stringBuilder.append("&type=video");
        stringBuilder.append("&pageToken=");
        stringBuilder.append(pageToken);
        return stringBuilder.toString().replaceAll("\\s","%20");

    }

    public static String getRelatedVideoUrl(String videoId){

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(relatedVideoFirstPartUrl);
            stringBuilder.append("&key=");
            stringBuilder.append(API_KEY);
            stringBuilder.append("&safeSearch=strict");
            stringBuilder.append("&type=video");
            stringBuilder.append("&relatedToVideoId=");
            stringBuilder.append(videoId);
            return stringBuilder.toString().replaceAll("\\s","%20");


    }

    public static final String HOME = "home";
    public static final String ALL_VIDEOS = "all_videos";
    public static final String MY_VIDEOS = "my_videos";
    public static final String TAGS = "tags";
    public static final String SAVED_SEARCH = "saved_search";



}
