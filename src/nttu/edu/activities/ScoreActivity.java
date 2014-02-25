package nttu.edu.activities;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import nttu.edu.R;
import nttu.edu.score.Format;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ScoreActivity extends Activity implements View.OnClickListener {
	
	//TODO: Start making a way to add points to a single game.	
	Button backButton;
	Button deleteButton;
	TableLayout entry;
	Format format;
	
	private Comparator<Map.Entry<AbstractMap.SimpleEntry<String, Integer>, Long>> entryCompare = new Comparator<Map.Entry<AbstractMap.SimpleEntry<String, Integer>, Long>>() {
		public int compare(Entry<SimpleEntry<String, Integer>, Long> a, Entry<SimpleEntry<String, Integer>, Long> b) {
			if (a.getValue() > b.getValue())
				return -1;
			if (a.getValue() < b.getValue())
				return 1;
			return 0;
		}
	};
	
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Window window = this.getWindow();
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		window.addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
		this.setContentView(R.layout.newscore);
		
		//=============================================================
		
		//TODO: Work more of this. (part 3 of scoreboard)
		entry = (TableLayout) this.findViewById(R.id.entry1);
		open();
		
		//=============================================================
	}
	
	private void open() {
		FileInputStream in;
		try {
			in = this.openFileInput("format.dat");
			ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(in));
			format = (Format) input.readObject();
			input.close();
			/*
			List<Map.Entry<Integer, Long>> entries = new ArrayList<Map.Entry<Integer, Long>>(format.highScores.entrySet());
			Collections.sort(entries, mapCompare);
			format.highScores.clear();
			for (Map.Entry<Integer, Long> entry : entries)
				format.highScores.put(entry.getKey(), entry.getValue());
			*/
			if (format.entries != null) {
				List<Map.Entry<AbstractMap.SimpleEntry<String, Integer>, Long>> entries = new ArrayList<Map.Entry<AbstractMap.SimpleEntry<String, Integer>, Long>>(format.entries.entrySet());
				Collections.sort(entries, entryCompare);
				format.entries.clear();
				for (Map.Entry<AbstractMap.SimpleEntry<String, Integer>, Long> m : entries) {
					format.entries.put(m.getKey(), m.getValue());
				}
				
				TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
				TextView text;
				TableRow row;
				
				for (int i = 0; i < entries.size(); i++) {
					row = new TableRow(this);
					//Entry #
					text = new TextView(this);
					text.setText(Integer.toString(i + 1));
					text.setGravity(Gravity.CENTER);
					row.addView(text, 0, params);
					//Stage #
					text = new TextView(this);
					text.setText(entries.get(i).getKey().getValue().toString());
					text.setGravity(Gravity.CENTER);
					row.addView(text, 1, params);
					//Name
					text = new TextView(this);
					text.setText(entries.get(i).getKey().getKey());
					text.setGravity(Gravity.CENTER);
					row.addView(text, 2, params);
					//High Score
					text = new TextView(this);
					text.setText(entries.get(i).getValue().toString());
					text.setGravity(Gravity.CENTER);
					row.addView(text, 3, params);
					entry.addView(row);
				}
			}
		}
		catch (FileNotFoundException e) {
			Log.e("ScoreActivity", "FileNotFoundException", e);
			format = null;
		}
		catch (IOException e) {
			Log.e("ScoreActivity", "IOException", e);
			format = null;
		}
		catch (ClassNotFoundException e) {
			Log.e("ScoreActivity", "ClassNotFoundException", e);
			format = null;
		}
	}
	
	@Override
	public void onResume() {
		//MenuActivity.music.onResume();
		MenuActivity.player.onResume();
		backButton = (Button) this.findViewById(R.id.score_back1);
		backButton.setOnClickListener(this);
		deleteButton = (Button) this.findViewById(R.id.score_delete1);
		deleteButton.setOnClickListener(this);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		//MenuActivity.music.onPause();
		MenuActivity.player.onPause();
		super.onPause();
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent e) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
			super.finish();
		return true;
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.score_back1:
				super.finish();
				break;
			case R.id.score_delete1:
				if (this.deleteFile("format.dat"))
					entry.removeAllViews();
				else
					Log.e("ScoreActivity", "Couldn't delete format.dat");
				break;
		}
	}
}
