package nttu.edu.entity;

import nttu.edu.ball.Ball;
import nttu.edu.ball.Marble;
import nttu.edu.graphics.Art;
import nttu.edu.graphics.RenderView;
import nttu.edu.level.Stage;
import android.graphics.Canvas;

public class CurvePipe extends Path {
	
	public float x, y;
	
	public CurvePipe() {
		super();
	}
	
	public boolean checkCollision(Ball b) {
		switch(this.orientation) {
			case LEFT_TO_UP:
			case LEFT_TO_DOWN:
			case RIGHT_TO_UP:
			case RIGHT_TO_DOWN:
				if (b.position[0] <= dstRect.right && b.position[1] <= dstRect.bottom && b.position[0] >= dstRect.left && b.position[1] >= dstRect.top) {
					b.setCurvePipe(this);
					break;
				}
			default:
				if (b.getCurvePipe() != null && b.getCurvePipe() == this)
					b.setCurvePipe(null);
				return false;
		}
		return true;
	}
	
	public void tick(Stage s) {
		place();
		checkCollision(s.cue);
		for (Marble m: s.marbles)
			checkCollision(m);
	}
	
	public void place() {
		float size = 8 * PIPE_SIZE * ratio;
		dstRect.set(position[0] - size, position[1] - size, position[0] + size, position[1] + size);
		switch(this.orientation) {
			case LEFT_TO_UP:
				x = dstRect.left;
				y = dstRect.top;
				break;
			case LEFT_TO_DOWN:
				x = dstRect.left;
				y = dstRect.bottom;
				break;
			case RIGHT_TO_UP:
				x = dstRect.right;
				y = dstRect.top;
				break;
			case RIGHT_TO_DOWN:
				x = dstRect.right;
				y = dstRect.bottom;
				break;
		}
	}
	
	@Override
	public void render(Canvas c, final float centerX, final float centerY) {
		if (bitmap == null) {
			bitmap = Art.sprites;
			return;
		}
		float xOffset = this.position[0] - RenderView.cameraX + centerX;
		float yOffset = this.position[1] - RenderView.cameraY + centerY;
		if (RenderView.bounds.contains(xOffset, yOffset)){
			move(xOffset, yOffset);
			c.drawBitmap(bitmap, srcRect, dstRect, null);
		}
	}
	
	private void move(float f, float g) {
		if (bitmap == null)
			return;
		float size = 8 * PIPE_SIZE * ratio;
		dstRect.set(f - size, g - size, f + size, g + size);
	}
}
