package net.optimusbs.videoapp.UtilityClasses;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;

import net.optimusbs.videoapp.Activities.Activity2;
import net.optimusbs.videoapp.Fragments.Search;
import net.optimusbs.videoapp.R;


/**
 * Created by PC-2 on 12/11/2016.
 */

public class SetUpToolbar {

    public static Toolbar setup(String text, final Activity activity){
        Toolbar toolbar = (android.support.v7.widget.Toolbar) activity.findViewById(R.id.tabanim_toolbar);
        TextView titleTxt = (TextView) activity.findViewById(R.id.title);
        titleTxt.setText(text);

        toolbar.setTitle(text);


        return toolbar;
    }

   /* public static void visibleSearch(Activity activity){
        RelativeLayout relativeLayout = (RelativeLayout) activity.findViewById(R.id.search_container);
        relativeLayout.setVisibility(View.VISIBLE);
    }*/

    public static void setTitle(String title, Activity activity){
        TextView titleTxt = (TextView) activity.findViewById(R.id.title);
        titleTxt.setText(title);
    }


}
