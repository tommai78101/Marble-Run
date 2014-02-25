package nttu.edu.entity;

import nttu.edu.ball.Ball;
import nttu.edu.ball.Marble;
import nttu.edu.graphics.Art;
import nttu.edu.graphics.RenderView;
import nttu.edu.level.Stage;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

public abstract class Funnel extends Path {
	//Makes the ball more easier to move into the Pipe.
	//Corner positions.
	
	public static enum Direction {
		LEFT, UP, RIGHT, DOWN
	}
	
	public float x1, x2, y1, y2;
	public float xPos, yPos;
	public float radius;
	public Direction direction;
	protected Matrix matrix;
	
	public Funnel(final float ratio) {
		radius = ratio * 8 * PIPE_SIZE;
		matrix = new Matrix();
	}
	
	@Override
	public void tick(Stage s) {
		place();
		checkCollision(s.cue);
		for (Marble m : s.marbles)
			checkCollision(m);
	}
	
	protected boolean checkCollision(Ball b) {
		if (b.position[0] > dstRect.left && b.position[0] < dstRect.right && b.position[1] > dstRect.top && b.position[1] < dstRect.bottom) {
			b.setFunnel(this);
			return true;
		}
		else if (b.getFunnel() != null && b.getFunnel() == this) {
			b.setFunnel(null);
			b.funnelDetector = false;
			b.funnelFlag1 = false;
			b.funnelFlag2 = false;
		}
		return false;
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
	}
	
	private void move(float f, float g) {
		if (bitmap != null) {
			dstRect.set(f - radius, g - radius, f + radius, g + radius);
		}
	}
	
	@Override
	public void place() {
		dstRect.set(position[0] - radius, position[1] - radius, position[0] + radius, position[1] + radius);
		switch (direction) {
			case LEFT:
			default:
				//Point 1 (Top left)
				x1 = dstRect.left;
				y1 = dstRect.top;
				//Point 2 (Bottom left)
				x2 = dstRect.left;
				y2 = dstRect.bottom;
				break;
			case RIGHT:
				x1 = dstRect.right;
				y1 = dstRect.top;
				x2 = dstRect.right;
				y2 = dstRect.bottom;
				break;
			case UP:
				x1 = dstRect.left;
				y1 = dstRect.top;
				x2 = dstRect.right;
				y2 = dstRect.top;
				break;
			case DOWN:
				x1 = dstRect.left;
				y1 = dstRect.bottom;
				x2 = dstRect.right;
				y2 = dstRect.bottom;
				break;
		}
	}
	
	public void setDirection(Direction d) {
		direction = d;
		switch (d) {
			case LEFT:
			default:
				matrix.reset();
				break;
			case RIGHT:
				matrix.reset();
				matrix.setScale(-1, 1);
				matrix.postRotate(0);
				matrix.postTranslate(-8, 0);
				break;
			case UP:
				matrix.reset();
				matrix.setScale(1, 1);
				matrix.postRotate(90);
				matrix.postTranslate(-8, 0);
				break;
			case DOWN:
				matrix.reset();
				matrix.setScale(1, 1);
				matrix.postRotate(-90);
				matrix.postTranslate(0, -8);
				break;
		}
		bitmap = Bitmap.createBitmap(Art.sprites, 40, 0, 8, 8, matrix, false);
		srcRect.set(0, 0, 8, 8);
	}
}
