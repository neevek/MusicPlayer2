package com.example.musicplayer;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Window;
import com.example.musicplayer.fragment.MainFragment;
import com.example.musicplayer.service.MusicPlayerService;

public class MainActivity extends Activity {

    private MusicPlayerService mMusicPlayerService;
    private ServiceConnection mServiceConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // call requestWindowFeature before super.onCreate(); see https://groups.google.com/d/msg/actionbarsherlock/dHIJn1qbkFE/bEzSg2haGZMJ
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setProgressBarIndeterminateVisibility(false);

        bindMusicPlayerService();

        showMainFragment();
    }

    private void bindMusicPlayerService() {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mMusicPlayerService = ((MusicPlayerService.MusicPlayerBinder)service).getMusicPlayerService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mMusicPlayerService = null;
            }
        };

        Intent intent = new Intent(this, MusicPlayerService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private void showMainFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.container, new MainFragment());
        ft.commitAllowingStateLoss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        stopService(new Intent(this, MusicPlayerService.class));
    }
}
