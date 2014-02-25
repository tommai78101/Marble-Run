package nttu.edu.entity;

import nttu.edu.ball.Ball;
import nttu.edu.ball.Marble;
import nttu.edu.graphics.Art;
import nttu.edu.graphics.RenderView;
import nttu.edu.level.Stage;
import android.graphics.Canvas;

public class Pipe extends Path {
	public Pipe() {
	}
	
	@Override
	public void tick(Stage l) {
		place();
		checkCollision(l.cue);
		for (Marble m : l.marbles)
			checkCollision(m);
	}
	
	@Override
	public void render(Canvas c, final float cx, final float cy) {
		if (bitmap != null) {
			float xOffset = this.position[0] - RenderView.cameraX + cx;
			float yOffset = this.position[1] - RenderView.cameraY + cy;
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
			float size = 8 * PIPE_SIZE * ratio;
			dstRect.set(f - size, g - size, f + size, g + size);
		}
	}
	
	public boolean checkCollision(Ball b) {
		if (b.position[0] > dstRect.left && b.position[0] < dstRect.right && b.position[1] > dstRect.top && b.position[1] < dstRect.bottom) {
			b.setPipe(this);
			return true;
		}
		else if (b.getPipe() != null && b.getPipe() == this)
			b.setPipe(null);
		return false;
	}
	
	@Override
	public void place() {
		//TODO: ???  Possibly not in need.
		float size = 8 * PIPE_SIZE * ratio;
		dstRect.set(position[0] - size, position[1] - size, position[0] + size, position[1] + size);
	}
}
