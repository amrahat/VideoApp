package net.optimusbs.videoapp.facebookutils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.optimusbs.videoapp.UtilityClasses.VolleyRequest;
import net.optimusbs.videoapp.facebookmodels.CommentsResponse;
import net.optimusbs.videoapp.interfaces.OnCommentLoadListener;
import net.optimusbs.videoapp.interfaces.OnLikeLoadListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Created by AMRahat on 2/27/2017.
 */

public class FacebookApi {
    private Context context;
    private Gson gson;
    private String TAG = "FacebookApiResponse";

    public FacebookApi(Context context) {
        this.context = context;
        gson = new Gson();
    }

    public void getReactionsOfPost(String postid, String accessToken, final OnLikeLoadListener onLikeLoadListener) {
        String url = FacebookApiUrl.getReactionUrl(postid, accessToken);
        VolleyRequest.sendRequestGet(context, url, new VolleyRequest.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int reactionCount = jsonObject.getJSONObject("summary").getInt("total_count");
                    onLikeLoadListener.onLikeLoad(reactionCount);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getCommentsOfPost(String postId, String accessToken, final OnCommentLoadListener onCommentLoadListener) {
        String url = FacebookApiUrl.getCommentUrl(postId, accessToken);
        Log.d(TAG, "getCommentsOfPost: "+url);
        VolleyRequest.sendRequestGet(context, url, new VolleyRequest.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Type type = new TypeToken<CommentsResponse>() {
                }.getType();
                CommentsResponse commentsResponse = gson.fromJson(result, type);
                Log.d(TAG, "onSuccess: commentcountfirstresponse" + commentsResponse.getData().size());
                onCommentLoadListener.onCommentLoad(commentsResponse);
            }
        });
    }

    public void postAComment(String postId, String accessToken, final String message, final OnCommentPostListener onCommentPostListener) {
        String url = FacebookApiUrl.getCommentUrl(postId, accessToken);
        Log.d(TAG, "postAComment: url> "+url);
        HashMap<String, String> map = new HashMap<>();
        map.put("message", message);
        VolleyRequest.sendRequest(context, url, map, new VolleyRequest.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    Log.d(TAG, "onSuccess: response: "+result);
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.has("id")){
                        onCommentPostListener.onCommentPost(message,jsonObject.getString("id"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void likeAPost(String postid) {
        Bundle bundle = new Bundle();
        bundle.putString("type","LIKE");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/"+postid+"/reactions",
                bundle,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
            /* handle the result */
                        Log.d(TAG, "onCompleted: "+response.toString());
                    }
                }
        ).executeAsync();
    }
}
