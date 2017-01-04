package net.optimusbs.videoapp.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import net.optimusbs.videoapp.Fragments.Tags;
import net.optimusbs.videoapp.R;

public class Activity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        getBundleData();
    }

    private void getBundleData() {
        Bundle bundle = getIntent().getBundleExtra("bundle");

        if(bundle.containsKey("fragment_name")){
            String fragmentName = bundle.getString("fragment_name");
            navigateToFragment(fragmentName);
        }
    }

    private void navigateToFragment(String fragmentName) {
        switch (fragmentName){
            case "tags":
                getSupportFragmentManager().
                        beginTransaction().
                        setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right).
                        replace(R.id.container, new Tags()).
                        commit();
                break;
        }
    }
}
