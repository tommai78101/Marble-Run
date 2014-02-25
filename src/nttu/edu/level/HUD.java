package nttu.edu.level;

import java.util.ArrayList;
import java.util.List;
import nttu.edu.ball.Marble;
import nttu.edu.graphics.Art;
import nttu.edu.graphics.RenderView;
import nttu.edu.hud.Compass;
import nttu.edu.hud.GoalCompass;
import nttu.edu.hud.HUDMenu;
import nttu.edu.hud.HUDScore;
import nttu.edu.hud.MarbleCompass;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

public class HUD {
	public Bitmap bitmap;
	public float[] position = new float[2];
	public List<Compass> compasses = new ArrayList<Compass>();
	public Matrix matrix;
	public HUDMenu menu;
	public HUDScore score;
	
	public HUD() {
		matrix = new Matrix();
		bitmap = Art.hud;
		menu = new HUDMenu();
		score = new HUDScore();
	}
	
	public void setPosition(float x, float y) {
		position[0] = x;
		position[1] = y;
		matrix.postTranslate(x, y);
		//Scale (x,y) = (2*4px,2*4px) = (8,8), 4px is distance from top left corner of HUD to menu button top left corner.
		menu.setPosition(x + 8, y + 8);
		//Not needed.
		//score.setPosition(x + 70 * RenderView.AspectRatio, y + 35 * RenderView.AspectRatio);
	}
	
	public void setScale(float x, float y) {
		matrix.setScale(x, y);
		menu.setScale(x * 1.1f, y * 1.1f);
	}
	
	public void addCompass(Stage s) {
		Compass compass = null;
		for (Marble m : s.marbles) {
			compass = new MarbleCompass(m);
			compass.setPosition(position[0] + 30 * RenderView.AspectRatio, position[1] + 30 * RenderView.AspectRatio);
			compasses.add(compass);
		}
		compass = new GoalCompass(s.hole);
		compass.setPosition(position[0] + 15f * RenderView.AspectRatio, position[1] + 45f * RenderView.AspectRatio);
		compasses.add(0, compass);
	}
	
	public void tick(Stage s) {
		if (!compasses.isEmpty()) {
			for (Compass c : compasses) {
				if (c != null)
					c.tick();
			}
		}
		score.tick(s);
	}
	
	public void render(Canvas c) {
		if (bitmap != null) {
			c.drawBitmap(bitmap, matrix, null);
			menu.render(c);
			score.render(c);
			if (!compasses.isEmpty())
				for (Compass compass : compasses)
					if (compass != null)
						compass.render(c);
		}
	}
	
	public boolean detectKeyPress(float x, float y) {
		if (menu.area.contains(x, y))
			return true;
		return false;
	}
	
	public void clean() {
		while (!compasses.isEmpty())
			compasses.remove(0);
		score.clear();
	}
}