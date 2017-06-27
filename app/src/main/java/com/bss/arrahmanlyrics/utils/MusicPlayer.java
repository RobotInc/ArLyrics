package com.bss.arrahmanlyrics.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.hardware.camera2.TotalCaptureResult;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
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

public class MusicPlayer implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, AudioManager.OnAudioFocusChangeListener {

	MediaPlayer player;
	HashMap<String, String> list;
	Context context;
	String currentPlayingSong;
	SeekBar bar;
	TextView totalDur;
	TelephonyManager tm;
	AudioManager audioManager;
	PhoneStateListener phoneStateListener;
	ProgressDialog dialog;
	int resumePosition;
	private boolean ongoingCall = false;


	public MusicPlayer(Context context) {
		this.context = context;

		player = new MediaPlayer();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);

		player.setOnCompletionListener(this);
		player.setOnErrorListener(this);
		player.setOnPreparedListener(this);
		player.setOnBufferingUpdateListener(this);

		callStateListener();
		requestAudioFocus();

	}

	private boolean requestAudioFocus() {
		audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
		if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			//Focus gained
			return true;
		}
		//Could not gain focus
		return false;
	}

	private boolean removeAudioFocus() {
		return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
				audioManager.abandonAudioFocus(this);
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		double ratio = percent / 100.0;
		int bufferingLevel = (int) (mp.getDuration() * ratio);

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
		dialog.hide();

	}

	public void setPlayList(HashMap<String, String> list) {
		this.list = list;
	}

	public void setPlay(String name, SeekBar bar, TextView totalDur,Context presetContext) {
		this.bar = bar;
		this.totalDur = totalDur;
		try {
			player.reset();
			player.setDataSource(String.valueOf(list.get(name)));
			currentPlayingSong = name;
			dialog = new ProgressDialog(presetContext);
			dialog.setMessage("Loading Song From database...");
			dialog.show();
			player.prepareAsync();
		} catch (IOException e) {
			Log.e("error source", String.valueOf(list.get(name)));
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
			resumePosition = player.getCurrentPosition();
		}

	}

	public void next() {

	}

	public void previous() {

	}

	public void resume() {
		if (!player.isPlaying()) {
			player.seekTo(resumePosition);
			player.start();
		}
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

	public boolean isPlaying() {
		return player.isPlaying();

	}

	public void stop() {
		player.stop();

	}


	@Override
	public void onAudioFocusChange(int focusChange) {

        //Invoked when the audio focus of the system is updated.
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (!player.isPlaying()) player.start();
                player.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (player.isPlaying()) player.stop();
                player.release();
                player = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (player.isPlaying()) player.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (player.isPlaying()) player.setVolume(0.1f, 0.1f);
                break;
        }

	}

	private void callStateListener() {
		// Get the telephony manager
		tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		//Starting listening for PhoneState changes
		phoneStateListener = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				switch (state) {
					//if at least one call exists or the phone is ringing
					//pause the MediaPlayer
					case TelephonyManager.CALL_STATE_OFFHOOK:
					case TelephonyManager.CALL_STATE_RINGING:
						if (player != null) {
							pause();
							ongoingCall = true;
						}
						break;
					case TelephonyManager.CALL_STATE_IDLE:
						// Phone idle. Start playing.
						if (player != null) {
							if (ongoingCall) {
								ongoingCall = false;
								resume();
							}
						}
						break;
				}
			}
		};
		// Register the listener with the telephony manager
		// Listen for changes to the device call state.
		tm.listen(phoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);
	}

	public void stopListener(){
		 removeAudioFocus();
        //Disable the PhoneStateListener
        if (phoneStateListener != null) {
            tm.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
	}

}
