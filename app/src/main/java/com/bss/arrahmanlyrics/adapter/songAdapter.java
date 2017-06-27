package com.bss.arrahmanlyrics.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bss.arrahmanlyrics.R;
import com.bss.arrahmanlyrics.models.Song;
import com.bss.arrahmanlyrics.utils.FirstLetterUpperCase;

import java.util.List;
import java.util.Locale;

/**
 * Created by mohan on 5/20/17.
 */

public class songAdapter extends RecyclerView.Adapter<songAdapter.MyViewHolder> {
    private View.OnClickListener mClickListener;
    private Context mContext;
    private List<Song> songlist;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, lyricist, trackNO;
        ImageView dots;


        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.title);
            lyricist = (TextView) view.findViewById(R.id.lyricist);
            dots = (ImageButton) view.findViewById(R.id.dot);
            trackNO = (TextView) view.findViewById(R.id.number);
            //albumCover = (ImageView) view.findViewById(R.id.album_artwork);
        }
    }

    public songAdapter(Context mContext, List<Song> songlist) {
        this.mContext = mContext;
        this.songlist = songlist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detail_list, parent, false);
        RecyclerView.ViewHolder holder = new MyViewHolder(itemView);

        return (MyViewHolder) holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Song actualsong = songlist.get(position);
        Log.i("actual song name", actualsong.getSongName());
        final Typeface title = Typeface.createFromAsset(mContext.getAssets(), "MavenPro.ttf");
        final Typeface lyricist = Typeface.createFromAsset(mContext.getAssets(), "MavenPro.ttf");
        holder.name.setTypeface(title);
        holder.lyricist.setTypeface(lyricist);

        holder.name.setText(FirstLetterUpperCase.convert(actualsong.getSongName()));
        //holder.name.setText(actualsong.getSongName());

        //holder.lyricist.setText(FirstLetterUpperCase.convert("Lyricist : " + actualsong.getLyricistNames()));
        holder.lyricist.setText("Lyricist : " + actualsong.getLyricistNames());

        holder.trackNO.setText(actualsong.getTrackNo());

        holder.dots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.dots);
            }
        });
    }

    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.song_list_item, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_fav:
                    Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_share:
                    Toast.makeText(mContext, "Share", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }


    @Override
    public int getItemCount() {
        return songlist.size();
    }

    public String FirstLetterUpperCase(String source){
        source = source.toLowerCase();

        StringBuffer res = new StringBuffer();

        String[] strArr = source.split(" ");
        for (String str : strArr) {
            char[] stringArray = str.trim().toCharArray();
            stringArray[0] = Character.toUpperCase(stringArray[0]);
            str = new String(stringArray);

            res.append(str).append(" ");
        }

        return res.toString().trim();
    }
}
