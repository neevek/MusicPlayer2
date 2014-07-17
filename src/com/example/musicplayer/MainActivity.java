package com.example.musicplayer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button btnOpenDialer = (Button)findViewById(R.id.btn_open_dialer);
        btnOpenDialer.setOnClickListener(this);

        Button btnOpenBrowser = (Button)findViewById(R.id.btn_open_browser);
        btnOpenBrowser.setOnClickListener(this);

        Button btnOpenNextActivity = (Button)findViewById(R.id.btn_open_next_activity);
        btnOpenNextActivity.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open_dialer: {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:02066666666"));
                startActivity(intent);
                break;
            }

            case R.id.btn_open_browser: {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.google.com"));
                startActivity(intent);
                break;
            }

            case R.id.btn_open_next_activity: {
                startActivity(new Intent(this, NextActivity.class));
                break;
            }
        }
    }
}
