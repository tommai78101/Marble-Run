package nttu.edu.hud;

import nttu.edu.graphics.Art;
import nttu.edu.graphics.RenderView;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

public class HUDMenu {
	public Bitmap scaledBitmap;
	public RectF area;
	public Matrix matrix;
	
	public HUDMenu() {
		matrix = new Matrix();
		area = new RectF();
		scaledBitmap = null;
	}
	
	public void setPosition(float x, float y) {
		matrix.postTranslate(x, y);
		area.left = x;
		area.top = y;
	}
	
	public void setScale(float x, float y) {
		matrix.setScale(x, y);
	}
	
	public void render(Canvas c) {
		if (scaledBitmap == null) {
			Matrix temp = new Matrix();
			temp.setScale(12f / 32f * RenderView.AspectRatio, 12f / 32f * RenderView.AspectRatio);
			scaledBitmap = Bitmap.createBitmap(Art.hudMenu, 0, 0, 32, 32, temp, false);
			area.right = area.left + scaledBitmap.getWidth() * 18f / 5f;
			area.bottom = area.top + scaledBitmap.getHeight() * 18f / 5f;
		}
		c.drawBitmap(scaledBitmap, matrix, null);
	}
}
