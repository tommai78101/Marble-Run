package nttu.edu.entity;

import nttu.edu.graphics.Art;
import nttu.edu.graphics.RenderView;
import nttu.edu.level.Stage;
import android.graphics.Canvas;

public class Hole extends Terrain {
	public float radius;
	private int slideNumber;
	
	public Hole(float x, float y, float ratio) {
		// (x,y) is the center of the hole.
		//Size of radius = 4.
		//
		super(x, y);
		this.bitmap = Art.animatedHole;
		this.radius = 16 * ratio;
		this.slideNumber = 0;
		srcRect.set(0, 0, 16, 16);
		dstRect.set(this.x - this.radius, this.y - this.radius, this.x + this.radius, this.y + this.radius);
	}
	
	@Override
	public void tick(Stage s) {
		if (bitmap == null) {
			bitmap = Art.animatedHole;
			return;
		}
		dstRect.set(this.x - this.radius, this.y - this.radius, this.x + this.radius, this.y + this.radius);
		slideNumber = slideNumber >= 15 ? 0 : slideNumber + 1;
	}
	
	@Override
	public void render(Canvas c, final float cx, final float cy) {
		if (bitmap != null) {
			int horizontal = (slideNumber % 4 * 16);
			int vertical = (slideNumber / 4 * 16);
			srcRect.set(horizontal, vertical, horizontal + 16, vertical + 16);
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
}
