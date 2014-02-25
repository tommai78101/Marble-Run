package nttu.edu.activities;

import nttu.edu.R;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
	public static SharedPreferences sharedPreferences;
	
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		sharedPreferences = this.getPreferences(MODE_PRIVATE);
		Window window = this.getWindow();
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		window.addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
		this.addPreferencesFromResource(R.xml.preferences);
		Preference back = this.findPreference("settings_back");
		CheckBoxPreference music = (CheckBoxPreference) this.findPreference("musicCheckBox");
		back.setOnPreferenceClickListener(this);
		music.setOnPreferenceChangeListener(this);
	}
	
	@Override
	protected void onPause() {
		//MenuActivity.music.onPause();
		MenuActivity.player.onPause();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		//MenuActivity.music.onResume();
		MenuActivity.player.onResume();
		super.onResume();
	}
	
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		Log.e("Settings", "Checked....");
		CheckBoxPreference pref = (CheckBoxPreference) preference;
		pref.setChecked(!pref.isChecked());
		//MenuActivity.music.toggle(pref.isChecked());
		MenuActivity.player.toggle(pref.isChecked());
		return true;
	}
	
	public boolean onPreferenceClick(Preference preference) {
		this.finish();
		return false;
	}
	
}
