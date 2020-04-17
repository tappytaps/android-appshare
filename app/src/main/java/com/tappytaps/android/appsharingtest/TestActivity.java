package com.tappytaps.android.appsharingtest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tappytaps.android.appsharing.ShareAppFragment;


public class TestActivity extends AppCompatActivity implements ShareAppFragment.Listener {

    private static final String TAG = "TestActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Button testButton = findViewById(R.id.button);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ShareAppFragment.Builder()
                        .setSimpleMessage("Simple message saying something very clever {url}")
                        .setEmailSubject("Something to share")
                        .setFacebookQuote("At the link below you can find... {url}")
                        .setTwitterMessage("This message also says something clever, shares {url} #and #sum #hashtagz")
                        .setUrl("https://www.babymonitor3g.com")
                        .setQrCodeUrl("https://www.tappytaps.com")
                        .show(getSupportFragmentManager());
            }
        });
    }

    @Override
    public void onShareAction(String using) {
        Log.d(TAG, "Share action using: " + using);
    }

    @Override
    public void onSharingDismissed() {
        Log.d(TAG, "Sharing dismissed");
    }
}
