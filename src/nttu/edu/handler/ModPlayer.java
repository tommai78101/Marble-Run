package nttu.edu.handler;

import nttu.edu.R;
import nttu.edu.activities.MenuActivity;
import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.peculiargames.andmodplug.MODResourcePlayer;

public class ModPlayer {
	private MODResourcePlayer player = null;
	private boolean stopFlag;
	private int currentMOD;
	private final int[] mods = {R.raw.marblerun};
	private final Activity activity;
	
	public ModPlayer(Activity a) {
		currentMOD = 0;
		activity = a;
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(a);
		toggle(pref.getBoolean("musicCheckBox", true));
	}
	
	public void onPause() {
		
	}
	
	public void onResume() {
	}
	
	public void nextMOD() {
		currentMOD++;
		if (currentMOD >= mods.length)
			currentMOD = 0;
		player.PausePlay();
		player.LoadMODResource(mods[currentMOD]);
		player.UnPausePlay();
	}
	
	public static void toggle(boolean value) {
		ModPlayer modplug = MenuActivity.player;
		if (value) {
			if (modplug == null)
				return;
			else {
				if (modplug.player == null) {
					modplug.player = new MODResourcePlayer(modplug.activity);
					modplug.player.LoadMODResource(modplug.mods[modplug.currentMOD]);
					modplug.player.setVolume(0.5f);
					modplug.player.setPatternLoopRange(0, 5, MODResourcePlayer.PATTERN_CHANGE_AFTER_GROUP);
					modplug.player.start();
					Toast.makeText(modplug.activity, "Ode to Tracker, by SaxxonPike", Toast.LENGTH_LONG).show();
				}
			}
		}
		else {
			if (modplug == null)
				return;
			else {
				if (modplug.player != null) {
					modplug.player.StopAndClose();
					modplug.player = null;
				}
			}
		}
	}
}
