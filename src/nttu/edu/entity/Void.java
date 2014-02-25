package nttu.edu.entity;

import java.util.Random;
import nttu.edu.graphics.Art;
import nttu.edu.graphics.RenderView;
import nttu.edu.level.Stage;
import android.graphics.Canvas;

public class Void extends Terrain {
	public float radius = 0f;
	public int direction;
	
	public Void(float x, float y, final float ratio) {
		super(x, y);
		// TODO: Will give it a random background.
		Random random = new Random();
		switch (random.nextInt(20)) {
			case 2:
				srcRect.set(32, 16, 40, 24);
				break;
			case 5:
				srcRect.set(32, 24, 40, 32);
				break;
			case 8:
				srcRect.set(40, 24, 48, 32);
				break;
			default:
				srcRect.set(40, 16, 48, 24);
				break;
		}
		this.radius = 16 * ratio;
		this.direction = random.nextInt(4);
		dstRect.set(this.x - radius, this.y - radius, this.x + radius, this.y + radius);
	}
	
	@Override
	public void tick(Stage s) {
		if (bitmap != Art.sprites) {
			bitmap = Art.sprites;
			return;
		}
		dstRect.set(x - radius, y - radius, x + radius, y + radius);
		if (s.cue.position[0] <= dstRect.right && s.cue.position[1] <= dstRect.bottom && s.cue.position[0] >= dstRect.left && s.cue.position[1] >= dstRect.top) {
			if (s.cue.position[2] < 0.1f)
				s.cue.die();
		}
	}
	
	@Override
	public void render(Canvas c, float centerX, float centerY) {
		if (bitmap != null) {
			float xOffset = this.x - RenderView.cameraX + centerX;
			float yOffset = this.y - RenderView.cameraY + centerY;
			if (RenderView.bounds.contains(xOffset, yOffset)) {
				move(xOffset, yOffset);
				//TODO: Make this bitmap use the matrix, instead of rotating the entire Canvas (takes a long time).
				c.save();
				c.rotate((this.direction * 90), dstRect.centerX(), dstRect.centerY());
				c.drawBitmap(bitmap, srcRect, dstRect, null);
				c.restore();
				
			}
		}
	}
	
	private void move(float f, float g) {
		if (bitmap != null) {
			dstRect.set(f - this.radius, g - this.radius, f + this.radius, g + this.radius);
		}
	}
}
