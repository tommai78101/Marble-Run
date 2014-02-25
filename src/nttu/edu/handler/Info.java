package nttu.edu.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import nttu.edu.level.Stage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.TextView;

public class Info {
	public static AlertDialog createSimpleInfoDialog(Context c, String msg, final Stage s) {
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		TextView view = new TextView(c);
		builder.setView(view);
		builder.setTitle("Information");
		view.setText(msg);
		builder.setNeutralButton("OK", s);
		return builder.create();
	}
	
	public static AlertDialog createSpecificDialog(Context c, String title, String msg, final Stage s) {
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		TextView view = new TextView(c);
		builder.setView(view);
		builder.setTitle(title);
		view.setText(msg);
		builder.setNeutralButton("OK", s);
		return builder.create();
	}
	
	public static AlertDialog createParsedDialog(Activity c, Stage s, final int stageNumber) {
		try {
			String title = getParsedTitle(c, "dialogue/text.txt", stageNumber);
			String msg = getParsedMessage(c, "dialogue/text.txt", stageNumber);
			if (title == null || msg == null) {
				s.togglePauseFlag();
				return null;
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(c);
			TextView view = new TextView(c);
			view.setText(msg);
			builder.setView(view);
			builder.setTitle(title);
			builder.setNeutralButton("OK", s);
			return builder.create();
		}
		catch (IOException e) {
			Log.e("Info", "Unable to initialize parsed dialog.", e);
			throw new RuntimeException(e);
		}
	}
	
	private static String getParsedTitle(Activity c, String fileName, final int i) throws IOException {
		AssetManager assets = c.getAssets();
		InputStream input = assets.open(fileName);
		BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
		String str;
		String results = null;
		while ((str = buffer.readLine()) != null) {
			int value = Integer.parseInt(str.split(" ")[0].split("#")[1]);
			if (value == i) {
				results = str.split("\"")[1];
				break;
			}
		}
		return results;
	}
	
	private static String getParsedMessage(Activity c, String fileName, final int i) throws IOException {
		AssetManager assets = c.getAssets();
		InputStream input = assets.open(fileName);
		BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
		String str;
		String results = null;
		while ((str = buffer.readLine()) != null) {
			int value = Integer.parseInt(str.split(" ")[0].split("#")[1]);
			if (value == i) {
				results = str.split("\"")[3];
				break;
			}
		}
		return results;
	}
}
