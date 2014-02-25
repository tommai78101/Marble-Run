package nttu.edu.activities;

import nttu.edu.R;
import nttu.edu.graphics.Art;
import nttu.edu.handler.ModPlayer;
import nttu.edu.sound.Sound;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

public class MenuActivity extends Activity implements View.OnClickListener {
	//public static MusicHandler music;
	public static ModPlayer player;
	
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Window window = this.getWindow();
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		window.addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
		this.setContentView(R.layout.menu);
		load();
		Button play = (Button) this.findViewById(R.id.playButton);
		Button score = (Button) this.findViewById(R.id.scoreButton);
		Button quit = (Button) this.findViewById(R.id.quitButton);
		Button settings = (Button) this.findViewById(R.id.settingsButton);
		play.setOnClickListener(this);
		score.setOnClickListener(this);
		quit.setOnClickListener(this);
		settings.setOnClickListener(this);
	}
	
	private void load() {
		AssetManager manager = this.getAssets();
		Art.sprites = Art.loadBitmap(manager, "art/sprites.png");
		Art.animatedHole = Art.loadBitmap(manager, "art/goal.png");
		Art.coin = Art.loadBitmap(manager, "art/coin.png");
		Art.gameOver = Art.loadBitmap(manager, "art/gameover.png");
		Art.gameOver = Bitmap.createScaledBitmap(Art.gameOver, 232, 32, false);
		Art.compass = Art.loadBitmap(manager, "art/compass.png");
		Art.hud = Art.loadBitmap(manager, "art/hud.png");
		Art.hudMenu = Art.loadBitmap(manager, "art/hudmenu.png");
		Sound.emergencyLoad(this, manager);
		PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, false);
		if (SettingsActivity.sharedPreferences == null)
			SettingsActivity.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		//music = new MusicHandler(this.getApplicationContext());
		player = new ModPlayer(this);
	}
	
	@Override
	protected void onPause() {
		//music.onPause();
		player.onPause();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		//music.onResume();
		player.onResume();
		super.onPause();
	}
	
	@Override
	public void finish() {
		//music.onFinish();
		player.onPause();
		super.finish();
	}
	
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
			case R.id.playButton:
				intent = new Intent(MenuActivity.this, LevelSelectionActivity.class);
				MenuActivity.this.startActivity(intent);
				break;
			case R.id.scoreButton:
				intent = new Intent(MenuActivity.this, ScoreActivity.class);
				MenuActivity.this.startActivity(intent);
				break;
			case R.id.quitButton:
				MenuActivity.this.finish();
				android.os.Process.killProcess(android.os.Process.myPid());
				break;
			case R.id.settingsButton:
				intent = new Intent(MenuActivity.this, SettingsActivity.class);
				MenuActivity.this.startActivity(intent);
				break;
		}
	}
}
