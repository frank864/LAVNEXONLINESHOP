package Admins;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import com.example.lavnexonlineshop.R;

public class AdminWhatAppActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_what_app);


        webView = findViewById(R.id.webview);
        webView.loadUrl("  https://chat.whatsapp.com/B97Hz3VcHjPFv7Vgl1AGDV");

        webView.getSettings().setJavaScriptEnabled(true);

    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        }else{
            super.onBackPressed();
        }
    }
}