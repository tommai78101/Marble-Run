package nttu.edu.ball;

import java.util.Random;
import nttu.edu.entity.Bumper;
import nttu.edu.entity.CurvePipe;
import nttu.edu.entity.Pipe;
import nttu.edu.entity.Ramp;
import nttu.edu.graphics.Art;
import nttu.edu.graphics.RenderView;
import nttu.edu.level.Stage;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.util.FloatMath;

public class Marble extends Ball {
	
	public final int INDEX;
	public boolean renderFlag;
	public float defaultX;
	public float defaultY;
	public float screenHeight;
	public float screenWidth;
	
	private boolean atGoal;
	private boolean inArea;
	public Paint paint;
	
	public final int SCORE;
	public boolean scored;
	
	public Marble(float width, float height, int index, float ratio) {
		super();
		INDEX = index;
		screenWidth = width;
		screenHeight = height;
		paint = new Paint();
		this.radius = 8 * ratio;
		srcRect.set(0, 0, 16, 16);
		dstRect.set(0, 0, 16, 16);
		randomPaint();
		SCORE = 200;
		scored = false;
	}
	
	public int setScore(int value) {
		return value;
	}
	
	public void setScoredFlag(boolean value) {
		scored = value;
	}
	
	public boolean hasBeenScored() {
		return scored;
	}
	
	public void setStartingPosition(float x, float y) {
		defaultX = x;
		defaultY = y;
		reset();
	}
	
	@Override
	public void reset() {
		position[0] = defaultX;
		position[1] = defaultY;
		for (int i = 0; i <= 1; i++) {
			speed[i] = 0;
			acceleration[i] = 0;
		}
		atGoal = false;
		inArea = false;
		renderFlag = true;
		this.insideHole = false;
		this.setScoredFlag(false);
		paint.setAlpha(255);
	}
	
	@Override
	public void tick(Stage level) {
		if (bitmap != null) {
			if (atGoal) {
				checkCollisionFlag(level.hole);
				reflect(level.hole);
				reflectResponse();
				fade();
			}
			else {
				if (checkAreaGravity(level.hole)) {
					this.gravityPull(level.hole);
					if (checkCollision(level.hole)) {
						atGoal = true;
					}
				}
				else {
					if (inArea == true) {
						inArea = false;
					}
					if (checkCollision(level.cue)) {
						collisionResponse(level.cue);
						resolveCollision(level.cue);
					}
					for (Marble m : level.marbles) {
						if (this.INDEX != m.INDEX && checkCollision(m)) {
							collisionResponse(m);
							resolveCollision(m);
						}
					}
				}
				if (curvePipe != null)
					reflect(curvePipe);
				else if (funnel != null)
					reflect(funnel);
				else if (ramp != null)
					reflect(ramp);
				else if (pipe != null)
					reflect(pipe);
				else if (connector != null)
					reflect(connector);
				else if (bumper != null) {
					reflect(bumper);
				}
				else
					reflectResponse();
			}
			move();
		}
		else
			bitmap = Art.sprites;
	}
	
	@Override
	public void render(Canvas c, final float cx, final float cy) {
		if (bitmap != null) {
			float xOffset = this.position[0] - RenderView.cameraX + cx;
			float yOffset = this.position[1] - RenderView.cameraY + cy;
			if (RenderView.bounds.contains(xOffset, yOffset)) {
				move(xOffset, yOffset);
				c.drawBitmap(bitmap, srcRect, dstRect, paint);
			}
		}
	}
	
	private void move(float f, float g) {
		if (bitmap != null) {
			dstRect.set(f - this.radius, g - this.radius, f + this.radius, g + this.radius);
		}
	}
	
	public boolean isAtGoal() {
		return atGoal;
	}
	
	public void setRenderFlag(boolean value) {
		renderFlag = value;
	}
	
