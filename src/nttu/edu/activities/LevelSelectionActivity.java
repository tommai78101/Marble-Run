package nttu.edu.activities;

import nttu.edu.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

public class LevelSelectionActivity extends Activity implements OnItemClickListener, View.OnClickListener {
	
	GridView grid;
	Button backButton;
	public static final int MAX_STAGES = 15;
	final int[] resources = new int[]{R.drawable.s1, R.drawable.s2, R.drawable.s3, R.drawable.s4, R.drawable.s5, R.drawable.s6, R.drawable.s7, R.drawable.s8, R.drawable.s9, R.drawable.s10, R.drawable.s11, R.drawable.s12, R.drawable.s13, R.drawable.s14, R.drawable.s15};
	
	public class CustomView extends ImageView {
		
		public CustomView(Context context) {
			super(context);
		}
		
		//Made so that it is always created as a square.
		@Override
		public void onMeasure(int width, int height) {
			super.onMeasure(width, width);
		}
	}
	public class ImageAdapter extends BaseAdapter {
		
		private Activity activity;
		
		public ImageAdapter(Activity a) {
			activity = a;
		}
		
		public int getCount() {
			return MAX_STAGES;
		}
		
		public Object getItem(int position) {
			return null;
		}
		
		public long getItemId(int position) {
			return 0;
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			//If you wish to revert back to a spritesheet of numbers for each ImageView, comment the working code
			//and uncomment the C-style code below.
			/*	CustomView image;
				if (convertView != null)
					image = (CustomView) convertView;
				else {
					image = new CustomView(activity);
					image.setLayoutParams(new GridView.LayoutParams(128, 128));
					image.setPadding(8, 8, 8, 8);
				}
				int left = ((position % MAX_STAGES) % 6) * 32;
				int top = ((position % MAX_STAGES) / 6) * 32;
				Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.level_select);
				bitmap = Bitmap.createBitmap(bitmap, left, top, 32, 32);
				image.setImageBitmap(bitmap);
				return image;
				*/
			CustomView image;
			if (convertView != null)
				image = (CustomView) convertView;
			else {
				image = new CustomView(activity);
				image.setLayoutParams(new GridView.LayoutParams(128, 128));
				image.setPadding(16, 16, 16, 16);
				int p = position % MAX_STAGES;
				image.setImageResource(resources[p]);
			}
			return image;
			
		}
	}
	
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Window window = this.getWindow();
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		window.addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.level_select);
		grid = (GridView) this.findViewById(R.id.level_select);
		grid.setAdapter(new ImageAdapter(this));
		grid.setOnItemClickListener(this);
		backButton = (Button) this.findViewById(R.id.level_back);
		backButton.setOnClickListener(this);
		
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this, PlayActivity.class);
		intent.putExtra("level", position + 1);
		this.startActivity(intent);
	}
	
	public void onClick(View v) {
		if (v.getId() == R.id.level_back) {
			this.finish();
		}
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
	
}
