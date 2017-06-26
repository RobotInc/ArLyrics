package com.bss.arrahmanlyrics.utils;

import android.content.Context;
import android.hardware.camera2.TotalCaptureResult;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bss.arrahmanlyrics.mainApp;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by mohan on 6/26/17.
 */

public class MusicPlayer implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

	MediaPlayer player;
	HashMap<String, String> list;
	Context context;
	String currentPlayingSong;
	SeekBar bar;
	TextView totalDur;

	public MusicPlayer(Context context) {
		this.context = context;

		player = new MediaPlayer();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setOnCompletionListener(this);
		player.setOnErrorListener(this);
		player.setOnPreparedListener(this);
		player.setOnBufferingUpdateListener(this);

	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		double ratio = percent / 100.0;
		int bufferingLevel = (int)(mp.getDuration() * ratio);

		bar.setSecondaryProgress(bufferingLevel);
	}

	@Override
	public void onCompletion(MediaPlayer mp) {

	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		return false;
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		play();

	}

	public void setPlayList(HashMap<String, String> list){
		this.list = list;
	}

	public void setPlay(String name, SeekBar bar, TextView totalDur) {
		this.bar = bar;
		this.totalDur = totalDur;
		try {
			player.reset();
			player.setDataSource(String.valueOf(list.get(name)));
			currentPlayingSong = name;
			player.prepareAsync();
		} catch (IOException e) {
			Log.e("error source",String.valueOf(list.get(name)));
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	public void play() {
		if (player.getDuration() > 0) {
			player.start();
			bar.setMax((int) player.getDuration());

			bar.setProgress(getCurrentPosition());
			totalDur.setText(String.format("%d : %d ",
					TimeUnit.MILLISECONDS.toMinutes((long) player.getDuration()),
					TimeUnit.MILLISECONDS.toSeconds((long) player.getDuration()) -
							TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
									player.getDuration())))
			);


		} else {
			Toast.makeText(context, "Nothing to play", Toast.LENGTH_SHORT).show();
		}

	}

	public void pause() {
		if (player.isPlaying()) {
			player.pause();
		}
	}

	public void next() {

	}

	public void previous() {

	}

	public void shuffle() {

	}

	public int getCurrentPosition() {
		return player.getCurrentPosition();
	}

	public int getDuration() {
		return player.getDuration();
	}

	public void seekTo(int progress) {
		player.seekTo(progress);
	}

	public boolean isPlaying(){
		return player.isPlaying();

	}
	public void stop(){
		player.stop();

	}
}
