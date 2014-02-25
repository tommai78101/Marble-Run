package nttu.edu.alt;

import nttu.edu.entity.Tee;
import nttu.edu.graphics.Art;
import nttu.edu.graphics.RenderView;
import nttu.edu.level.Stage;
import android.graphics.Canvas;

public class NewCue extends NewBall implements Obstacle {
	private boolean goalFlag;
	private boolean jumpFlag;
	private boolean soundFlag;
	private boolean deadFlag;
	private final float RADIUS;
	private float xBound;
	private float yBound;
	private float[] defaults;
	
	public NewCue(float w, float h, float ratio) {
		RADIUS = 8f * ratio;
		xBound = w;
		yBound = h;
		goalFlag = jumpFlag = soundFlag = deadFlag = false;
		this.bitmap = Art.sprites;
		this.srcRect.set(0, 0, 16, 16);
		this.dstRect.set(0, 0, 16, 16);
		
	}
	
	@Override
	public void tick(Stage s) {
	}
	
	/*
	@Override
	public void tick(Stage s) {
		if (this.bitmap != null) {
			if (s.newHole.check(this) || goalFlag) {
				if (!jumpFlag) {
					goalFlag = true;
					die();
					s.newHole.respond(this);
					
					position[0] -= speed[0];
					position[1] -= speed[1];
					speed[0] *= 0.1f;
					speed[1] *= 0.1f;
					
					keepInBoundary();
				}
				else {
					jump();
					if (position[2] <= 0f) {
						soundFlag = false;
						jumpFlag = false;
					}
				}
			}
			else {
				if (!deadFlag) {
					if (acceleration[2] < 0f)
						setJumpState();
					if (!jumpFlag || position[2] <= 0f) {
						if (!goalFlag) {
							for (Obstacle o : s.obstacles)
								if (o.check(this))
									o.respond(this);
							keepInBoundary();
						}
					}
					else {
						if (soundFlag) {
							//PLAY
							soundFlag = false;
						}
						jump();
					}
				}
				else
					falling();
			}
			lockCamera();
		}
		else
			this.bitmap = Art.sprites;
	}*/
	
	@Override
	public void render(Canvas c, float centerX, float centerY) {
		if (bitmap != null) {
			float xOffset = position[0] - RenderView.cameraX;
			float yOffset = position[1] - RenderView.cameraY;
			move(centerX + xOffset, centerY + yOffset);
			c.drawBitmap(bitmap, srcRect, dstRect, null);
		}
	}
	
	@Override
	public void reset() {
		for (int i = 0; i < 3; i++)
			position[i] = defaults[i];
		jumpFlag = goalFlag = deadFlag = soundFlag = false;
		lockCamera();
	}
	
	public void setStartingPosition(Tee tee) {
		position[0] = tee.x;
		position[1] = tee.y;
		for (int i = 0; i < 3; i++)
			defaults[i] = position[i];
		keepInBoundary();
	}
	
	public boolean hasReachedGoal() {
		return goalFlag;
	}
	
	//===========  PRIVATE METHODS  =====================
	
	private void lockCamera() {
		RenderView.cameraX = position[0];
		RenderView.cameraY = position[1];
	}
	
	private void die() {
		if (!deadFlag)
			deadFlag = true;
		if (jumpFlag)
			jumpFlag = false;
	}
	
	private void jump() {
		speed[0] += acceleration[0];
		speed[1] += acceleration[1];
		position[0] -= speed[0];
		position[1] -= speed[1];
		speed[0] *= 0.1f;
		speed[1] *= 0.1f;
		keepInBoundary();
	}
	
	private void keepInBoundary() {
		if (position[0] > xBound + RADIUS)
			position[0] = xBound + RADIUS;
		if (position[1] > yBound + RADIUS)
			position[1] = yBound + RADIUS;
		if (position[0] < -RADIUS)
			position[0] = RADIUS;
		if (position[1] < -RADIUS)
			position[1] = -RADIUS;
	}
	
	private void move(float f, float g) {
		dstRect.set(f - RADIUS, g - RADIUS, f + RADIUS, g + RADIUS);
		if (jumpFlag || deadFlag) {
			float s = position[2] * RenderView.AspectRatio;
			dstRect.left -= s;
			dstRect.right += s;
			dstRect.top -= s;
			dstRect.bottom += s;
		}
	}
	
	private void setJumpState() {
		jumpFlag = true;
		soundFlag = true;
	}
	
	private void falling() {
		if (position[2] > -RADIUS) {
			float temp = 0.1f * position[2];
			position[2] -= 1.2f * temp;
		}
	}
	
	//===========  IMPLEMENTED METHODS  ===================
	
	public boolean check(Obstacle obstacle) {
		return false;
	}
	
	public void respond(Obstacle obstacle) {
		if (obstacle == this) {
			for (int i = 0; i < 2; i++) {
				//Not set to Verlet Integration, due to too much work.
				speed[i] += acceleration[i];
				position[i] -= speed[i];
				speed[i] *= 0.6f;
			}
		}
	}
	
}
