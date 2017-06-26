package com.bss.arrahmanlyrics.activites;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bss.arrahmanlyrics.R;
import com.bss.arrahmanlyrics.adapter.songAdapter;
import com.bss.arrahmanlyrics.models.Song;
import com.bss.arrahmanlyrics.utils.BlurImage;
import com.bss.arrahmanlyrics.utils.CustomLayoutManager;

import com.bss.arrahmanlyrics.utils.DividerItemDecoration;
import com.bss.arrahmanlyrics.utils.FirstLetterUpperCase;
import com.bss.arrahmanlyrics.utils.RecyclerItemClickListener;
import com.bss.arrahmanlyrics.utils.SimpleDividerItemDecoration;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class albumSongList extends AppCompatActivity {
    private ImageView artworkView;
    private Song song;
    private List<Song> songlist;
    private songAdapter songListAdapter;
    private FastScrollRecyclerView rv;
    private Toolbar toolbar;
    HashMap<String, Object> values;
    FloatingActionButton shuffleButton;
    FloatingActionButton playPauseButton;
    DatabaseReference ref;
    TextView songTitle, songLyricist;
    ImageView backgroundArt;
    String imageString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_song_list);
        Log.i("Movie Title",getIntent().getExtras().getString("Title"));
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(FirstLetterUpperCase.convert(getIntent().getExtras().getString("Title")));
        backgroundArt = (ImageView) findViewById(R.id.BackgroundArt);
        backgroundArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("Title",getIntent().getExtras().getString("Title"));
                bundle.putString("SongTitle","");
                bundle.putString("lyricist","");
                bundle.putString("trackNo","");
                Intent i = new Intent(getApplicationContext(), lyricsActivity.class);


                i.putExtras(bundle);
                startActivity(i);
            }
        });
        songlist = new ArrayList<>();
        artworkView = (ImageView) findViewById(R.id.album_artwork);
        rv = (FastScrollRecyclerView) findViewById(R.id.songrv);
        artworkView = (ImageView) findViewById(R.id.album_artwork);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        songListAdapter = new songAdapter(getApplicationContext(), songlist);
        rv.setAdapter(songListAdapter);
        CustomLayoutManager customLayoutManager = new CustomLayoutManager(getApplicationContext());
        customLayoutManager.setSmoothScrollbarEnabled(true);
        rv.setLayoutManager(customLayoutManager);
        rv.addItemDecoration(new DividerItemDecoration(getApplicationContext(),75,false));
        rv.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Song song = songlist.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("Title",getIntent().getExtras().getString("Title"));
                bundle.putString("SongTitle",song.getSongName());
                bundle.putString("lyricist",song.getLyricistNames());
                bundle.putString("trackNo",song.getTrackNo());
                Toast.makeText(getApplicationContext(),getIntent().getExtras().getString("Title"),Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), lyricsActivity.class);


                i.putExtras(bundle);
                startActivity(i);





            }
        }));
        songTitle = (TextView) findViewById(R.id.song_title);
        songLyricist = (TextView) findViewById(R.id.song_lyricist);
        final Typeface title = Typeface.createFromAsset(getApplicationContext().getAssets(), "MavenPro.ttf");
        final Typeface lyricist = Typeface.createFromAsset(getApplicationContext().getAssets(), "MavenPro.ttf");
        songTitle.setTypeface(title);
        songLyricist.setTypeface(lyricist);

        shuffleButton = (FloatingActionButton) findViewById(R.id.shuffle_fab);
        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        playPauseButton = (FloatingActionButton) findViewById(R.id.Play_pause);
        ref = FirebaseDatabase.getInstance().getReference();

        ref.child("AR Rahman").child("Tamil").child(getIntent().getExtras().getString("Title")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                values = (HashMap<String, Object>) dataSnapshot.getValue();

                prepareSongList();
                songListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void prepareSongList() {
        songlist.clear();
        List<Song> list = new ArrayList<>();
        SortedSet<String> trackNos = new TreeSet<>();
        for (String songs : values.keySet()) {
            if (!songs.equals("IMAGE")) {
                HashMap<String, Object> oneSong = (HashMap<String, Object>) values.get(songs);
                Log.i("Track NO", String.valueOf(oneSong.get("Track NO")));

                Song newSong = new Song(songs, oneSong.get("Track NO").toString(), oneSong.get("Lyricist").toString());
                list.add(newSong);
                trackNos.add((String.valueOf(oneSong.get("Track NO"))));
            } else if (songs.equals("IMAGE")) {
                String imageString = String.valueOf(values.get(songs));
                Log.i("image string", imageString);
                try {
                    Glide.with(getApplicationContext()).load(getImage(imageString)).into(artworkView);
                    Bitmap image = BlurImage.blur(getApplicationContext(), getBitmap(imageString));

                    backgroundArt.setImageBitmap(image);

                } catch (Exception e) {
                    Log.i("image error", e.toString());
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
            Log.i("songLIst size", String.valueOf(songlist.size()));
        }
        for (String Track : trackNos) {
            for (Song songNo : list) {
                if (songNo.getTrackNo().equals(Track)) {
                    songlist.add(songNo);
                }
            }

        }
        setPalettes();


    }


    private byte[] getImage(String imageString) {
        if (imageString.equals(null)) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bitMapData = stream.toByteArray();
            return bitMapData;

        }
        byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
        //Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedString;
    }

    public void setPalettes() {

        try {
            Palette.from(getBitmap(String.valueOf(values.get("IMAGE")))).maximumColorCount(160000000).generate(new Palette.PaletteAsyncListener() {

                @Override
                public void onGenerated(Palette palette) {

                    // Get the "vibrant" color swatch based on the bitmap
                    if (palette.getVibrantSwatch() != null) {
                        toolbar.setBackgroundColor(palette.getVibrantSwatch().getRgb());
                        toolbar.setTitleTextColor(palette.getVibrantSwatch().getTitleTextColor());
                        songTitle.setTextColor(palette.getVibrantSwatch().getTitleTextColor());
                        songLyricist.setTextColor(palette.getVibrantSwatch().getTitleTextColor());
                        shuffleButton.setBackgroundTintList(ColorStateList.valueOf(palette.getVibrantSwatch().getRgb()));
                        playPauseButton.setBackgroundTintList(ColorStateList.valueOf(palette.getVibrantSwatch().getRgb()));
                        //holder.count.setTextColor(palette.getVibrantSwatch().getBodyTextColor());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().setStatusBarColor(palette.getVibrantSwatch().getRgb());
                        }
                    } else if (palette.getLightVibrantSwatch() != null) {
                        toolbar.setBackgroundColor(palette.getLightVibrantSwatch().getRgb());
                        toolbar.setTitleTextColor(palette.getLightVibrantSwatch().getTitleTextColor());
                        songTitle.setTextColor(palette.getLightVibrantSwatch().getTitleTextColor());
                        songLyricist.setTextColor(palette.getLightVibrantSwatch().getTitleTextColor());
                        shuffleButton.setBackgroundTintList(ColorStateList.valueOf(palette.getLightVibrantSwatch().getRgb()));
                        playPauseButton.setBackgroundTintList(ColorStateList.valueOf(palette.getLightVibrantSwatch().getRgb()));
                        //holder.count.setTextColor(palette.getMutedSwatch().getBodyTextColor());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().setStatusBarColor(palette.getLightVibrantSwatch().getRgb());
                        }
                    } else if (palette.getDarkVibrantSwatch() != null) {
                        toolbar.setBackgroundColor(palette.getDarkVibrantSwatch().getRgb());

                        toolbar.setTitleTextColor(palette.getDarkVibrantSwatch().getTitleTextColor());
                        songTitle.setTextColor(palette.getDarkVibrantSwatch().getTitleTextColor());
                        songLyricist.setTextColor(palette.getDarkVibrantSwatch().getTitleTextColor());
                        shuffleButton.setBackgroundTintList(ColorStateList.valueOf(palette.getDarkVibrantSwatch().getRgb()));
                        playPauseButton.setBackgroundTintList(ColorStateList.valueOf(palette.getDarkVibrantSwatch().getRgb()));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().setStatusBarColor(palette.getDarkVibrantSwatch().getRgb());
                        }


                    } else if (palette.getLightMutedSwatch() != null) {
                        toolbar.setBackgroundColor(palette.getLightMutedSwatch().getRgb());
                        toolbar.setTitleTextColor(palette.getLightMutedSwatch().getTitleTextColor());
                        songTitle.setTextColor(palette.getLightMutedSwatch().getTitleTextColor());
                        songLyricist.setTextColor(palette.getLightMutedSwatch().getTitleTextColor());
                        shuffleButton.setBackgroundTintList(ColorStateList.valueOf(palette.getLightMutedSwatch().getRgb()));
                        playPauseButton.setBackgroundTintList(ColorStateList.valueOf(palette.getLightMutedSwatch().getRgb()));
                        //holder.count.setTextColor(palette.getMutedSwatch().getBodyTextColor());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().setStatusBarColor(palette.getLightMutedSwatch().getRgb());
                        }
                    } else if (palette.getDarkMutedSwatch() != null) {
                        toolbar.setBackgroundColor(palette.getDarkMutedSwatch().getRgb());
                        toolbar.setTitleTextColor(palette.getDarkMutedSwatch().getTitleTextColor());
                        songTitle.setTextColor(palette.getDarkMutedSwatch().getTitleTextColor());
                        songLyricist.setTextColor(palette.getDarkMutedSwatch().getTitleTextColor());
                        shuffleButton.setBackgroundTintList(ColorStateList.valueOf(palette.getDarkMutedSwatch().getRgb()));
                        playPauseButton.setBackgroundTintList(ColorStateList.valueOf(palette.getDarkMutedSwatch().getRgb()));
                        //holder.count.setTextColor(palette.getDarkMutedSwatch().getBodyTextColor());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().setStatusBarColor(palette.getDarkMutedSwatch().getRgb());
                        }
                    } else if (palette.getDominantSwatch() != null) {
                        toolbar.setBackgroundColor(palette.getDominantSwatch().getRgb());
                        toolbar.setTitleTextColor(palette.getDominantSwatch().getTitleTextColor());
                        songTitle.setTextColor(palette.getDominantSwatch().getTitleTextColor());
                        songLyricist.setTextColor(palette.getDominantSwatch().getTitleTextColor());
                        shuffleButton.setBackgroundTintList(ColorStateList.valueOf(palette.getDominantSwatch().getRgb()));
                        playPauseButton.setBackgroundTintList(ColorStateList.valueOf(palette.getDominantSwatch().getRgb()));
                        //holder.count.setTextColor(palette.getDominantSwatch().getBodyTextColor());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().setStatusBarColor(palette.getDominantSwatch().getRgb());
                        }
                    } else if (palette.getMutedSwatch() != null) {
                        toolbar.setBackgroundColor(palette.getMutedSwatch().getRgb());
                        toolbar.setTitleTextColor(palette.getMutedSwatch().getTitleTextColor());
                        songTitle.setTextColor(palette.getMutedSwatch().getTitleTextColor());
                        songLyricist.setTextColor(palette.getMutedSwatch().getTitleTextColor());
                        shuffleButton.setBackgroundTintList(ColorStateList.valueOf(palette.getMutedSwatch().getRgb()));
                        playPauseButton.setBackgroundTintList(ColorStateList.valueOf(palette.getMutedSwatch().getRgb()));
                        //holder.count.setTextColor(palette.getMutedSwatch().getBodyTextColor());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().setStatusBarColor(palette.getMutedSwatch().getRgb());
                        }
                    }


                }
            });
        } catch (Exception e) {
            Log.i("exception:", e.getMessage());
        }
    }

    public Bitmap getBitmap(String imageString) {
        if (imageString.equals(null)) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            return bitmap;
        }
        byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return bitmap;
    }
}
