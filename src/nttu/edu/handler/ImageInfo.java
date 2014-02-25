package nttu.edu.handler;

import java.io.IOException;
import java.io.InputStream;
import nttu.edu.activities.PlayActivity;
import nttu.edu.level.Stage;
import android.app.AlertDialog;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class ImageInfo {
	public static AlertDialog createImageDialog(PlayActivity a, final Stage s) {
		AlertDialog.Builder builder = new AlertDialog.Builder(a);
		AssetManager manager = a.getAssets();
		Bitmap img;
		try {
			InputStream input = manager.open("indicator/tutorial" + a.stageNumber + ".png");
			img = BitmapFactory.decodeStream(input);
		}
		catch (IOException e) {
			s.togglePauseFlag();
			return null;
		}
		if (img == null) {
			s.togglePauseFlag();
			return null;
		}
		ImageView view = new ImageView(a);
		view.setImageBitmap(img);
		builder.setView(view);
		builder.setNeutralButton("OK", s);
		builder.setCancelable(false);
		return builder.create();
	}
}
