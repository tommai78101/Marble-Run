package nttu.edu.hud;

import nttu.edu.entity.Hole;
import nttu.edu.entity.Terrain;
import nttu.edu.graphics.RenderView;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

public class GoalCompass extends Compass {
	public Terrain target;
	
	public GoalCompass(Hole h) {
		super();
		setTarget(h);
	}
	
	@Override
	public void tick() {
		if (bitmap != null) {
			angle = (float) Math.toDegrees(Math.atan2(target.y - RenderView.cameraY, target.x - RenderView.cameraX)) + 90f;
			//TODO: Fix this.
			matrix.setScale(0.75f, 1.5f);
			matrix.postTranslate(-(bitmap.getWidth() / 2f), 0f);
			matrix.postRotate(angle);
			matrix.postTranslate(position[0], position[1]);
		}
	}
	
	public void setTarget(Hole t) {
		target = t;
		paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStyle(Style.FILL);
	}
}
