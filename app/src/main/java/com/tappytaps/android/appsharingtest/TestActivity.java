package com.tappytaps.android.appsharingtest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tappytaps.android.appsharing.ShareAppFragment;


public class TestActivity extends AppCompatActivity {

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
                        .setDescription("Testing")
                        .setUrl("https://www.google.com")
                        .show(getSupportFragmentManager());
            }
        });
    }
}
