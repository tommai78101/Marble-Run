package nttu.edu.entity;

import nttu.edu.ball.Ball;
import nttu.edu.graphics.Art;
import nttu.edu.graphics.RenderView;
import android.graphics.Bitmap;

public class Ramp extends Funnel {
	
	public Ramp(float ratio) {
		super(ratio);
	}
	
	@Override
	public boolean checkCollision(Ball b) {
		if (b.position[0] > dstRect.left && b.position[0] < dstRect.right && b.position[1] > dstRect.top && b.position[1] < dstRect.bottom) {
			b.setRamp(this);
			return true;
		}
		else if (b.getRamp() != null && b.getRamp() == this) {
			b.setRamp(null);
			b.funnelDetector = false;
			b.funnelFlag1 = false;
			b.funnelFlag2 = false;
		}
		return false;
	}
	
	@Override
	public void place() {
		dstRect.set(position[0] - radius, position[1] - radius, position[0] + radius, position[1] + radius);
		float size = 10 * PIPE_SIZE * RenderView.AspectRatio;
		switch (direction) {
			case LEFT:
			default:
				//Point 1 (Top left)
				x1 = dstRect.left + size;
				y1 = dstRect.top;
				//Point 2 (Bottom left)
				x2 = dstRect.left + size;
				y2 = dstRect.bottom;
				break;
			case RIGHT:
				x1 = dstRect.right - size;
				y1 = dstRect.top;
				x2 = dstRect.right - size;
				y2 = dstRect.bottom;
				break;
			case UP:
				x1 = dstRect.left;
				y1 = dstRect.top + size;
				x2 = dstRect.right;
				y2 = dstRect.top + size;
				break;
			case DOWN:
				x1 = dstRect.left;
				y1 = dstRect.bottom - size;
				x2 = dstRect.right;
				y2 = dstRect.bottom - size;
				break;
		}
	}
	
	@Override
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
		bitmap = Bitmap.createBitmap(Art.sprites, 48, 0, 8, 8, matrix, false);
		srcRect.set(0, 0, 8, 8);
	}
}
