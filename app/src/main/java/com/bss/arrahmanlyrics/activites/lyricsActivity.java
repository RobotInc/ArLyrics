package com.bss.arrahmanlyrics.activites;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bss.arrahmanlyrics.Fragments.EnglishLyrics;
import com.bss.arrahmanlyrics.Fragments.OtherLyrics;
import com.bss.arrahmanlyrics.Fragments.songList;
import com.bss.arrahmanlyrics.R;
import com.bss.arrahmanlyrics.mainApp;
import com.bss.arrahmanlyrics.models.Song;
import com.bss.arrahmanlyrics.models.slideSong;
import com.bss.arrahmanlyrics.utils.ArtworkUtils;
import com.bss.arrahmanlyrics.utils.MusicPlayer;
import com.bss.arrahmanlyrics.utils.PlayPauseView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class lyricsActivity extends AppCompatActivity implements ImageView.OnClickListener {
	DatabaseReference imageRef;
	DatabaseReference songRef;
	ViewPager lyricsPager;
	List<String> songList;
	HashMap<String, Object> values;
	HashMap<String, String> links;
	SectionsPagerAdapter section;
	private ImageView play, next, prev, shuffle;
	private SeekBar bar;
	TextView currentDur, totalDur;
	//MusicPlayer mainApp.getPlayer();

	private Handler myHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lyrics2);
		overridePendingTransition(R.anim.slide_in_up, R.anim.fade_back);
		getWindow().setStatusBarColor(Color.parseColor("#FF9F721E"));
		links = new HashMap<>();
		lyricsPager = (ViewPager) findViewById(R.id.lyricsPager);
		//slidingpanel = (SlidingPaneLayout) findViewById(R.id.slidingpanelayout);
		//songlistView = (RecyclerView) findViewById(R.id.fastsonglist);
		section = new SectionsPagerAdapter(getSupportFragmentManager());
		section.addFragment(new songList(), "Song List");
		section.addFragment(new EnglishLyrics(), "English Lyrics");
		section.addFragment(new OtherLyrics(), "Other Lyrics");
		songList = new ArrayList<>();
		values = new HashMap<>();
		lyricsPager.setAdapter(section);
		lyricsPager.setCurrentItem(1);
		//if (!slidingpanel.isOpen()) {
		//     slidingpanel.closePane();
		//  } else {
		//      slidingpanel.openPane();
		//  }

		play = (ImageView) findViewById(R.id.playPause);
		next = (ImageView) findViewById(R.id.forward);
		prev = (ImageView) findViewById(R.id.backward);
		shuffle = (ImageView) findViewById(R.id.shuffle_song);
		play.setOnClickListener(this);
		next.setOnClickListener(this);
		prev.setOnClickListener(this);
		shuffle.setOnClickListener(this);
		bar = (SeekBar) findViewById(R.id.custombar);
		bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					mainApp.getPlayer().seekTo(progress);
				}

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});
		currentDur = (TextView) findViewById(R.id.currentDur);
		totalDur = (TextView) findViewById(R.id.totalDur);


		//songListAdapter = new SlideSongAdapter(getApplicationContext(), songlist);
		//songlistView.setAdapter(songListAdapter);
		//CustomLayoutManager customLayoutManager = new CustomLayoutManager(getApplicationContext());
		//customLayoutManager.setSmoothScrollbarEnabled(true);
		// songlistView.setLayoutManager(customLayoutManager);
		// songlistView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
		imageRef = FirebaseDatabase.getInstance().getReference();
		imageRef.child("AR Rahman").child("Tamil").child(getIntent().getExtras().getString("Title")).child("IMAGE").addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				String imageString = String.valueOf(dataSnapshot.getValue());


				setBackground(imageString);


			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
			}
		});
		songRef = FirebaseDatabase.getInstance().getReference();
		songRef.child("AR Rahman").child("Tamil").child(getIntent().getExtras().getString("Title")).addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				values = (HashMap<String, Object>) dataSnapshot.getValue();

				preparePlaylist();
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});


	}

	private void preparePlaylist() {
		songList.clear();
		for (String songs : values.keySet()) {
			if (!songs.equals("IMAGE")) {
				HashMap<String, Object> oneSong = (HashMap<String, Object>) values.get(songs);
				songList.add(songs);
				Log.i("data source",String.valueOf(oneSong.get("Download")));
				links.put(songs, String.valueOf(oneSong.get("Download")));
			}

		}

		mainApp.getPlayer().setPlayList(links);
		mainApp.getPlayer().setPlay(getIntent().getExtras().getString("SongTitle"),bar,totalDur);
		play.setImageResource(R.drawable.ic_action_pause);
		Log.e("duration",String.valueOf(mainApp.getPlayer().getDuration()));
		bar.setMax((int) mainApp.getPlayer().getDuration());
		bar.setProgress(mainApp.getPlayer().getCurrentPosition());
		totalDur.setText(String.format("%d : %d ",
				TimeUnit.MILLISECONDS.toMinutes((long) mainApp.getPlayer().getDuration()),
				TimeUnit.MILLISECONDS.toSeconds((long) mainApp.getPlayer().getDuration()) -
						TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
								mainApp.getPlayer().getDuration())))
		);

		myHandler.postDelayed(UpdateSongTime, 100);
	}


	/*   private void setLyrics() {
		   final StringBuilder builderEnglish = new StringBuilder();
		   builderEnglish.append(selectedSong.get("English"));
		   builderEnglish.append(selectedSong.get("EnglishOne"));
		   final StringBuilder builderOther = new StringBuilder();
		   builderOther.append(selectedSong.get("Others"));
		   builderOther.append(selectedSong.get("OthersOne"));

		   Typeface english = Typeface.createFromAsset(getAssets(),"english.ttf");
		   title.setText(getIntent().getExtras().getString("SongTitle"));


		   lyricist.setText(getIntent().getExtras().getString("lyricist"));


		   lyricsText.setText(String.valueOf(builderOther));
		   lyricsText.setTypeface(english);
	   }
   */
	@Override
	protected void onPause() {
		overridePendingTransition(R.anim.fade_forward, R.anim.slide_out_down);
		super.onPause();


	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(mainApp.getPlayer().isPlaying()){
			mainApp.getPlayer().stop();
		}
		overridePendingTransition(R.anim.slide_in_up, R.anim.fade_forward);

	}

	public void setBackground(String imageString) {
		try {
			ImageView cover = (ImageView) findViewById(R.id.blurArtwork);
			Bitmap bitmap = getBitmap(imageString);
			ArtworkUtils.blurPreferances(getApplicationContext(), bitmap, cover);
			//setPalettes(blured);
		} catch (Exception e) {
			Log.e("error", e.getMessage());
		}


	}

	/*   public void setPalettes(Bitmap bitmap) {

		   try {
			   Palette.from(bitmap).maximumColorCount(160000000).generate(new Palette.PaletteAsyncListener() {

				   @Override
				   public void onGenerated(Palette palette) {
					   LinearLayout layout = (LinearLayout) findViewById(R.id.dragPanel);
					   playPauseView = (PlayPauseView) findViewById(R.id.btn_play);
					   // Get the "vibrant" color swatch based on the bitmap
						if (palette.getDarkVibrantSwatch() != null) {
						   layout.setBackgroundColor(palette.getDarkVibrantSwatch().getRgb());
							getWindow().setStatusBarColor(palette.getDarkVibrantSwatch().getRgb());
							marqueeTextView.setTextColor(ColorStateList.valueOf(palette.getDarkVibrantSwatch().getBodyTextColor()));
						   playPauseView.setBackgroundTintList(ColorStateList.valueOf(palette.getDarkVibrantSwatch().getTitleTextColor()));


					   } else if (palette.getDarkMutedSwatch() != null) {
							layout.setBackgroundColor(palette.getDarkMutedSwatch().getRgb());
							getWindow().setStatusBarColor(palette.getDarkMutedSwatch().getRgb());
							marqueeTextView.setTextColor(ColorStateList.valueOf(palette.getDarkMutedSwatch().getBodyTextColor()));
							playPauseView.setBackgroundTintList(ColorStateList.valueOf(palette.getDarkMutedSwatch().getTitleTextColor()));
					   } else if (palette.getDominantSwatch() != null) {
							layout.setBackgroundColor(palette.getDominantSwatch().getRgb());
							getWindow().setStatusBarColor(palette.getDominantSwatch().getRgb());
							marqueeTextView.setTextColor(ColorStateList.valueOf(palette.getDominantSwatch().getBodyTextColor()));
							playPauseView.setBackgroundTintList(ColorStateList.valueOf(palette.getDominantSwatch().getTitleTextColor()));
					   } else if (palette.getMutedSwatch() != null) {
							layout.setBackgroundColor(palette.getMutedSwatch().getRgb());
							getWindow().setStatusBarColor(palette.getMutedSwatch().getRgb());
							marqueeTextView.setTextColor(ColorStateList.valueOf(palette.getMutedSwatch().getBodyTextColor()));
							playPauseView.setBackgroundTintList(ColorStateList.valueOf(palette.getMutedSwatch().getTitleTextColor()));
					   }


				   }
			   });
		   } catch (Exception e) {
			   Log.i("exception:", e.getMessage());
		   }
	   }*/


	public Bitmap getBitmap(String imageString) {
		if (imageString.equals(null)) {
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
			return bitmap;
		}
		byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
		Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
		return bitmap;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.playPause: {
				break;
			}
			case R.id.backward: {
				break;
			}
			case R.id.forward: {
				break;
			}
			case R.id.shuffle_song: {
				break;
			}
		}
	}


	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		private final List<Fragment> mFragmentList = new ArrayList<>();
		private final List<String> mFragmentTitleList = new ArrayList<>();

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return mFragmentList.get(position);
		}

		@Override
		public int getCount() {
			return mFragmentList.size();
		}

		public void addFragment(Fragment fragment, String title) {
			mFragmentList.add(fragment);
			mFragmentTitleList.add(title);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mFragmentTitleList.get(position);
		}
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

	private Runnable UpdateSongTime = new Runnable() {
		public void run() {
			if (mainApp.getPlayer().isPlaying()) {
				int startTime = mainApp.getPlayer().getCurrentPosition();
				currentDur.setText(String.format("%d : %d",
						TimeUnit.MILLISECONDS.toMinutes((long) startTime),
						TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
								TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
										toMinutes((long) startTime)))
				);
				if (startTime == mainApp.getPlayer().getDuration()) {
					bar.setProgress(0);
				} else {
					bar.setProgress(startTime);
				}
			}


			myHandler.postDelayed(this, 100);

		}
	};



}
