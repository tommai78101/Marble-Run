package nttu.edu.sound;

import java.io.IOException;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.preference.PreferenceManager;

public class Sound {
	public static int jumpID;
	public static int coinID;
	private static int id;
	
	public static SoundPool pool;
	public static Context applicationContext = null;
	
	public static int loadSound(Activity a, AssetManager m, String filename) {
		if (pool == null)
			pool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		if (pool == null)
			throw new RuntimeException("SoundPool failed to load. [Sound.java, line 14]");
		if (applicationContext == null)
			applicationContext = a.getApplicationContext();
		int id = 0;
		try {
			id = pool.load(m.openFd(filename), 1);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		if (id == 0)
			throw new RuntimeException("Sound file not loaded properly.");
		return id;
	}
	
	public static void play(int i) {
		if (pool == null)
			pool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		else {
			id = i;
			new Thread(new Runnable() {
				public void run() {
					SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
					boolean soundFlag = preferences.getBoolean("soundCheckBox", true);
					if (soundFlag) {
						if (pool.play(id, 0.05f, 0.05f, 0, 0, 1f) == 0)
							throw new RuntimeException("Failed to play sound file, ID: " + id);
					}
				}
			}).start();
		}
	}
	
	public static void emergencyLoad(Activity a, AssetManager m) {
		if (pool != null) {
			pool.unload(Sound.jumpID);
			pool.unload(Sound.coinID);
		}
		Sound.jumpID = Sound.loadSound(a, m, "sounds/jump.wav");
		Sound.coinID = Sound.loadSound(a, m, "sounds/coin.wav");
	}
}
