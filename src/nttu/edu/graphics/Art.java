package nttu.edu.graphics;

import java.io.IOException;
import java.io.InputStream;
import nttu.edu.activities.PlayActivity;
import nttu.edu.level.HUD;
import nttu.edu.level.Stage;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

public class Art {
	public static Bitmap sprites;
	public static Bitmap gameOver;
	public static Bitmap compass;
	public static Bitmap hud;
	public static Bitmap hudMenu;
	public static Bitmap coin;
	public static Bitmap animatedHole;
	
	public static Bitmap loadBitmap(AssetManager manager, String filename) {
		InputStream input = null;
		Bitmap bitmap = null;
		try {
			input = manager.open(filename);
			bitmap = BitmapFactory.decodeStream(input);
		}
		catch (IOException e) {
			e.printStackTrace();
			Log.e("ERROR", "Can't load bitmap correctly.");
		}
		if (bitmap == null) {
			Log.e("ERROR", "Bitmap is null. Expect NullPointerException.");
		}
		return bitmap;
	}
	
	public static Stage loadStage(PlayActivity activity, Stage s, int number) throws IOException {
		Stage stage = null;
		try {
			AssetManager assets = activity.getAssets();
			InputStream input = assets.open("stages/stage" + Integer.toString(number) + ".png");
			Bitmap bitmap = BitmapFactory.decodeStream(input);
			stage = new Stage(bitmap.getWidth(), bitmap.getHeight());
			stage.number = number;
			if (s != null) {
				stage.cloneScores(s);
			}
			bitmap.getPixels(stage.data, 0, stage.width, 0, 0, stage.width, stage.height);
		}
		catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(activity, "That was the last stage.", Toast.LENGTH_SHORT).show();
			throw e;
		}
		return stage;
	}
	
	public static HUD loadHUD(RenderView render) {
		//TODO: If stage doesn't contain any Marbles, tweak the HUD.
		HUD hud = null;
		if (render.hud == null) {
			hud = new HUD();
		}
		else {
			hud = render.hud;
			hud.clean();
		}
		hud.setScale(2f, 2f);
		hud.setPosition(render.width - 60f, 0f);
		hud.addCompass(render.stage);
		return hud;
	}
}
