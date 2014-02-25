package nttu.edu.hud;

import nttu.edu.level.Stage;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class HUDScore {
	public String scorePoints;
	private Paint paint;
	private float x, y;
	
	public HUDScore() {
		scorePoints = new String();
		paint = new Paint();
		paint.setColor(Color.DKGRAY);
		paint.setTextSize(24f);
	}
	
	public void setPosition(float a, float b) {
		x = a;
		y = b;
	}
	
	public void render(Canvas c) {
		c.drawText(scorePoints, x, y, paint);
	}
	
	public void tick(Stage s) {
		//scorePoints = Long.toString(s.getScore()) + "/" + Long.toString(s.getTotalScore());
	}
	
	public void clear() {
		scorePoints = "";
	}
}
