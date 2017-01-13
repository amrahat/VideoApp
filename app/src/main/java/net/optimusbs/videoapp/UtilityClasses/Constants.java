package net.optimusbs.videoapp.UtilityClasses;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Santo on 1/2/2017.
 */

public class Constants {
    public static final String HOME_BANNER_REF = "home-banners";
    public static final String TAG_REF = "tags";
    public static final String API_KEY = "AIzaSyDzm8kggHcd1t4rC9_SbGBg1CfO71za0gM";
    public static final String USERDB = "users";
    public static final String LIKED = "liked_videos" ;
    public static String HOME_CAT_REF = "home-categories";
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
}
