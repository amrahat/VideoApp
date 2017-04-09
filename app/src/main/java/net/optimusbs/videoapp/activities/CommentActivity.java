package net.optimusbs.videoapp.activities;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.widget.IconTextView;

import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.fragments.CommentFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static net.optimusbs.videoapp.UtilityClasses.Constants.API_KEY;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {
    @InjectView(R.id.back_button)
    IconTextView backButton;

    @InjectView(R.id.webview)
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Iconify.with(new FontAwesomeModule());
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.status_bar_color));
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

            //loadCommentIntoWebView(bundle.getString("post_id"));

        }
    }

    private void loadCommentIntoWebView(String post_id) {
        String url = "https://www.facebook.com/mannitsolutionsltd/posts/"+post_id;
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setDomStorageEnabled(true);

        webView.loadDataWithBaseURL(url,
                "<html><head></head><body><div id=\"fb-root\"></div><div id=\"fb-root\"></div><script>(function(d, s, id) {var js, fjs = d.getElementsByTagName(s)[0];if (d.getElementById(id)) return;js = d.createElement(s); js.id = id;js.src = \"http://connect.facebook.net/en_US/all.js#xfbml=1&appId="+API_KEY+ "\";fjs.parentNode.insertBefore(js, fjs);}(document, 'script', 'facebook-jssdk'));</script><div class=\"fb-comments\" data-href=\""
                        +url+"\" data-width=\"470\"></div> </body></html>", "text/html", null, null);

        //webView.loadUrl(url);
    }

    @Override
    public void onClick(View view) {
        if(view==backButton){
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }else {
            super.onBackPressed();

        }
    }
}
