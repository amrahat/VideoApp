package net.optimusbs.videoapp.UtilityClasses;

/**
 * Created by Santo on 1/2/2017.
 */

public class Constants {
    public static final String API_KEY = "AIzaSyDzm8kggHcd1t4rC9_SbGBg1CfO71za0gM";
    String dataUrl = "https://www.googleapis.com/youtube/v3/videos?id=I6eH0ewFyCM" +
            "&key=AIzaSyDzm8kggHcd1t4rC9_SbGBg1CfO71za0gM" +
            "&part=contentDetails,snippet,,statistics,status";
    //public static final String API_KEY = "AIzaSyDzm8kggHcd1t4rC9_SbGBg1CfO71za0gM";
    //,
    static String firstPartUrl = "https://www.googleapis.com/youtube/v3/videos?id=";

    public static String getDataUrl(String videoId){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(firstPartUrl);
        stringBuilder.append(videoId);
        stringBuilder.append("&key=");
        stringBuilder.append(API_KEY);
        stringBuilder.append("&part=snippet,statistics");

        return stringBuilder.toString();
    }
}
