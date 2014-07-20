package com.example.musicplayer;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Window;
import com.example.musicplayer.fragment.MainFragment;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // call requestWindowFeature before super.onCreate(); see https://groups.google.com/d/msg/actionbarsherlock/dHIJn1qbkFE/bEzSg2haGZMJ
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setProgressBarIndeterminateVisibility(false);


        showMainFragment();
    }

    private void showMainFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.container, new MainFragment());
        ft.commitAllowingStateLoss();
    }
}
