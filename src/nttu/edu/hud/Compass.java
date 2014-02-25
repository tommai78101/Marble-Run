package nttu.edu.hud;

import nttu.edu.graphics.Art;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

public abstract class Compass {
	public Matrix matrix;
	public Bitmap bitmap;
	public Rect srcRect;
	public float[] position = new float[2];
	public float angle;
	protected Paint paint;
	
	public Compass() {
		matrix = new Matrix();
		srcRect = new Rect();
		bitmap = Art.compass;
		srcRect.set(0, 0, 16, 16);
		angle = 0f;
	}
	
	public void setPosition(float x, float y) {
		position[0] = x;
		position[1] = y;
	}
	
	public abstract void tick();
	
	public void render(Canvas c) {
		if (bitmap != null) {
			c.drawBitmap(bitmap, matrix, paint);
		}
	}
}
