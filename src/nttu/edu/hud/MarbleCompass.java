package nttu.edu.hud;

import java.util.Random;
import nttu.edu.ball.Marble;
import nttu.edu.graphics.RenderView;
import android.graphics.Color;
import android.graphics.Paint;

public class MarbleCompass extends Compass {
	public Marble target;
	
	public MarbleCompass(Marble m) {
		super();
		setTarget(m);
	}
	
	@Override
	public void tick() {
		if (bitmap != null) {
			angle = (float) Math.toDegrees(Math.atan2(target.position[1] - RenderView.cameraY, target.position[0] - RenderView.cameraX)) + 90f;
			matrix.setScale(RenderView.AspectRatio / 2, RenderView.AspectRatio);
			matrix.postTranslate(-bitmap.getWidth() * (RenderView.AspectRatio / 2) + 8, -bitmap.getHeight() * RenderView.AspectRatio);
			matrix.postRotate(angle);
			matrix.postTranslate(position[0], position[1]);
		}
	}
	
	public void setTarget(Marble m) {
		target = m;
		paint = m.paint;
		if (paint == null) {
			Random r = new Random();
			paint = new Paint();
			paint.setColor(Color.rgb(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
		}
	}
}
