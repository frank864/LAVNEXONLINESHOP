package com.example.lavnexonlineshop.Buyers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import com.example.lavnexonlineshop.R;

public class UserWhatActivity extends AppCompatActivity {
    private WebView webViewUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_what);

        webViewUsers = findViewById(R.id.webviewUsers);
        webViewUsers.loadUrl("https://chat.whatsapp.com/DVu4jbzFSbzKEApS62B2bo ");

        webViewUsers.getSettings().setJavaScriptEnabled(true);

    }

    @Override
    public void onBackPressed() {
        if (webViewUsers.canGoBack()){
            webViewUsers.goBack();
        }else{
            super.onBackPressed();
        }
    }
}
