package nttu.edu.activities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import nttu.edu.R;
import nttu.edu.graphics.RenderView;
import nttu.edu.handler.Accelero;
import nttu.edu.handler.Loading;
import nttu.edu.score.Format;
import nttu.edu.score.Score;
import nttu.edu.sound.Sound;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PlayActivity extends Activity implements Runnable, DialogInterface.OnClickListener, EditText.OnEditorActionListener {
	public RenderView renderView = null;
	public Accelero accelerometer = null;
	public AssetManager assetManager = null;
	public Score leaderboard = null;
	public Looper loop;
	public int stageNumber;
	
	private EditText losingInput;
	private EditText winningInput;
	private TextView losingText;
	private TextView winningText;
	private Button saveButton;
	public Loading loading;
	private PlayActivity.LoadingScreenHandler loadScreenHandler;
	private BroadcastReceiver screenReceiver;
	
	public Format format;
	
	private static class LoadingScreenHandler extends Handler {
		private final WeakReference<PlayActivity> activity;
		
		public LoadingScreenHandler(PlayActivity a) {
			activity = new WeakReference<PlayActivity>(a);
		}
		
		@Override
		public void handleMessage(Message msg) {
			PlayActivity a = activity.get();
			if (a.loading != null) {
				switch (msg.what) {
					case 0:
						a.loading.dismiss();
						break;
					case 1:
						a.loading.show();
						break;
				}
			}
		}
	}
	
	private class ButtonHandler implements View.OnClickListener {
		private Dialog dialog;
		
		public ButtonHandler(Dialog d) {
			this.dialog = d;
		}
		
		public void onClick(View view) {
			switch (view.getId()) {
				case R.id.pause_resume:
					renderView.unpauseGame();
					renderView.timeScore.unpauseTimer();
					break;
				case R.id.pause_restart:
					renderView.resetGame();
					renderView.setDialogFlag(false);
					break;
				case R.id.pause_settings:
					Intent intent = new Intent(PlayActivity.this, SettingsActivity.class);
					PlayActivity.this.startActivity(intent);
					return;
				case R.id.pause_back:
					PlayActivity.this.finish();
					break;
				default:
					break;
			}
			dialog.dismiss();
		}
		
	}
	
	@Override
	public void onCreate(Bundle b) {
		loading = new Loading(this);
		super.onCreate(b);
		if (loadScreenHandler == null) {
			loadScreenHandler = new PlayActivity.LoadingScreenHandler(this);
			loadScreenHandler.sendEmptyMessage(1);
		}
		
		Intent intent = this.getIntent();
		this.stageNumber = intent.getIntExtra("level", 1);
		
		accelerometer = new Accelero();
		leaderboard = new Score(this);
		renderView = new RenderView(this);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Window window = this.getWindow();
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		window.addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(renderView);
		
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		screenReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
					PlayActivity.this.onPause();
				else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON))
					PlayActivity.this.onResume();
			}
		};
		this.registerReceiver(screenReceiver, filter);
		
		//====================================
		
		FileInputStream in;
		try {
			in = this.openFileInput("format.dat");
			ObjectInputStream inn = new ObjectInputStream(new BufferedInputStream(in));
			format = (Format) inn.readObject();
			inn.close();
		}
		catch (FileNotFoundException e) {
			Log.e("PlayActivity", "FileNotFoundException", e);
			format = new Format();
		}
		catch (OptionalDataException e) {
			Log.e("PlayActivity", "OptionalDataException", e);
			format = new Format();
		}
		catch (ClassNotFoundException e) {
			Log.e("PlayActivity", "ClassNotFoundException", e);
			format = new Format();
		}
		catch (IOException e) {
			Log.e("PlayActivity", "IOException", e);
			format = new Format();
		}
		
		//====================================
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		losingText = new TextView(this);
		losingText.setTextSize(20f);
		winningText = new TextView(this);
		winningText.setTextSize(20f);
		losingInput = new EditText(this);
		losingInput.setEllipsize(TruncateAt.END);
		losingInput.setMaxLines(1);
		losingInput.setOnEditorActionListener(this);
		losingInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
		losingInput.setSingleLine();
		losingInput.setHint("Enter your name here.");
		winningInput = new EditText(this);
		winningInput.setEllipsize(TruncateAt.END);
		winningInput.setMaxLines(1);
		winningInput.setOnEditorActionListener(this);
		winningInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
		winningInput.setSingleLine();
		winningInput.setHint("Enter your name here.");
		LinearLayout losingLayout = new LinearLayout(this);
		losingLayout.setOrientation(LinearLayout.VERTICAL);
		losingLayout.addView(losingText);
		losingLayout.addView(losingInput);
		builder.setView(losingLayout);
		builder.setTitle("Retry?");
		builder.setPositiveButton("Retry", this);
		builder.setNeutralButton("Want to save?", this);
		builder.setNegativeButton("Level Selection", this);
		builder.setCancelable(false);
		final AlertDialog ad = builder.create();
		ad.setOnShowListener(new DialogInterface.OnShowListener() {
			public void onShow(final DialogInterface dialog) {
				saveButton = ad.getButton(AlertDialog.BUTTON_NEUTRAL);
				saveButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View arg0) {
						// TODO: Polish the dialog.
						String playerName = losingInput.getText().toString();
						if (playerName.isEmpty())
							playerName = "Player";
						format.addBestEntryAndName(stageNumber, renderView.getStage().getOldAccumulatedScore(), playerName);
						if (saveButton == null) {
							saveButton = ad.getButton(AlertDialog.BUTTON_NEUTRAL);
						}
						saveButton.setText("Saved!");
						saveButton.setEnabled(false);
						return;
					}
				});
				Button retry = ad.getButton(AlertDialog.BUTTON_POSITIVE);
				retry.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						if (saveButton == null) {
							saveButton = ad.getButton(AlertDialog.BUTTON_NEUTRAL);
						}
						saveButton.setText("Want to save?");
						saveButton.setEnabled(true);
						renderView.resetGame();
						renderView.setDialogFlag(false);
						dialog.dismiss();
					}
				});
			}
		});
		renderView.setLosingDialog(ad);
		if (stageNumber >= LevelSelectionActivity.MAX_STAGES) {
			builder = new AlertDialog.Builder(this);
			LinearLayout winningLayout = new LinearLayout(this);
			winningLayout.setOrientation(LinearLayout.VERTICAL);
			winningLayout.addView(winningText);
			winningLayout.addView(winningInput);
			builder.setView(winningLayout);
			builder.setTitle("You Finished the Game!");
			builder.setPositiveButton("Level Selection", this);
			builder.setCancelable(false);
			renderView.setWinningDialog(builder.create());
		}
		else {
			builder = new AlertDialog.Builder(this);
			LinearLayout winningLayout = new LinearLayout(this);
			winningLayout.addView(winningText);
			builder.setTitle("You win!");
			builder.setView(winningLayout);
			builder.setPositiveButton("Next Stage", this);
			builder.setCancelable(false);
			renderView.setWinningDialog(builder.create());
		}
		/*builder = new AlertDialog.Builder(this);
		builder.setTitle("Game Paused!");
		builder.setPositiveButton("Back to Game", this);
		builder.setNeutralButton("Restart", this);
		builder.setNegativeButton("Level Selection", this);
		builder.setCancelable(false);
		renderView.setPausingDialog(builder.create());*/
		
		Dialog pauseDialog = new Dialog(this);
		pauseDialog.setContentView(R.layout.pause_dialog);
		ButtonHandler bh = new ButtonHandler(pauseDialog);
		Button button = (Button) pauseDialog.findViewById(R.id.pause_resume);
		button.setOnClickListener(bh);
		button = (Button) pauseDialog.findViewById(R.id.pause_restart);
		button.setOnClickListener(bh);
		button = (Button) pauseDialog.findViewById(R.id.pause_settings);
		button.setOnClickListener(bh);
		button = (Button) pauseDialog.findViewById(R.id.pause_back);
		button.setOnClickListener(bh);
		
		pauseDialog.setTitle("Game Paused!");
		renderView.setPausingDialog(pauseDialog);
		try {
			renderView.createStage(this);
		}
		catch (IOException e) {
			//Shouldn't occur.
		}
	}
	
	@Override
	public void onResume() {
		if (renderView != null) {
			Thread thread = new Thread(this);
			thread.setName("PlayActivity Thread");
			thread.start();
			renderView.resume();
		}
		if (accelerometer != null)
			accelerometer.resume(this);
		if (Sound.pool == null) {
			Sound.pool = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
			Sound.emergencyLoad(this, getAssets());
		}
		//MenuActivity.music.onResume();
		MenuActivity.player.onResume();
		super.onResume();
	}
	
	@Override
	public void onPause() {
		if (renderView != null)
			renderView.pause();
		if (accelerometer != null)
			accelerometer.pause();
		if (loop != null)
			loop.quit();
		if (Sound.pool != null) {
			Sound.pool.release();
			Sound.pool = null;
		}
		//MenuActivity.music.onPause();
		MenuActivity.player.onPause();
		super.onPause();
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent e) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_MENU:
				if (renderView.isGamePaused())
					renderView.unpauseGame();
				else
					renderView.pauseGame();
				break;
			case KeyEvent.KEYCODE_BACK:
				return super.onKeyUp(keyCode, e);
			case KeyEvent.KEYCODE_SEARCH:
				break;
		}
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent e) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_HOME:
			case KeyEvent.KEYCODE_SEARCH:
				break;
		}
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN:
				float mouseX = e.getX();
				float mouseY = e.getY();
				if (renderView.hud.detectKeyPress(mouseX, mouseY)) {
					if (renderView.isGamePaused())
						renderView.unpauseGame();
					else
						renderView.pauseGame();
					break;
				}
		}
		return true;
	}
	
	public void run() {
		Looper.prepare();
		loop = Looper.myLooper();
		renderView.setHandler(new Handler(), new Handler());
		Looper.loop();
	}
	
	public void onClick(DialogInterface dialog, int which) {
		if (dialog == renderView.getLosingDialog()) {
			switch (which) {
				case DialogInterface.BUTTON_NEGATIVE:
					PlayActivity.this.onBackPressed();
					break;
				default:
					break;
			}
		}
		else if (dialog == renderView.getWinningDialog()) {
			switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					if (stageNumber >= LevelSelectionActivity.MAX_STAGES) {
						String name = winningInput.getText().toString();
						format.addBestEntryAndName(stageNumber, renderView.getStage().getHighScore(), name);
						PlayActivity.this.finish();
						break;
					}
					else {
						long score = renderView.getStage().getHighScore();
						format.addBestEntry(stageNumber, score);
						stageNumber++;
						try {
							renderView.createStage(this);
						}
						catch (IOException e) {
							addEntryAndReturn(1);
							break;
						}
						renderView.resetGame();
					}
					if (stageNumber >= LevelSelectionActivity.MAX_STAGES) {
						//new Thread(lastStageRunnable).start();
						AlertDialog.Builder builder = new AlertDialog.Builder(PlayActivity.this);
						LinearLayout newLayout = new LinearLayout(PlayActivity.this);
						winningText = new TextView(PlayActivity.this);
						winningInput = new EditText(PlayActivity.this);
						winningInput.setEllipsize(TruncateAt.END);
						winningInput.setMaxLines(1);
						winningInput.setOnEditorActionListener(this);
						winningInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
						winningInput.setSingleLine();
						winningInput.setHint("Enter your name here.");
						newLayout.setOrientation(LinearLayout.VERTICAL);
						newLayout.addView(winningText);
						newLayout.addView(winningInput);
						builder.setView(newLayout);
						builder.setTitle("You Finished the Game!");
						builder.setNeutralButton("Level Selection", PlayActivity.this);
						renderView.setWinningDialog(builder.setCancelable(false).create());
					}
					break;
				case DialogInterface.BUTTON_NEUTRAL:
					long lastScore = renderView.getStage().getNewAccumulatedScore();
					format.addBestEntry(stageNumber, lastScore);
					break;
			}
		}
		else if (dialog == renderView.getPausingDialog()) {
			switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					renderView.unpauseGame();
					renderView.timeScore.unpauseTimer();
					break;
				case DialogInterface.BUTTON_NEUTRAL:
					renderView.resetGame();
					renderView.setDialogFlag(false);
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					PlayActivity.this.finish();
					break;
			}
		}
		dialog.dismiss();
	}
	
	public void addEntryAndReturn(final int i) {
		String name;
		switch (i) {
			case 0:
			default:
				name = losingInput.getText().toString();
				break;
			case 1:
				name = winningInput.getText().toString();
				break;
		}
		// SCORE - This is where the game saves the score.
		long score = renderView.getStage().getOldAccumulatedScore();
		if (!name.isEmpty())
			saveFormat(name, score);
		else {
			name = "Player";
			saveFormat(name, score);
		}
		
		PlayActivity.this.onBackPressed();
	}
	
	private void saveFormat(String name, long score) {
		//TODO: Work more of this. (part 1 of ScoreBoard)
		//format.addEntry(name, score);
		format.addBestEntry(stageNumber, score);
	}
	
	public final float getAspectRatio() {
		Display d = getWindowManager().getDefaultDisplay();
		float ratio;
		switch (d.getRotation()) {
			case Surface.ROTATION_0:
			case Surface.ROTATION_90:
			default:
				ratio = (float) d.getHeight() / (float) d.getWidth();
				break;
			case Surface.ROTATION_180:
			case Surface.ROTATION_270:
				ratio = (float) d.getWidth() / (float) d.getHeight();
				break;
		}
		if (ratio <= 0)
			ratio = 1;
		return ratio;
	}
	
	public void setScoreText(String str, long total, final int i) {
		switch (i) {
			case 0:
			default:
				losingText.setText(str + Long.toString(total));
				break;
			case 1:
				winningText.setText(str + Long.toString(total));
				break;
		}
		
	}
	
	public Handler getLoadingScreenHandler() {
		return this.loadScreenHandler;
	}
	
	private Comparator<Map.Entry<Integer, Long>> mapCompare = new Comparator<Map.Entry<Integer, Long>>() {
		public int compare(Entry<Integer, Long> a, Entry<Integer, Long> b) {
			if (a.getValue() > b.getValue())
				return -1;
			if (a.getValue() < b.getValue())
				return 1;
			return 0;
		}
	};
	
	@Override
	public void finish() {
		if (screenReceiver != null)
			this.unregisterReceiver(screenReceiver);
		renderView.clearTotalScore();
		try {
			if (format.highScores != null) {
				List<Map.Entry<Integer, Long>> entries = new ArrayList<Map.Entry<Integer, Long>>(format.highScores.entrySet());
				Collections.sort(entries, mapCompare);
				format.highScores.clear();
				for (Map.Entry<Integer, Long> entry : entries)
					format.highScores.put(entry.getKey(), entry.getValue());
			}
			FileOutputStream out = this.openFileOutput("format.dat", Activity.MODE_PRIVATE);
			ObjectOutputStream output = new ObjectOutputStream(new BufferedOutputStream(out));
			output.writeObject(format);
			output.close();
		}
		catch (IOException e) {
			Log.e("PlayActivity", "Can't save file. IOException.", e);
		}
		super.finish();
	}
	
	@Override
	public void onAttachedToWindow() {
		Window window = this.getWindow();
		window.setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		window.setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
		super.onAttachedToWindow();
	}
	
	@Override
	public void onDestroy() {
		if (renderView.isRunning())
			this.onPause();
		super.onDestroy();
	}
	
	public boolean onEditorAction(TextView view, int actionID, KeyEvent event) {
		if (event != null) {
			if (event.getAction() == KeyEvent.KEYCODE_ENTER || actionID == EditorInfo.IME_ACTION_DONE) {
				InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromInputMethod(view.getWindowToken(), 0);
				return true;
			}
		}
		return false;
	}
	
}