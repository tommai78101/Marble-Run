package nttu.edu.handler;

import nttu.edu.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.Log;

public class MusicHandler implements MediaPlayer.OnPreparedListener, Runnable {
	private MediaPlayer player = null;
	private boolean stopFlag;
	
	public MusicHandler(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		stopFlag = !preferences.getBoolean("musicCheckBox", true);
		Log.e("Music", "Constructor stopFlag: " + stopFlag);
		player = MediaPlayer.create(context, R.raw.marblerun);
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setVolume(0.5f, 0.5f);
		player.setOnPreparedListener(this);
		player.setLooping(true);
		new Thread(this).start();
	}
	
	public void start() {
		Log.e("Music", "Start().");
	}
	
	public boolean isStopped() {
		return stopFlag;
	}
	
	public void onPause() {
		
	}
	
	public void onResume() {
		
	}
	
	public void onFinish() {
		player.release();
	}
	
	public void onPrepared(MediaPlayer mp) {
	}
	
	public void toggle(boolean state) {
		stopFlag = !state;
		Log.e("Music", "Boolean state:" + state);
		Log.e("Music", "stopFlag:" + stopFlag);
		new Thread(this).start();
	}
	
	public void run() {
		if (stopFlag) {
			Log.e("Music", "Detected stopFlag = true.");
			if (player.isPlaying()) {
				Log.e("Music", "Pause().");
				player.pause();
				Log.e("Music", "Seek to the beginning.");
				player.seekTo(0);
			}
		}
		else {
			Log.e("Music", "Detected stopFlag = false.");
			Log.e("Music", "Start().");
			if (!player.isPlaying()) {
				Log.e("Music", "Detected player is not playing.");
				player.start();
			}
			Log.e("Music", "Music should be playing.");
		}
	}
}