	private void randomPaint() {
		Random random = new Random();
		ColorFilter filter = new LightingColorFilter(Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255)), Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255)));
		paint.setColorFilter(filter);
	}
	
	private void move() {
		if (position[0] > screenWidth + this.radius)
			position[0] = screenWidth + this.radius;
		if (position[1] > screenHeight + this.radius)
			position[1] = screenHeight + this.radius;
		if (position[0] < -radius)
			position[0] = -radius;
		if (position[1] < -radius)
			position[1] = -radius;
	}
	
	private void wallCollisionResponse() {
		if (position[0] > screenWidth + this.radius)
			speed[0] = -speed[0];
		if (position[1] > screenHeight + this.radius)
			speed[1] = -speed[1];
		for (int i = 0; i <= 1; i++) {
			if (position[i] < -radius)
				speed[i] = -speed[i];
		}
	}
	
	private void fade() {
		int value = paint.getAlpha();
		if (value > 0) {
			value -= 5;
			paint.setAlpha(value);
			if (value < 0) {
				value = 0;
				renderFlag = false;
			}
		}
	}
	
	@Override
	public void reflect(CurvePipe p) {
		
		//TODO: Rewrite this function completely.
		// Split into 5 sections:
		// 			A	OuterRing	B	InnerRing	C
		// A: 			Any balls in this area hits outer ring, use reflection to bounce away.
		// OuterRing:	Any balls on top of outer ring must move back into center, B.
		// B:			Use reflection or something else that allows movement.
		// InnerRing:	Any balls on top of inner ring must move back into center, B.
		// C: 			Any balls in this area hits inner ring, use reflection to bounce away.
		
		// Must declare new OuterRing radius and InnerRing radius.
		
		if (p != null) {
			float multiplier = RenderView.AspectRatio * p.PIPE_SIZE * 2;
			//Comparing all of these regions to ball position. (Test #1)
			//Ball - Center, use "-=" as positive attraction to the center line. "+=" is pushing away from center line.
			float dx = position[0] - p.x;
			float dy = position[1] - p.y;
			float distance = FloatMath.sqrt(dx * dx + dy * dy);
			if (distance > 6 * multiplier) {
				//A
				if (distance - radius < 6 * multiplier) {
					//reflect
					if (distance == 0)
						distance = 1f;
					dx /= distance;
					dy /= distance;
					double dotProduct = this.speed[0] * dx + this.speed[1] * dy;
					this.speed[0] += (float) (-2 * dotProduct * dx);
					this.speed[1] += (float) (-2 * dotProduct * dy);
					reflectResponse();
				}
				else {
					reflectResponse();
				}
			}
			else if (distance <= 6 * multiplier && distance > 4 * multiplier) {
				//B
				if (distance + radius > 6 * multiplier) {
					//reflect
					if (distance == 0)
						distance = 1f;
					dx /= distance;
					dy /= distance;
					double dotProduct = this.speed[0] * dx + this.speed[1] * dy;
					this.speed[0] += (float) (-2 * dotProduct * dx);
					this.speed[1] += (float) (-2 * dotProduct * dy);
					reflectResponse();
				}
				else {
					//pull ball back into center.
					//do something about ball moving counterclockwise or clockwise
					float pipeRadius = p.dstRect.width() / 2;
					float targetX = dx * pipeRadius;
					float targetY = dy * pipeRadius;
					targetX += p.x;
					targetY += p.y;
					targetX = targetX - this.position[0];
					targetY = targetY - this.position[1];
					float avn = FloatMath.sqrt(targetX * targetX + targetY * targetY);
					if (avn == 0)
						avn = 1f;
					targetX /= avn;
					targetY /= avn;
					speed[0] -= targetX;
					speed[1] -= targetY;
					reflectResponse();
				}
			}
			else if (distance <= 4 * multiplier && distance > 2 * multiplier) {
				//C
				if (distance - radius < 2 * multiplier) {
					//reflect
					if (distance == 0)
						distance = 1f;
					dx /= distance;
					dy /= distance;
					double dotProduct = this.speed[0] * dx + this.speed[1] * dy;
					this.speed[0] += (float) (-2 * dotProduct * dx);
					this.speed[1] += (float) (-2 * dotProduct * dy);
					reflectResponse();
				}
				else {
					//push ball back into center.
					//do something about ball moving counterclockwise or clockwise
					float pipeRadius = p.dstRect.width() / 2;
					float targetX = dx * pipeRadius;
					float targetY = dy * pipeRadius;
					targetX += p.x;
					targetY += p.y;
					targetX = targetX - this.position[0];
					targetY = targetY - this.position[1];
					float avn = FloatMath.sqrt(targetX * targetX + targetY * targetY);
					if (avn == 0)
						avn = 1f;
					targetX /= avn;
					targetY /= avn;
					speed[0] -= targetX;
					speed[1] -= targetY;
					reflectResponse();
				}
			}
			else {
				//D
				if (distance + radius > 2 * multiplier) {
					//reflect
					if (distance == 0)
						distance = 1f;
					dx /= distance;
					dy /= distance;
					double dotProduct = this.speed[0] * dx + this.speed[1] * dy;
					this.speed[0] += (float) (-2 * dotProduct * dx);
					this.speed[1] += (float) (-2 * dotProduct * dy);
					reflectResponse();
				}
				else {
					reflectResponse();
				}
			}
			super.reflect(p);
		}
	}
	
	@Override
	public void reflect(Pipe p) {
		if (p != null) {
			float pipeRadius = p.PIPE_SIZE * 3 * RenderView.AspectRatio;
			float newTop = p.dstRect.top + pipeRadius;
			float newBottom = p.dstRect.bottom - pipeRadius;
			float newLeft = p.dstRect.left + pipeRadius;
			float newRight = p.dstRect.right - pipeRadius;
			switch (p.orientation) {
				case HORIZONTAL:
					// Region A:
					if (position[1] - radius < newTop && position[1] + radius > newTop) {
						if (position[1] < newTop) {
							this.speed[1] = -this.speed[1];
							this.position[1] -= this.speed[1] * 10;
							this.speed[1] *= 0.9992f;
						}
						else {
							float dax = (p.dstRect.left + p.dstRect.width() / 2) - position[0];
							float day = (p.dstRect.top + p.dstRect.height() / 2) - position[1];
							float dan = FloatMath.sqrt(dax * dax + day * day);
							if (dan == 0)
								dan = 1f;
							day /= dan;
							this.speed[1] -= day;
							this.position[1] -= this.speed[1];
							this.speed[1] *= 0.1f;
						}
						this.position[0] -= this.speed[0] * 10;
						this.speed[0] *= 0.9992f;
					}
					else if (position[1] + radius > newBottom && position[1] - radius < newBottom) {
						if (position[1] > newBottom) {
							this.speed[1] = -this.speed[1];
							this.position[1] -= this.speed[1] * 10;
							this.speed[1] *= 0.9992f;
						}
						else {
							float dax = (p.dstRect.left + p.dstRect.width() / 2) - position[0];
							float day = (p.dstRect.top + p.dstRect.height() / 2) - position[1];
							float dan = FloatMath.sqrt(dax * dax + day * day);
							if (dan == 0)
								dan = 1f;
							day /= dan;
							this.speed[1] -= day;
							this.position[1] -= this.speed[1];
							this.speed[1] *= 0.1f;
						}
						this.position[0] -= this.speed[0] * 10;
						this.speed[0] *= 0.9992f;
					}
					else if (position[1] < newBottom && position[1] > newTop) {
						// Region B
						float dax = (p.dstRect.left + p.dstRect.width() / 2) - position[0];
						float day = (p.dstRect.top + p.dstRect.height() / 2) - position[1];
						float dan = FloatMath.sqrt(dax * dax + day * day);
						if (dan == 0)
							dan = 1f;
						day /= dan;
						this.speed[1] -= day;
						this.position[0] -= this.speed[0] * 10;
						this.position[1] -= this.speed[1];
						this.speed[0] *= 0.9992f;
						this.speed[1] *= 0.1f;
					}
					else
						reflectResponse();
					break;
				case VERTICAL:
					// Region A:
					if (position[0] - radius < newLeft && position[0] + radius > newLeft) {
						if (position[0] < newLeft) {
							this.speed[0] = -this.speed[0];
							this.position[0] -= this.speed[0] * 10;
							this.speed[0] *= 0.9992f;
						}
						else {
							float dax = (p.dstRect.left + p.dstRect.width() / 2) - position[0];
							float day = (p.dstRect.top + p.dstRect.height() / 2) - position[1];
							float dan = FloatMath.sqrt(dax * dax + day * day);
							if (dan == 0)
								dan = 1f;
							dax /= dan;
							this.speed[0] -= dax;
							this.position[0] -= this.speed[0];
							this.speed[0] *= 0.1f;
						}
						this.position[1] -= this.speed[1] * 10;
						this.speed[1] *= 0.9992f;
					}
					else if (position[0] + radius > newRight && position[0] - radius < newRight) {
						if (position[0] > newRight) {
							this.speed[0] = -this.speed[0];
							this.position[0] -= this.speed[0] * 10;
							this.speed[0] *= 0.9992f;
						}
						else {
							float dax = (p.dstRect.left + p.dstRect.width() / 2) - position[0];
							float day = (p.dstRect.top + p.dstRect.height() / 2) - position[1];
							float dan = FloatMath.sqrt(dax * dax + day * day);
							if (dan == 0)
								dan = 1f;
							dax /= dan;
							this.speed[0] -= dax;
							this.position[0] -= this.speed[0];
							this.speed[0] *= 0.1f;
						}
						this.position[1] -= this.speed[1] * 10;
						this.speed[1] *= 0.9992f;
					}
					else if (position[0] < newRight && position[0] > newLeft) {
						// Region B
						float dax = (p.dstRect.left + p.dstRect.width() / 2) - position[0];
						float day = (p.dstRect.top + p.dstRect.height() / 2) - position[1];
						float dan = FloatMath.sqrt(dax * dax + day * day);
						if (dan == 0)
							dan = 1f;
						dax /= dan;
						this.speed[0] -= dax;
						this.position[1] -= this.speed[1] * 10;
						this.position[0] -= this.speed[0];
						this.speed[1] *= 0.9992f;
						this.speed[0] *= 0.1f;
					}
					else
						reflectResponse();
					break;
				default:
					break;
			}
		}
	}
	
	@Override
	public void reflectResponse() {
		for (int i = 0; i <= 1; i++) {
			position[i] -= speed[i] * 10;
			speed[i] *= 0.9992f;
		}
		wallCollisionResponse();
	}
	
	@Override
	public void reflect(Bumper b) {
		if (b != null) {
			double nx = (this.position[0]) - (b.x);
			double ny = (this.position[1]) - (b.y);
			double nd = Math.hypot(nx, ny);
			if (nd < b.radius + radius) {
				if (nd == 0)
					nd = 1;
				nx /= nd;
				ny /= nd;
				double dotProduct = this.speed[0] * nx + this.speed[1] * ny;
				this.speed[0] += (float) (-3 * dotProduct * nx);
				this.speed[1] += (float) (-3 * dotProduct * ny);
				this.position[0] -= this.speed[0] * 6;
				this.position[1] -= this.speed[1] * 6;
				this.speed[0] *= 0.9992;
				this.speed[1] *= 0.9992;
			}
		}
	}
	
	@Override
	public void reflect(Ramp r) {
		float halfWidth = r.dstRect.width() / 2;
		float halfHeight = r.dstRect.height() / 2;
		float dx1 = position[0] - r.x1;
		float dy1 = position[1] - r.y1;
		float dx2 = position[0] - r.x2;
		float dy2 = position[1] - r.y2;
		float funnelRadius = r.PIPE_SIZE * RenderView.AspectRatio;
		float pipeRadius = 3 * funnelRadius;
		final float f = r.PIPE_SIZE * 12;
		switch (r.direction) {
			case LEFT:
				if (Math.abs(position[0] - r.dstRect.left) > halfWidth) {
					if (funnelDetector == false && this.position[0] > r.dstRect.right - funnelRadius)
						funnelDetector = true;
					
					float dist1 = FloatMath.sqrt(dx1 * dx1 + dy1 * dy1);
					float dist2 = FloatMath.sqrt(dx2 * dx2 + dy2 * dy2);
					if (funnelDetector) {
						if (dist1 < f) {
							funnelFlag1 = true;
						}
						else if (dist2 < f) {
							funnelFlag2 = true;
						}
						else
							reflectResponse();
					}
					else {
						if (dist1 < f) {
							funnelResponse(dx1, dy1, dist1);
						}
						else if (dist2 < f) {
							funnelResponse(dx2, dy2, dist2);
						}
						else
							reflectResponse();
						return;
					}
					if (funnelFlag1)
						funnelResponse(dx1, dy1, dist1);
					if (funnelFlag2)
						funnelResponse(dx2, dy2, dist2);
				}
				else {
					float newTop = r.dstRect.top + pipeRadius;
					float newBottom = r.dstRect.bottom - pipeRadius;
					if (position[1] - radius < newTop && position[1] + radius > newTop) {
						if (position[1] < newTop) {
							this.speed[1] = -this.speed[1];
							this.position[1] -= this.speed[1] * 10;
							this.speed[1] *= 0.9992f;
						}
						else {
							float dax = (r.dstRect.left + r.dstRect.width() / 2) - position[0];
							float day = (r.dstRect.top + r.dstRect.height() / 2) - position[1];
							float dan = FloatMath.sqrt(dax * dax + day * day);
							if (dan == 0)
								dan = 1f;
							day /= dan;
							this.speed[1] -= day;
							this.position[1] -= this.speed[1];
							this.speed[1] *= 0.1f;
						}
						this.position[0] -= this.speed[0] * 10;
						this.speed[0] *= 0.9992f;
					}
					else if (position[1] + radius > newBottom && position[1] - radius < newBottom) {
						if (position[1] > newBottom) {
							this.speed[1] = -this.speed[1];
							this.position[1] -= this.speed[1] * 10;
							this.speed[1] *= 0.9992f;
						}
						else {
							float dax = (r.dstRect.left + r.dstRect.width() / 2) - position[0];
							float day = (r.dstRect.top + r.dstRect.height() / 2) - position[1];
							float dan = FloatMath.sqrt(dax * dax + day * day);
							if (dan == 0)
								dan = 1f;
							day /= dan;
							this.speed[1] -= day;
							this.position[1] -= this.speed[1];
							this.speed[1] *= 0.1f;
						}
						this.position[0] -= this.speed[0] * 10;
						this.speed[0] *= 0.9992f;
					}
					else if (position[1] < newBottom && position[1] > newTop) {
						// Region B
						float dax = (r.dstRect.left + r.dstRect.width() / 2) - position[0];
						float day = (r.dstRect.top + r.dstRect.height() / 2) - position[1];
						float dan = FloatMath.sqrt(dax * dax + day * day);
						if (dan == 0)
							dan = 1f;
						day /= dan;
						this.speed[1] -= day;
						this.position[0] -= this.speed[0] * 10;
						this.position[1] -= this.speed[1];
						this.speed[0] *= 0.9992f;
						this.speed[1] *= 0.1f;
					}
					else
						reflectResponse();
				}
				break;
			case RIGHT:
				if (Math.abs(position[0] - r.dstRect.left) < halfWidth) {
					
					if (funnelDetector == false && this.position[0] < r.dstRect.left + funnelRadius)
						funnelDetector = true;
					float dist1 = FloatMath.sqrt(dx1 * dx1 + dy1 * dy1);
					float dist2 = FloatMath.sqrt(dx2 * dx2 + dy2 * dy2);
					
					if (funnelDetector) {
						if (dist1 < f) {
							funnelFlag1 = true;
						}
						else if (dist2 < f) {
							funnelFlag2 = true;
						}
						else
							reflectResponse();
					}
					else {
						if (dist1 < f) {
							funnelResponse(dx1, dy1, dist1);
						}
						else if (dist2 < f) {
							funnelResponse(dx2, dy2, dist2);
						}
						else
							reflectResponse();
						return;
					}
					if (funnelFlag1)
						funnelResponse(dx1, dy1, dist1);
					if (funnelFlag2)
						funnelResponse(dx2, dy2, dist2);
				}
				else {
					float newTop = r.dstRect.top + pipeRadius;
					float newBottom = r.dstRect.bottom - pipeRadius;
					if (position[1] - radius < newTop && position[1] + radius > newTop) {
						if (position[1] < newTop) {
							this.speed[1] = -this.speed[1];
							this.position[1] -= this.speed[1] * 10;
							this.speed[1] *= 0.9992f;
						}
						else {
							float dax = (r.dstRect.left + r.dstRect.width() / 2) - position[0];
							float day = (r.dstRect.top + r.dstRect.height() / 2) - position[1];
							float dan = FloatMath.sqrt(dax * dax + day * day);
							if (dan == 0)
								dan = 1f;
							day /= dan;
							this.speed[1] -= day;
							this.position[1] -= this.speed[1];
							this.speed[1] *= 0.1f;
						}
						this.position[0] -= this.speed[0] * 10;
						this.speed[0] *= 0.9992f;
					}
					else if (position[1] + radius > newBottom && position[1] - radius < newBottom) {
						if (position[1] > newBottom) {
							this.speed[1] = -this.speed[1];
							this.position[1] -= this.speed[1] * 10;
							this.speed[1] *= 0.9992f;
						}
						else {
							float dax = (r.dstRect.left + r.dstRect.width() / 2) - position[0];
							float day = (r.dstRect.top + r.dstRect.height() / 2) - position[1];
							float dan = FloatMath.sqrt(dax * dax + day * day);
							if (dan == 0)
								dan = 1f;
							day /= dan;
							this.speed[1] -= day;
							this.position[1] -= this.speed[1];
							this.speed[1] *= 0.1f;
						}
						this.position[0] -= this.speed[0] * 10;
						this.speed[0] *= 0.9992f;
					}
					else if (position[1] < newBottom && position[1] > newTop) {
						// Region B
						float dax = (r.dstRect.left + r.dstRect.width() / 2) - position[0];
						float day = (r.dstRect.top + r.dstRect.height() / 2) - position[1];
						float dan = FloatMath.sqrt(dax * dax + day * day);
						if (dan == 0)
							dan = 1f;
						day /= dan;
						this.speed[1] -= day;
						this.position[0] -= this.speed[0] * 10;
						this.position[1] -= this.speed[1];
						this.speed[0] *= 0.9992f;
						this.speed[1] *= 0.1f;
					}
					else
						reflectResponse();
				}
				break;
			case UP:
				if (Math.abs(position[1] - r.dstRect.top) > halfHeight) {
					if (funnelDetector == false && this.position[1] > r.dstRect.top + r.dstRect.height() / 2)
						funnelDetector = true;
					float dist1 = FloatMath.sqrt(dx1 * dx1 + dy1 * dy1);
					float dist2 = FloatMath.sqrt(dx2 * dx2 + dy2 * dy2);
					
					if (funnelDetector) {
						if (dist1 < f) {
							funnelFlag1 = true;
						}
						else if (dist2 < f) {
							funnelFlag2 = true;
						}
						else
							reflectResponse();
					}
					else {
						if (dist1 < f) {
							funnelResponse(dx1, dy1, dist1);
						}
						else if (dist2 < f) {
							funnelResponse(dx2, dy2, dist2);
						}
						else
							reflectResponse();
						return;
					}
					if (funnelFlag1)
						funnelResponse(dx1, dy1, dist1);
					if (funnelFlag2)
						funnelResponse(dx2, dy2, dist2);
				}
				else {
					float newLeft = r.dstRect.left + pipeRadius;
					float newRight = r.dstRect.right - pipeRadius;
					if (position[0] - radius < newLeft && position[0] + radius > newLeft) {
						if (position[0] < newLeft) {
							this.speed[0] = -this.speed[0];
							this.position[0] -= this.speed[0] * 10;
							this.speed[0] *= 0.9992f;
						}
						else {
							float dax = (r.dstRect.left + r.dstRect.width() / 2) - position[0];
							float day = (r.dstRect.top + r.dstRect.height() / 2) - position[1];
							float dan = FloatMath.sqrt(dax * dax + day * day);
							if (dan == 0)
								dan = 1f;
							dax /= dan;
							this.speed[0] -= dax;
							this.position[0] -= this.speed[0];
							this.speed[0] *= 0.1f;
						}
						this.position[1] -= this.speed[1] * 10;
						this.speed[1] *= 0.9992f;
					}
					else if (position[0] + radius > newRight && position[0] - radius < newRight) {
						if (position[0] > newRight) {
							this.speed[0] = -this.speed[0];
							this.position[0] -= this.speed[0] * 10;
							this.speed[0] *= 0.9992f;
						}
						else {
							float dax = (r.dstRect.left + r.dstRect.width() / 2) - position[0];
							float day = (r.dstRect.top + r.dstRect.height() / 2) - position[1];
							float dan = FloatMath.sqrt(dax * dax + day * day);
							if (dan == 0)
								dan = 1f;
							dax /= dan;
							this.speed[0] -= dax;
							this.position[0] -= this.speed[0];
							this.speed[0] *= 0.1f;
						}
						this.position[1] -= this.speed[1] * 10;
						this.speed[1] *= 0.9992f;
					}
					else if (position[0] < newRight && position[0] > newLeft) {
						// Region B
						float dax = (r.dstRect.left + r.dstRect.width() / 2) - position[0];
						float day = (r.dstRect.top + r.dstRect.height() / 2) - position[1];
						float dan = FloatMath.sqrt(dax * dax + day * day);
						if (dan == 0)
							dan = 1f;
						dax /= dan;
						this.speed[0] -= dax;
						this.position[1] -= this.speed[1] * 10;
						this.position[0] -= this.speed[0];
						this.speed[1] *= 0.9992f;
						this.speed[0] *= 0.1f;
					}
					else
						reflectResponse();
				}
				break;
			case DOWN:
				if (Math.abs(position[1] - r.dstRect.top) < halfHeight) {
					if (funnelDetector == false && this.position[1] < r.dstRect.top + r.dstRect.height() / 2)
						funnelDetector = true;
					float dist1 = FloatMath.sqrt(dx1 * dx1 + dy1 * dy1);
					float dist2 = FloatMath.sqrt(dx2 * dx2 + dy2 * dy2);
					
					if (funnelDetector) {
						if (dist1 < f) {
							funnelFlag1 = true;
						}
						else if (dist2 < f) {
							funnelFlag2 = true;
						}
						else
							reflectResponse();
					}
					else {
						if (dist1 < f) {
							funnelResponse(dx1, dy1, dist1);
						}
						else if (dist2 < f) {
							funnelResponse(dx2, dy2, dist2);
						}
						else
							reflectResponse();
						return;
					}
					if (funnelFlag1)
						funnelResponse(dx1, dy1, dist1);
					if (funnelFlag2)
						funnelResponse(dx2, dy2, dist2);
				}
				else {
					float newLeft = r.dstRect.left + pipeRadius;
					float newRight = r.dstRect.right - pipeRadius;
					if (position[0] - radius < newLeft && position[0] + radius > newLeft) {
						if (position[0] < newLeft) {
							this.speed[0] = -this.speed[0];
							this.position[0] -= this.speed[0] * 10;
							this.speed[0] *= 0.9992f;
						}
						else {
							float dax = (r.dstRect.left + r.dstRect.width() / 2) - position[0];
							float day = (r.dstRect.top + r.dstRect.height() / 2) - position[1];
							float dan = FloatMath.sqrt(dax * dax + day * day);
							if (dan == 0)
								dan = 1f;
							dax /= dan;
							this.speed[0] -= dax;
							this.position[0] -= this.speed[0];
							this.speed[0] *= 0.1f;
						}
						this.position[1] -= this.speed[1] * 10;
						this.speed[1] *= 0.9992f;
					}
					else if (position[0] + radius > newRight && position[0] - radius < newRight) {
						if (position[0] > newRight) {
							this.speed[0] = -this.speed[0];
							this.position[0] -= this.speed[0] * 10;
							this.speed[0] *= 0.9992f;
						}
						else {
							float dax = (r.dstRect.left + r.dstRect.width() / 2) - position[0];
							float day = (r.dstRect.top + r.dstRect.height() / 2) - position[1];
							float dan = FloatMath.sqrt(dax * dax + day * day);
							if (dan == 0)
								dan = 1f;
							dax /= dan;
							this.speed[0] -= dax;
							this.position[0] -= this.speed[0];
							this.speed[0] *= 0.1f;
						}
						this.position[1] -= this.speed[1] * 10;
						this.speed[1] *= 0.9992f;
					}
					else if (position[0] < newRight && position[0] > newLeft) {
						// Region B
						float dax = (r.dstRect.left + r.dstRect.width() / 2) - position[0];
						float day = (r.dstRect.top + r.dstRect.height() / 2) - position[1];
						float dan = FloatMath.sqrt(dax * dax + day * day);
						if (dan == 0)
							dan = 1f;
						dax /= dan;
						this.speed[0] -= dax;
						this.position[1] -= this.speed[1] * 10;
						this.position[0] -= this.speed[0];
						this.speed[1] *= 0.9992f;
						this.speed[0] *= 0.1f;
					}
					else
						reflectResponse();
				}
				break;
		}
	}
}
