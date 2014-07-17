package com.example.musicplayer;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class MainActivity extends Activity {
    private GridView mGvMain;
    private GridViewItemAdapter mAdapter;

    private final static String[] MENU_ITEM_TEXT = new String[]{"全部音乐", "歌手", "专辑"};
    private final static int[] MENU_ITEM_DATA_COUNT = new int[3];
    private final static int[] MENU_ITEM_ICON = new int[] { R.drawable.icon_music, R.drawable.icon_artist, R.drawable.icon_album };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mGvMain = (GridView)findViewById(R.id.gv_main);
        mAdapter = new GridViewItemAdapter();
        mGvMain.setAdapter(mAdapter);
    }

    class GridViewItemAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return MENU_ITEM_TEXT.length;
        }

        @Override
        public Object getItem(int position) {
            return MENU_ITEM_TEXT[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.gv_main_item, null);

                convertView.setTag(convertView);
            }

            TextView tvMenuItem = (TextView)convertView;
            BitmapDrawable bitmapDrawable = (BitmapDrawable)getResources().getDrawable(MENU_ITEM_ICON[position]);
            bitmapDrawable.setBounds(0, 0, bitmapDrawable.getIntrinsicWidth(), bitmapDrawable.getIntrinsicHeight());
            tvMenuItem.setCompoundDrawables(null, bitmapDrawable, null, null);
            tvMenuItem.setText(MENU_ITEM_TEXT[position] + "(" + MENU_ITEM_DATA_COUNT[position] + ")");

            return convertView;
        }
    }
}
