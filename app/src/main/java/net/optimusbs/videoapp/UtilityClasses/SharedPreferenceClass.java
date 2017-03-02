package net.optimusbs.videoapp.UtilityClasses;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashSet;

/**
 * Created by Sohel on 1/7/2017.
 */

public class SharedPreferenceClass {

    public static void addToSavedSearch(String savedSearch,Context context){
        SharedPreferences pref = context.getSharedPreferences("saved_search",0);
        HashSet<String> savedSearchSet = getAllSavedSearch(context);
        savedSearchSet.add(savedSearch);

        pref.edit().putString("saved",new Gson().toJson(savedSearchSet)).commit();



    }

    public static HashSet<String> getAllSavedSearch(Context context){
        SharedPreferences pref = context.getSharedPreferences("saved_search",0);
        String savedSearch = pref.getString("saved","");
        if(!savedSearch.isEmpty()){
            //User user;
            Type type = new TypeToken<HashSet<String>>() {
            }.getType();
            return new Gson().fromJson(savedSearch,type);

        }

        return new HashSet<>();
    }

    /*public static void s(String chatroomid, TreeMap<String, Message2> messageMap, Context context){
        SharedPreferences pref = context.getSharedPreferences("chatroommessages",0);
        pref.edit().putString(chatroomid,new Gson().toJson(messageMap)).commit();


    }

    public static void addToRecentMessage(String userid,RecentMessageClass recentMessage,String withid,Context context){
        HashMap<String, RecentMessageClass> messageMap = getRecentMessageForUser(userid,context);
        messageMap.put(withid,recentMessage);
        setRecentMessageForUser(userid,messageMap,context);
    }*/


}
