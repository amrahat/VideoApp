package net.optimusbs.videoapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.widget.IconTextView;

import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.fragments.CommentFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {
    @InjectView(R.id.back_button)
    IconTextView backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Iconify.with(new FontAwesomeModule());
        setContentView(R.layout.activity_comment);
        ButterKnife.inject(this);
        getBundle();

        backButton.setOnClickListener(this);
    }

    private void getBundle() {
        if(getIntent().hasExtra("bundle")){
            Bundle bundle = getIntent().getBundleExtra("bundle");
            CommentFragment commentFragment = new CommentFragment();
            commentFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,commentFragment).commit();

        }
    }

    @Override
    public void onClick(View view) {
        if(view==backButton){
            onBackPressed();
        }
    }
}
