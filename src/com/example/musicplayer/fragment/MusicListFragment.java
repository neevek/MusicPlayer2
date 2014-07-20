package com.example.musicplayer.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.example.musicplayer.R;

/**
 * Created by neevek on 7/20/14.
 */
public class MusicListFragment extends Fragment implements AdapterView.OnItemClickListener {
    private String mTitle;
    private MusicListItemHandler mMusicListItemHandler;

    public MusicListFragment(String title, MusicListItemHandler fetcher) {
        mTitle = title;
        mMusicListItemHandler = fetcher;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(mTitle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.music_list, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ListView listView = (ListView)view.findViewById(R.id.lv_music_list);
        // don't forget to call this!!!
        listView.setEmptyView(view.findViewById(android.R.id.empty));
        listView.setAdapter(new MusicListAdapter());
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mMusicListItemHandler.onItemClick(position);
    }

    class MusicListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mMusicListItemHandler.getCount();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.music_list_item, null);

                holder = new ViewHolder();
                holder.tvTitle = (TextView)convertView.findViewById(R.id.tv_title);
                holder.tvSubtitle = (TextView)convertView.findViewById(R.id.tv_subtitle);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            holder.tvTitle.setText(mMusicListItemHandler.getItemTitle(position));
            holder.tvSubtitle.setText(mMusicListItemHandler.getItemSubtitle(position));

            return convertView;
        }
    }

    class ViewHolder {
        TextView tvTitle;
        TextView tvSubtitle;
    }

    interface MusicListItemHandler {
        int getCount();
        String getItemTitle(int position);
        String getItemSubtitle(int position);
        void onItemClick(int position);
    }
}
