package nttu.edu.entity;

import nttu.edu.ball.Ball;
import nttu.edu.ball.Marble;
import nttu.edu.graphics.Art;
import nttu.edu.graphics.RenderView;
import nttu.edu.level.Stage;
import android.graphics.Canvas;

public class Connector extends Path {
	//Connects intersecting Pipes together.
	public final float radius;
	
	public Connector(float x, float y, float z) {
		super.setPlacement(x, y, z);
		srcRect.set(40, 8, 48, 16);
		radius = 8 * PIPE_SIZE * RenderView.AspectRatio;
	}
	
	@Override
	public void tick(Stage s) {
		place();
		checkCollision(s.cue);
		for (Marble m : s.marbles)
			checkCollision(m);
	}
	
	private void checkCollision(Ball b) {
		if (b.position[0] >= dstRect.left && b.position[0] <= dstRect.right && b.position[1] >= dstRect.top && b.position[1] <= dstRect.bottom) {
			b.setConnector(this);
		}
		else if (b.getConnector() != null && b.getConnector() == this) {
			b.setConnector(null);
		}
	}
	
	@Override
	public void render(Canvas c, float centerX, float centerY) {
		if (bitmap != null) {
			float xOffset = position[0] - RenderView.cameraX + centerX;
			float yOffset = position[1] - RenderView.cameraY + centerY;
			if (RenderView.bounds.contains(xOffset, yOffset)) {
				move(xOffset, yOffset);
				c.drawBitmap(bitmap, srcRect, dstRect, null);
			}
		}
		else
			bitmap = Art.sprites;
	}
	
	private void move(float f, float g) {
		if (bitmap != null) {
			dstRect.set(f - radius, g - radius, f + radius, g + radius);
		}
	}
	
	@Override
	public void place() {
		float r = this.radius + 8f;
		dstRect.set(position[0] - r, position[1] - r, position[0] + r, position[1] + r);
	}
}
