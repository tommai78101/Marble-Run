package nttu.edu.entity;

import nttu.edu.ball.Ball;
import nttu.edu.ball.Marble;
import nttu.edu.graphics.RenderView;
import nttu.edu.level.Stage;
import android.graphics.Canvas;
import android.util.FloatMath;

public class Bumper extends Terrain {
	
	public float radius;
	
	public Bumper(float x, float y, final float ratio) {
		super(x, y);
		radius = 16f * ratio;
		srcRect.set(0, 32, 16, 48);
		dstRect.set(this.x - radius, this.y - radius, this.x + radius, this.y + radius);
	}
	
	@Override
	public void tick(Stage s) {
		checkCollision(s.cue);
		for (Marble m : s.marbles) {
			checkCollision(m);
		}
	}
	
	@Override
	public void render(Canvas c, final float cx, final float cy) {
		if (bitmap != null) {
			float xOffset = this.x - RenderView.cameraX + cx;
			float yOffset = this.y - RenderView.cameraY + cy;
			if (RenderView.bounds.contains(xOffset, yOffset)) {
				move(xOffset, yOffset);
				c.drawBitmap(bitmap, srcRect, dstRect, null);
			}
		}
	}
	
	private void move(float f, float g) {
		if (bitmap != null) {
			dstRect.set(f - this.radius, g - this.radius, f + this.radius, g + this.radius);
		}
	}
	
	private void checkCollision(Ball b) {
		float dx = b.position[0] - x;
		float dy = b.position[1] - y;
		float distance = FloatMath.sqrt(dx * dx + dy * dy);
		if (distance < radius + b.radius)
			b.bumper = this;
		else if (b.bumper != null && b.bumper == this)
			b.bumper = null;
	}
	
}
