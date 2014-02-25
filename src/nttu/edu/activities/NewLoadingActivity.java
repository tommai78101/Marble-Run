package nttu.edu.activities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

import nttu.edu.R;
import nttu.edu.graphics.Art;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

public class NewLoadingActivity extends Activity {
	public ProgressBar bar;
	private AssetManager assetManager;
	public Handler handler;
	public ProgressTask task;
	
	private final String[] list = {
	// Art.sprites
	"art/sprites.png" };
	
	private class ProgressTask extends AsyncTask<Void, Void, Void> {
		public int totalByteSize;
		public int currentByteSize;
		public Queue<Bitmap> bitmapQueue;
		public Queue<byte[]> byteQueue;
		
		public ProgressTask() {
			totalByteSize = 0;
			currentByteSize = 0;
			bitmapQueue = new LinkedList<Bitmap>();
			byteQueue = new LinkedList<byte[]>();
		}
		
		public void onPostExecute(Void params) {
			Art.sprites = bitmapQueue.remove();
			finish();
		}
		
		public void onPreExecute() {
			try {
				for (int i = 0; i < list.length; i++) {
					byte[] bytes = readFromStream(list[i]);
					totalByteSize += bytes.length;
					byteQueue.add(bytes);
				}
				bar.setMax(totalByteSize);
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		public void onProgressUpdate(Void... params) {
			bar.setProgress(currentByteSize);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			while (currentByteSize < totalByteSize) {
				try {
					Thread.sleep(1000);
					if (byteQueue.size() > 0) {
						byte[] bytes = byteQueue.remove();
						Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
						bitmapQueue.add(bitmap);
						currentByteSize += bytes.length;
						this.publishProgress();
					}
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
		
		private byte[] readFromStream(String path) throws IOException {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length = 0;
			InputStream input = assetManager.open(path);
			while (input.available() > 0 && (length = input.read(buffer)) != -1)
				output.write(buffer, 0, length);
			return output.toByteArray();
		}
		
	}
	
	public void onCreate(Bundle b) {
		super.onCreate(b);
		this.setContentView(R.layout.progressbar);
		assetManager = this.getAssets();
		handler = new Handler();
		task = new ProgressTask();
		bar = (ProgressBar) this.findViewById(R.id.loadingBar);
		if (bar == null) throw new RuntimeException("Failed to load the progress bar.");
		task.execute();
	}
	
	public void finish() {
		Intent intent = new Intent(this, MenuActivity.class);
		intent.putExtra("Success Flag", Art.sprites != null);
		this.setResult(RESULT_OK, intent);
		super.finish();
	}
}
