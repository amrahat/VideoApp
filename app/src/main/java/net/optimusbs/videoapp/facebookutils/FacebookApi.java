package net.optimusbs.videoapp.facebookutils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.optimusbs.videoapp.UtilityClasses.VolleyRequest;
import net.optimusbs.videoapp.facebookmodels.CommentsResponse;

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

    public void getReactionsOfPost(String postid,String accessToken){
        String url = FacebookApiUrl.getReactionUrl(postid,accessToken);
        VolleyRequest.sendRequestGet(context, url, new VolleyRequest.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int reactionCount = jsonObject.getJSONObject("summary").getInt("total_count");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getCommentsOfPost(String postId,String accessToken){
        String url = FacebookApiUrl.getCommentUrl(postId,accessToken);
        VolleyRequest.sendRequestGet(context, url, new VolleyRequest.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Type type = new TypeToken<CommentsResponse>(){}.getType();
                CommentsResponse commentsResponse = gson.fromJson(result,type);
                Log.d(TAG, "onSuccess: commentcountfirstresponse"+commentsResponse.getData().size());
            }
        });
    }

    public void postAComment(String postId,String accessToken,String message){
        String url = FacebookApiUrl.getCommentUrl(postId,accessToken);
        HashMap<String,String> map = new HashMap<>();
        map.put("message",message);
        VolleyRequest.sendRequest(context, url,map, new VolleyRequest.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

            }
        });
    }
}
