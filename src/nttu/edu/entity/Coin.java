package nttu.edu.entity;

import nttu.edu.graphics.Art;
import nttu.edu.graphics.RenderView;
import nttu.edu.level.Stage;
import nttu.edu.sound.Sound;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;

public class Coin extends Entity {
	public static enum ColorType {
		YELLOW, RED, BLUE, GREEN
	};
	
	private float x, y;
	private ColorType type;
	private boolean getCoin = false;
	private boolean scored = false;
	private boolean flip;
	private byte state;
	private final float radius = 6f * RenderView.AspectRatio;
	private final Paint paint;
	
	public Coin(ColorType t, float f, float g) {
		paint = new Paint();
		switch (t) {
			case YELLOW:
			default:
				paint.setColorFilter(new LightingColorFilter(Color.YELLOW, 0x545400));
				break;
			case RED:
				paint.setColorFilter(new LightingColorFilter(Color.RED, 0x540000));
				break;
			case BLUE:
				paint.setColorFilter(new LightingColorFilter(Color.BLUE, 0x000054));
				break;
			case GREEN:
				paint.setColorFilter(new LightingColorFilter(Color.GREEN, 0x005400));
				break;
		}
		type = t;
		getCoin = false;
		state = 0x0;
		flip = false;
		srcRect.set(0, 0, 16, 16);
		dstRect.set(0, 0, 16, 16);
		bitmap = Art.coin;
		x = f;
		y = g;
	}
	
	@Override
	public void tick(Stage s) {
		dstRect.set(x - radius, y - radius, x + radius, y + radius);
		if (dstRect.contains(s.cue.position[0], s.cue.position[1])) {
			getCoin = true;
			if (!scored) {
				switch (type) {
					default:
					case YELLOW:
						s.addTemporaryScore(200);
						s.addTemporaryTotalScore(200);
						break;
					case RED:
						s.addTemporaryScore(500);
						s.addTemporaryTotalScore(500);
						break;
					case GREEN:
						s.addTemporaryScore(1000);
						s.addTemporaryTotalScore(1000);
						break;
					case BLUE:
						s.addTemporaryScore(2000);
						s.addTemporaryTotalScore(2000);
						break;
				}
				scored = true;
				Sound.play(Sound.coinID);
			}
		}
		checkState();
	}
	
	@Override
	public void render(Canvas c, float centerX, float centerY) {
		if (bitmap != null) {
			if (!getCoin) {
				srcRect.set(state * 16, srcRect.top, (state + 1) * 16, srcRect.bottom);
				float xOffset = this.x - RenderView.cameraX + centerX;
				float yOffset = this.y - RenderView.cameraY + centerY;
				if (RenderView.bounds.contains(xOffset, yOffset)) {
					move(xOffset, yOffset);
					c.drawBitmap(bitmap, srcRect, dstRect, paint);
				}
			}
		}
	}
	
	private void checkState() {
		if (!flip) {
			state++;
			if (state > 5) {
				state = 5;
				flip = true;
			}
		}
		else {
			state--;
			if (state < 0) {
				state = 0;
				flip = false;
			}
		}
	}
	
	private void move(float f, float g) {
		if (bitmap != null) {
			dstRect.set(f - this.radius, g - this.radius, f + this.radius, g + this.radius);
		}
	}
	
	@Override
	public void reset() {
		getCoin = false;
		state = 0x0;
		scored = false;
	}
	
	public boolean gotCoin() {
		return getCoin;
	}
	
	public ColorType getType() {
		return type;
	}
}
