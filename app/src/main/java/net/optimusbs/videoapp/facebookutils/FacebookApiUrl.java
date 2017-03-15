package net.optimusbs.videoapp.facebookutils;

/**
 * Created by AMRahat on 2/27/2017.
 */

public class FacebookApiUrl {
    private static String BASE_URL = "https://graph.facebook.com/";
    private static String COMMENTS = "/comments?access_token=";
    private static String REACTIONS = "reactions?access_token=";
    private static String summary = "&summary=1&filter=toplevel";
    private static String FACEBOOK_PAGE_ID = "145580269224186";

    public static String getCommentUrl(String postid,String accessToken){
        return BASE_URL+FACEBOOK_PAGE_ID+"_"+postid+COMMENTS+accessToken+summary;
    }
    public static String getReactionUrl(String postid,String accessToken){
        return BASE_URL+FACEBOOK_PAGE_ID+"_"+postid+COMMENTS+accessToken+summary;
    }
}
