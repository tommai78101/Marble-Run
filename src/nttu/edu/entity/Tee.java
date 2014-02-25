package nttu.edu.entity;

import nttu.edu.graphics.Art;
import nttu.edu.graphics.RenderView;
import nttu.edu.level.Stage;
import android.graphics.Canvas;
import android.graphics.RectF;

public class Tee extends Terrain {
	private RectF A;
	private RectF B;
	private RectF C;
	public float radius = 0f;
	
	public Tee(float x, float y, float ratio) {
		super(x, y);
		radius = 16 * ratio;
		A = new RectF();
		B = new RectF();
		C = new RectF();
		srcRect.set(0, 16, 16, 32);
		A.set(this.x - radius, this.y - radius, this.x, this.y);
		B.set(this.x, this.y - radius, this.x + radius, this.y);
		C.set(this.x - radius, this.y, this.x, this.y + radius);
		dstRect.set(this.x, this.y, this.x + radius, this.y + radius);
	}
	
	public void tick(Stage s) {
		if (bitmap == null) {
			bitmap = Art.sprites;
			return;
		}
		A.set(this.x - radius, this.y - radius, this.x, this.y);
		B.set(this.x, this.y - radius, this.x + radius, this.y);
		C.set(this.x - radius, this.y, this.x, this.y + radius);
		dstRect.set(this.x, this.y, this.x + radius, this.y + radius);
	}
	
	@Override
	public void render(Canvas c, float centerX, float centerY) {
		if (bitmap != null) {
			float xOffset = this.x - RenderView.cameraX + centerX;
			float yOffset = this.y - RenderView.cameraY + centerY;
			if (RenderView.bounds.contains(xOffset, yOffset)){
				move(xOffset, yOffset);
				c.drawBitmap(bitmap, srcRect, A, null);
				c.drawBitmap(bitmap, srcRect, B, null);
				c.drawBitmap(bitmap, srcRect, C, null);
				c.drawBitmap(bitmap, srcRect, dstRect, null);
			}
		}
	}
	
	private void move(float f, float g) {
		if (bitmap != null) {
			A.set(f - radius, g - radius, f, g);
			B.set(f, g - radius, f + radius, g);
			C.set(f - radius, g, f, g + radius);
			dstRect.set(f, g, f + radius, g + radius);
		}
	}
}
