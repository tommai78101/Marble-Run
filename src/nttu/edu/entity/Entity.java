package nttu.edu.entity;

import nttu.edu.graphics.Art;
import nttu.edu.level.Stage;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

public abstract class Entity implements Comparable<Entity> {
	
	public Bitmap bitmap;
	public Rect srcRect;
	public RectF dstRect;
	public int priority;
	
	public Entity() {
		//Default setup.
		bitmap = Art.sprites;
		srcRect = new Rect();
		dstRect = new RectF();
	}
	
	public void setBitmap(Bitmap b) {
		bitmap = b;
	}
	
	public void setSourceRect(Rect r) {
		srcRect = r;
	}
	
	public void setDestinationRect(RectF r) {
		dstRect = r;
	}
	
	public void setDestinationRect(Rect r) {
		dstRect.set(r);
	}
	
	public void setPriority(int value) {
		priority = value;
	}
	
	public abstract void tick(Stage s);
	
	public abstract void render(Canvas c, final float centerX, final float centerY);
	
	public int compareTo(Entity another) {
		if (this.priority - another.priority > 0)
			return 1;
		if (this.priority - another.priority < 0)
			return -1;
		return 0;
	}
	
	public abstract void reset();
	
}
