package nttu.edu.entity;

import nttu.edu.graphics.Art;
import nttu.edu.graphics.RenderView;
import nttu.edu.level.Stage;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

public class Border extends Terrain {
	public float borderWidth;
	public float borderHeight;
	public Paint paint;
	
	public Border(float x, float y) {
		super(x, y);
		move(x, y);
		paint = new Paint();
		paint.setStyle(Style.FILL_AND_STROKE);
		paint.setColor(Color.rgb(79, 234, 255));
	}
	
	@Override
	public void tick(Stage s) {
	}
	
	@Override
	public void render(Canvas c, float centerX, float centerY) {
		if (bitmap != null) {
			float xOffset = x - RenderView.cameraX + centerX;
			float yOffset = y - RenderView.cameraY + centerY;
			move(xOffset, yOffset);
			c.drawRect(dstRect, paint);
		}
		else
			bitmap = Art.sprites;
	}
	
	private void move(float f, float g) {
		if (bitmap != null) {
			float size = 16 * RenderView.AspectRatio;
			dstRect.set(f - size, g - size, f + borderWidth + size, g + borderHeight + size);
		}
	}
	
	public void setNewColor(int r, int g, int b) {
		paint.setColor(Color.rgb(r, g, b));
	}
	
	@Override
	public void reset() {
		x = defaultX;
		y = defaultY;
	}
}
