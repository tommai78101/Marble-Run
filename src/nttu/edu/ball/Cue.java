package nttu.edu.ball;

import nttu.edu.entity.Bumper;
import nttu.edu.entity.Connector;
import nttu.edu.entity.CurvePipe;
import nttu.edu.entity.Funnel;
import nttu.edu.entity.Pipe;
import nttu.edu.entity.Ramp;
import nttu.edu.entity.Tee;
import nttu.edu.graphics.Art;
import nttu.edu.graphics.RenderView;
import nttu.edu.handler.Accelero;
import nttu.edu.level.Stage;
import nttu.edu.sound.Sound;
import android.graphics.Canvas;
import android.util.FloatMath;
import android.util.Log;

public class Cue extends Ball {
	//TODO: Refactor same codes into functions, then reuse the functions.
	//TODO: Partial rewrite... D: (Gamedev.net recommended action.)
	private boolean goalFlag;
	private final float[] defaults = new float[3];
	
	// Jumping variables
	private boolean jumping;
	private float radians = 0f;
	private int multiplier = 0;
	private int radianSpeed = 1;
	private boolean jumpingSound = false;
	
	// Death variables
	private boolean death;
	private float deathSpeed = 0f;
	// private int lives;
	
	public Void voidTile;
	
	// Score
	public final int SCORE;
	
	public Cue(float w, float h, float ratio) {
		super();
		this.screenWidth = w;
		this.screenHeight = h;
		this.radius = 8 * ratio;
		this.bitmap = Art.sprites;
		this.jumping = false;
		this.goalFlag = false;
		this.srcRect.set(0, 0, 16, 16);
		this.dstRect.set(0, 0, 16, 16);
		// -------------
		this.death = false;
		// this.lives = 3;
		// -------------
		SCORE = 0;
	}
	
	public int setScore(int value) {
		return value;
	}
	
	@Override
	public void tick(Stage s) {
		this.setAcceleration(Accelero.X, Accelero.Y, Accelero.Z);
		if (bitmap != null) {
			if (this.checkCollision(s.hole) || goalFlag) {
				if (!jumping) {
					goalFlag = true;
					die();
					reflect(s.hole);
					move();
					position[0] -= speed[0];
					position[1] -= speed[1];
					speed[0] *= 0.6f;
					speed[1] *= 0.6f;
					RenderView.cameraX = position[0];
					RenderView.cameraY = position[1];
					
				}
				else {
					reflectResponse();
					move();
					jump();
					if (position[2] <= 0f) {
						jumpingSound = false;
						jumping = false;
					}
				}
				
			}
			else {
				if (acceleration[2] < 0.0) {
					multiplier = 8;
					jumping = true;
					if (jumpingSound == false)
						jumpingSound = true;
				}
				if (!death) {
					if (!jumping) {
						// Moving - Start
						if (!goalFlag) {
							for (Marble m : s.marbles) {
								if (this.checkCollision(m)) {
									this.collisionResponse(m);
								}
							}
						}
						if (pipe != null)
							reflect(pipe);
						else if (funnel != null)
							reflect(funnel);
						else if (ramp != null)
							reflect(ramp);
						else if (connector != null)
							reflect(connector);
						else if (curvePipe != null)
							reflect(curvePipe);
						else if (bumper != null)
							reflect(bumper);
						else
							reflectResponse();
						move();
						// Moving - End
						
					}
					else {
						// Jumping - Start
						if (jumpingSound) {
							Sound.play(Sound.jumpID);
							jumpingSound = false;
						}
						reflectResponse();
						move();
						jump();
						// Jumping - End
						if (bumper != null)
							reflect(bumper);
					}
				}
				else {
					
					deathAnimation();
				}
				
			}
		}
		else
			bitmap = Art.sprites;
	}
	
	@Override
	public void render(Canvas c, final float centerX, final float centerY) {
		if (bitmap != null) {
			float xOffset = this.position[0] - RenderView.cameraX;
			float yOffset = this.position[1] - RenderView.cameraY;
			move(centerX + xOffset, centerY + yOffset);
			c.drawBitmap(bitmap, srcRect, dstRect, null);
		}
	}
	
	private void move(float f, float g) {
		if (bitmap == null)
			return;
		dstRect.set(f - this.radius, g - this.radius, f + this.radius, g + this.radius);
		if (jumping || death) {
			dstRect.left -= position[2] * RenderView.AspectRatio;
			dstRect.right += position[2] * RenderView.AspectRatio;
			dstRect.top -= position[2] * RenderView.AspectRatio;
			dstRect.bottom += position[2] * RenderView.AspectRatio;
		}
	}
	
	public void setStartingPosition(Tee tee) {
		this.setPosition(tee.x, tee.y, 0f);
		for (int i = 0; i < 3; i++) {
			defaults[i] = position[i];
		}
		move();
		reset();
	}
	
	public void setGoalFlag(boolean value) {
		goalFlag = value;
	}
	
	public boolean hasReachedGoal() {
		return goalFlag;
	}
	
	@Override
	public void reset() {
		for (int i = 0; i < 3; i++) {
			position[i] = defaults[i];
		}
		jumping = false;
		goalFlag = false;
		this.insideHole = false;
		RenderView.cameraX = defaults[0];
		RenderView.cameraY = defaults[1];
		this.deathSpeed = 0f;
		this.death = false;
	}
	
	@Override
	public void reflectResponse() {
		for (int i = 0; i <= 1; i++) {
			speed[i] += acceleration[i];
			position[i] -= speed[i];
			speed[i] *= 0.1f;
		}
		RenderView.cameraX = position[0];
		RenderView.cameraY = position[1];
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
	
	private void jump() {
		float pi = (float) Math.PI;
		position[2] = FloatMath.cos((radians + pi)) * multiplier + multiplier;
		radians += 0.2f * radianSpeed;
		if (radians > 2 * Math.PI) {
			if (death) {
				jumping = false;
				return;
			}
			radianSpeed += 2;
			radians = 0f;
			multiplier -= 1;
			if (multiplier <= 0) {
				jumping = false;
				multiplier = 8;
				radians = 0f;
				radianSpeed = 1;
				position[2] = 0f;
			}
		}
	}
	
	@Override
	public void reflect(CurvePipe p) {
		if (p != null) {
			
			float thickness = p.PIPE_SIZE * 3 * RenderView.AspectRatio + radius * 2;
			float thinness = p.PIPE_SIZE * 5;
			
			float vx = this.position[0] - p.x;
			float vy = this.position[1] - p.y;
			float vn = FloatMath.sqrt(vx * vx + vy * vy);
			if (vn == 0)
				vn = 1f;
			vx /= vn;
			vy /= vn;
			
			float pipeRadius = p.dstRect.width() / 2;
			float targetX = vx * pipeRadius;
			float targetY = vy * pipeRadius;
			targetX += p.x;
			targetY += p.y;
			float avx = targetX - this.position[0];
			float avy = targetY - this.position[1];
			float avn = FloatMath.sqrt(avx * avx + avy * avy);
			if (avn == 0)
				avn = 1f;
			avx /= avn;
			avy /= avn;
			
			if (vn - radius > thinness && vn < thickness) {
				//Region B
				//Inside Boundary
				if (Math.abs(targetX - position[0]) > 1f)
					acceleration[0] -= avx;
				if (Math.abs(targetY - position[1]) > 1f)
					acceleration[1] -= avy;
				reflectResponse();
			}
			else if (vn >= thickness) {
				//Region A
				//Outside boundary
				if (vn - radius < thickness) {
					double dotProduct = this.speed[0] * vx + this.speed[1] * vy;
					this.speed[0] += (float) (-2 * dotProduct * vx);
					this.speed[1] += (float) (-2 * dotProduct * vy);
					this.position[0] -= this.speed[0] * 10;
					this.position[1] -= this.speed[1] * 10;
					
				}
				else {
					reflectResponse();
				}
			}
			else if (vn >= thinness) {
				//Region A
				
				if (vn - radius < thinness) {
					if (vn - radius < 0) {
						double dotProduct = this.speed[0] * vx + this.speed[1] * vy;
						this.speed[0] += (float) (-2 * dotProduct * vx);
						this.speed[1] += (float) (-2 * dotProduct * vy);
						this.position[0] -= this.speed[0] * 10;
						this.position[1] -= this.speed[1] * 10;
					}
					else {
						acceleration[0] -= avx * 2;
						acceleration[1] -= avy * 2;
						reflectResponse();
					}
					
				}
				else {
					acceleration[0] -= avx * 2;
					acceleration[1] -= avy * 2;
					reflectResponse();
				}
			}
			else if (vn < thinness) {
				float dx = p.x - position[0];
				float dy = p.y - position[1];
				if (dx >= 0) {
					if (dy >= 0) {
						if (acceleration[0] >= 0) {
							speed[0] += -2 * speed[0];
							if (acceleration[1] >= 0) {
								speed[1] += -2 * speed[1];
							}
							else {
								this.speed[1] += this.acceleration[1];
								this.position[1] -= this.speed[1];
								this.speed[1] *= 0.1f;
							}
						}
						else {
							if (acceleration[1] >= 0) {
								speed[1] += -2 * speed[1];
							}
							else {
								this.speed[1] += this.acceleration[1];
								this.position[1] -= this.speed[1];
								this.speed[1] *= 0.1f;
							}
							this.speed[0] += this.acceleration[0];
							this.position[0] -= this.speed[0];
							this.speed[0] *= 0.1f;
						}
					}
					else {
						if (acceleration[0] >= 0) {
							speed[0] += -2 * speed[0];
							if (acceleration[1] <= 0) {
								speed[1] += -2 * speed[1];
							}
							else {
								this.speed[1] += this.acceleration[1];
								this.position[1] -= this.speed[1];
								this.speed[1] *= 0.1f;
							}
						}
						else {
							if (acceleration[1] <= 0) {
								speed[1] += -2 * speed[1];
							}
							else {
								this.speed[1] += this.acceleration[1];
								this.position[1] -= this.speed[1];
								this.speed[1] *= 0.1f;
							}
							this.speed[0] += this.acceleration[0];
							this.position[0] -= this.speed[0];
							this.speed[0] *= 0.1f;
						}
					}
				}
				else {
					if (dy >= 0) {
						if (acceleration[0] <= 0) {
							speed[0] += -2 * speed[0];
							if (acceleration[1] >= 0) {
								speed[1] += -2 * speed[1];
							}
							else {
								this.speed[1] += this.acceleration[1];
								this.position[1] -= this.speed[1];
								this.speed[1] *= 0.1f;
							}
						}
						else {
							if (acceleration[1] >= 0) {
								speed[1] += -2 * speed[1];
							}
							else {
								this.speed[1] += this.acceleration[1];
								this.position[1] -= this.speed[1];
								this.speed[1] *= 0.1f;
							}
							this.speed[0] += this.acceleration[0];
							this.position[0] -= this.speed[0];
							this.speed[0] *= 0.1f;
						}
					}
					else {
						if (acceleration[0] <= 0) {
							speed[0] += -2 * speed[0];
							if (acceleration[1] <= 0) {
								speed[1] += -2 * speed[1];
							}
							else {
								this.speed[1] += this.acceleration[1];
								this.position[1] -= this.speed[1];
								this.speed[1] *= 0.1f;
							}
						}
						else {
							if (acceleration[1] <= 0) {
								speed[1] += -2 * speed[1];
							}
							else {
								this.speed[1] += this.acceleration[1];
								this.position[1] -= this.speed[1];
								this.speed[1] *= 0.1f;
							}
							this.speed[0] += this.acceleration[0];
							this.position[0] -= this.speed[0];
							this.speed[0] *= 0.1f;
						}
					}
				}
				
			}
			else
				reflectResponse();
			RenderView.cameraX = this.position[0];
			RenderView.cameraY = this.position[1];
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
					if (position[1] + radius > newTop && position[1] - radius < newTop) {
						if (position[1] < newTop) {
							if (acceleration[1] > 0) {
								this.speed[1] += this.acceleration[1];
								this.position[1] -= this.speed[1];
								this.speed[1] *= 0.1f;
							}
							else
								this.speed[1] += -2 * this.speed[1];
							this.speed[0] += this.acceleration[0];
							this.position[0] -= this.speed[0];
							this.speed[0] *= 0.1f;
							RenderView.cameraX = this.position[0];
							RenderView.cameraY = this.position[1];
						}
						else {
							if (acceleration[1] > 0) {
								this.speed[1] += -2 * this.speed[1];
							}
							else {
								float dax = (p.dstRect.left + p.dstRect.width() / 2) - position[0];
								float day = (p.dstRect.top + p.dstRect.height() / 2) - position[1];
								float dan = FloatMath.sqrt(dax * dax + day * day);
								if (dan == 0)
									dan = 1f;
								day /= dan;
								this.acceleration[1] -= day;
								this.speed[1] += this.acceleration[1];
								this.position[1] -= this.speed[1];
								this.speed[1] *= 0.1f;
							}
							this.speed[0] += this.acceleration[0];
							this.position[0] -= this.speed[0];
							this.speed[0] *= 0.1f;
							RenderView.cameraX = this.position[0];
							RenderView.cameraY = this.position[1];
						}
					}
					else if (position[1] - radius < newBottom && position[1] + radius > newBottom) {
						if (position[1] > newBottom) {
							if (acceleration[1] < 0) {
								this.speed[1] += this.acceleration[1];
								this.position[1] -= this.speed[1];
								this.speed[1] *= 0.1f;
							}
							else
								this.speed[1] += -2 * this.speed[1];
							this.speed[0] += this.acceleration[0];
							this.position[0] -= this.speed[0];
							this.speed[0] *= 0.1f;
							RenderView.cameraX = this.position[0];
							RenderView.cameraY = this.position[1];
						}
						else {
							if (acceleration[1] < 0) {
								this.speed[1] += -2 * this.speed[1];
							}
							else {
								float dax = (p.dstRect.left + p.dstRect.width() / 2) - position[0];
								float day = (p.dstRect.top + p.dstRect.height() / 2) - position[1];
								float dan = FloatMath.sqrt(dax * dax + day * day);
								if (dan == 0)
									dan = 1f;
								day /= dan;
								this.acceleration[1] -= day;
								this.speed[1] += this.acceleration[1];
								this.position[1] -= this.speed[1];
								this.speed[1] *= 0.1f;
							}
							this.speed[0] += this.acceleration[0];
							this.position[0] -= this.speed[0];
							this.speed[0] *= 0.1f;
							RenderView.cameraX = this.position[0];
							RenderView.cameraY = this.position[1];
						}
					}
					else if (position[1] + radius < newBottom && position[1] - radius > newTop) {
						// Region B
						float dax = (p.dstRect.left + p.dstRect.width() / 2) - position[0];
						float day = (p.dstRect.top + p.dstRect.height() / 2) - position[1];
						float dan = FloatMath.sqrt(dax * dax + day * day);
						if (dan == 0)
							dan = 1f;
						day /= dan;
						this.acceleration[1] -= day;
						reflectResponse();
					}
					else
						reflectResponse();
					break;
				case VERTICAL:
					// Region A:
					if (position[0] + radius > newLeft && position[0] - radius < newLeft) {
						if (position[0] < newLeft) {
							if (acceleration[0] > 0) {
								this.speed[0] += this.acceleration[0];
								this.position[0] -= this.speed[0];
								this.speed[0] *= 0.1f;
							}
							else
								this.speed[0] += -2 * this.speed[0];
							this.speed[1] += this.acceleration[1];
							this.position[1] -= this.speed[1];
							this.speed[1] *= 0.1f;
							RenderView.cameraX = this.position[0];
							RenderView.cameraY = this.position[1];
						}
						else {
							if (acceleration[0] > 0) {
								this.speed[0] += -2 * this.speed[0];
							}
							else {
								float dax = (p.dstRect.left + p.dstRect.width() / 2) - position[0];
								float day = (p.dstRect.top + p.dstRect.height() / 2) - position[1];
								float dan = FloatMath.sqrt(dax * dax + day * day);
								if (dan == 0)
									dan = 1f;
								dax /= dan;
								this.acceleration[0] -= dax;
								this.speed[0] += this.acceleration[0];
								this.position[0] -= this.speed[0];
								this.speed[0] *= 0.1f;
							}
							this.speed[1] += this.acceleration[1];
							this.position[1] -= this.speed[1];
							this.speed[1] *= 0.1f;
							RenderView.cameraX = this.position[0];
							RenderView.cameraY = this.position[1];
						}
					}
					else if (position[0] - radius < newRight && position[0] + radius > newRight) {
						if (position[0] > newRight) {
							if (acceleration[0] < 0) {
								this.speed[0] += this.acceleration[0];
								this.position[0] -= this.speed[0];
								this.speed[0] *= 0.1f;
							}
							else
								this.speed[0] += -2 * this.speed[0];
							this.speed[1] += this.acceleration[1];
							this.position[1] -= this.speed[1];
							this.speed[1] *= 0.1f;
							RenderView.cameraX = this.position[0];
							RenderView.cameraY = this.position[1];
						}
						else {
							if (acceleration[0] < 0) {
								this.speed[0] += -2 * this.speed[0];
							}
							else {
								float dax = (p.dstRect.left + p.dstRect.width() / 2) - position[0];
								float day = (p.dstRect.top + p.dstRect.height() / 2) - position[1];
								float dan = FloatMath.sqrt(dax * dax + day * day);
								if (dan == 0)
									dan = 1f;
								dax /= dan;
								this.acceleration[0] -= dax;
								this.speed[0] += this.acceleration[0];
								this.position[0] -= this.speed[0];
								this.speed[0] *= 0.1f;
							}
							this.speed[1] += this.acceleration[1];
							this.position[1] -= this.speed[1];
							this.speed[1] *= 0.1f;
							RenderView.cameraX = this.position[0];
							RenderView.cameraY = this.position[1];
						}
					}
					else if (position[0] + radius < newRight && position[0] - radius > newLeft) {
						// Region B
						float dax = (p.dstRect.left + p.dstRect.width() / 2) - position[0];
						float day = (p.dstRect.top + p.dstRect.height() / 2) - position[1];
						float dan = FloatMath.sqrt(dax * dax + day * day);
						if (dan == 0)
							dan = 1f;
						dax /= dan;
						this.acceleration[0] -= dax;
						reflectResponse();
					}
					else
						reflectResponse();
					break;
				default:
					break;
			}
		}
	}
	
	public boolean isDead() {
		return death || (position[2] <= -radius);
	}
	
	public void die() {
		if (!death) {
			// lives--;
			death = true;
		}
		if (jumping && radians > 2 * Math.PI) {
			jumping = false;
		}
	}
	
	public void revive() {
		if (death) {
			death = false;
		}
	}
	
	public void deathAnimation() {
		for (int i = 0; i <= 1; i++) {
			position[i] -= speed[i];
			speed[i] *= 0.8f;
		}
		if (position[2] > -radius) {
			position[2] -= 0.1f * deathSpeed;
			deathSpeed += 0.4f;
			if (jumping) {
				jumping = false;
				multiplier = 8;
				radians = 0f;
				radianSpeed = 1;
			}
		}
	}
	
	@Override
	public void reflect(Bumper b) {
		if (b != null) {
			
			if (position[2] <= 0) {
				jumping = false;
				multiplier = 8;
				radians = 0f;
				radianSpeed = 1;
				position[2] = 0f;
			}
			if (jumping) {
				reflectResponse();
			}
			else {
				float dx, dy, dd;
				while (true) {
					dx = position[0] - b.x;
					dy = position[1] - b.y;
					dd = FloatMath.sqrt(dx * dx + dy * dy);
					if (dd <= this.radius + b.radius) {
						if (dd == 0)
							dd = 1;
						dx /= dd;
						dy /= dd;
						position[0] += dx;
						position[1] += dy;
						
						RenderView.cameraX = this.position[0];
						RenderView.cameraY = this.position[1];
					}
					else
						break;
				}
				Log.d("Cue", "It worked...");
			}
			
			RenderView.cameraX = this.position[0];
			RenderView.cameraY = this.position[1];
		}
	}
	
	@Override
	public void reflect(Funnel f) {
		super.reflect(f);
		RenderView.cameraX = this.position[0];
		RenderView.cameraY = this.position[1];
	}
	
	@Override
	public void reflect(Connector c) {
		//aaa
		//FIXME: Need to work on this. Ball needs to have circle->square collision. This is for Square.
		//if (position[0] < c.dstRect.right + radius && position[0] + radius > c.dstRect.right) {
		if (position[0] - radius <= c.dstRect.right && position[0] + radius > c.dstRect.right) {
			if (acceleration[0] > 0)
				speed[0] += -2 * speed[0];
			else {
				speed[0] += acceleration[0];
				position[0] -= speed[0];
				speed[0] *= 0.1f;
			}
			speed[1] += acceleration[1];
			position[1] -= speed[1];
			speed[1] *= 0.1f;
		}
		if (position[0] + radius >= c.dstRect.left && position[0] - radius < c.dstRect.left) {
			if (acceleration[0] < 0)
				speed[0] += -2 * speed[0];
			else {
				speed[0] += acceleration[0];
				position[0] -= speed[0];
				speed[0] *= 0.1f;
			}
			speed[1] += acceleration[1];
			position[1] -= speed[1];
			speed[1] *= 0.1f;
		}
		if (position[1] - radius <= c.dstRect.bottom && position[1] + radius > c.dstRect.bottom) {
			if (acceleration[1] > 0)
				speed[1] += -2 * speed[1];
			else {
				speed[1] += acceleration[1];
				position[1] -= speed[1];
				speed[1] *= 0.1f;
			}
			speed[0] += acceleration[0];
			position[0] -= speed[0];
			speed[0] *= 0.1f;
		}
		if (position[1] + radius >= c.dstRect.top && position[1] - radius < c.dstRect.top) {
			if (acceleration[1] < 0)
				speed[1] += -2 * speed[1];
			else {
				speed[1] += acceleration[1];
				position[1] -= speed[1];
				speed[1] *= 0.1f;
			}
			speed[0] += acceleration[0];
			position[0] -= speed[0];
			speed[0] *= 0.1f;
		}
		
		RenderView.cameraX = this.position[0];
		RenderView.cameraY = this.position[1];
	}
	
	public void reflect2(Connector c) {
		if (lineIntersect(this, c.dstRect.left, c.dstRect.top, c.dstRect.left, c.dstRect.bottom)) {
			Log.d("Connector", "From Left");
			if (acceleration[0] < 0)
				speed[0] += -2 * speed[0];
			else {
				speed[0] += acceleration[0];
				position[0] -= speed[0];
				speed[0] *= 0.1f;
			}
			speed[1] += acceleration[1];
			position[1] -= speed[1];
			speed[1] *= 0.1f;
		}
		if (lineIntersect(this, c.dstRect.right, c.dstRect.top, c.dstRect.right, c.dstRect.bottom)) {
			Log.d("Connector", "From Right");
			reflectResponse();
		}
		if (lineIntersect(this, c.dstRect.left, c.dstRect.top, c.dstRect.right, c.dstRect.top)) {
			Log.d("Connector", "From Up");
			reflectResponse();
		}
		if (lineIntersect(this, c.dstRect.left, c.dstRect.bottom, c.dstRect.right, c.dstRect.bottom)) {
			Log.d("Connector", "From Down");
			reflectResponse();
		}
		
		RenderView.cameraX = this.position[0];
		RenderView.cameraY = this.position[1];
		
	}
	
	private boolean lineIntersect(Ball b, float x1, float y1, float x2, float y2) {
		float acx = b.position[0] - x1;
		float acy = b.position[1] - y1;
		float abx = x2 - x1;
		float aby = y2 - y1;
		float scalar = acx * abx + acy * aby;
		float dx = scalar * abx + x1;
		float dy = scalar * aby + y1;
		float cdx = dx - b.position[0];
		float cdy = dy - b.position[1];
		float dist = FloatMath.sqrt(cdx * cdx + cdy * cdy);
		return dist < b.radius;
	}
	
	@Override
	public void reflect(Ramp r) {
		float halfWidth = r.dstRect.width() / 2;
		float halfHeight = r.dstRect.height() / 2;
		float dx1 = position[0] - r.x1;
		float dx2 = position[0] - r.x2;
		float dy1 = position[1] - r.y1;
		float dy2 = position[1] - r.y2;
		float d1 = FloatMath.sqrt(dx1 * dx1 + dy1 * dy1);
		float d2 = FloatMath.sqrt(dx2 * dx2 + dy2 * dy2);
		float pipeRadius = 3 * r.PIPE_SIZE * RenderView.AspectRatio;
		switch (r.direction) {
			case LEFT:
				if (Math.abs(position[0] - r.dstRect.left) > halfWidth) {
					//Region A
					//Curve Region
					if (d1 < pipeRadius + radius) {
						if (d1 == 0)
							d1 = 1;
						dx1 /= d1;
						dy1 /= d1;
						float dotProduct = (speed[0] * dx1 + speed[1] * dy1);
						speed[0] += (-2 * dotProduct * dx1);
						speed[1] += (-2 * dotProduct * dy1);
						position[0] -= speed[0] * 10;
						position[1] -= speed[1] * 10;
					}
					else if (d2 < pipeRadius + radius) {
						if (d2 == 0)
							d2 = 1;
						dx2 /= d2;
						dy2 /= d2;
						float dotProduct = (speed[0] * dx2 + speed[1] * dy2);
						speed[0] += (-2 * dotProduct * dx2);
						speed[1] += (-2 * dotProduct * dy2);
						position[0] -= speed[0] * 10;
						position[1] -= speed[1] * 10;
						
					}
					else {
						//Non-curve Region
						reflectResponse();
					}
				}
				else if (Math.abs(position[0] - r.dstRect.left) < halfWidth) {
					//Region B
					//MUST NOT ALLOW CUE TO GO ON PIPE AT ALL. CANNOT ALLOW CUE TO MOVE UP/DOWN WHEN
					//INSIDE RAMP ONLY!
					
					//Split into 5 groups of Pipe.
					float newTop = r.dstRect.top + pipeRadius;
					float newBottom = r.dstRect.bottom - pipeRadius;
					if (position[1] + radius > newTop && position[1] - radius < newTop) {
						if (position[1] < newTop) {
							if (acceleration[1] > 0) {
								this.speed[1] += this.acceleration[1];
								this.position[1] -= this.speed[1];
								this.speed[1] *= 0.1f;
							}
							else
								this.speed[1] += -2 * this.speed[1];
							this.speed[0] += this.acceleration[0];
							this.position[0] -= this.speed[0];
							this.speed[0] *= 0.1f;
							RenderView.cameraX = this.position[0];
							RenderView.cameraY = this.position[1];
						}
						else {
							if (acceleration[1] > 0) {
								this.speed[1] += -2 * this.speed[1];
							}
							else {
								float dax = (r.dstRect.left + r.dstRect.width() / 2) - position[0];
								float day = (r.dstRect.top + r.dstRect.height() / 2) - position[1];
								float dan = FloatMath.sqrt(dax * dax + day * day);
								if (dan == 0)
									dan = 1f;
								day /= dan;
								this.acceleration[1] -= day;
								this.speed[1] += this.acceleration[1];
								this.position[1] -= this.speed[1];
								this.speed[1] *= 0.1f;
							}
							this.speed[0] += this.acceleration[0];
							this.position[0] -= this.speed[0];
							this.speed[0] *= 0.1f;
							RenderView.cameraX = this.position[0];
							RenderView.cameraY = this.position[1];
						}
					}
					else if (position[1] - radius < newBottom && position[1] + radius > newBottom) {
						if (position[1] > newBottom) {
							if (acceleration[1] < 0) {
								this.speed[1] += this.acceleration[1];
								this.position[1] -= this.speed[1];
								this.speed[1] *= 0.1f;
							}
							else
								this.speed[1] += -2 * this.speed[1];
							this.speed[0] += this.acceleration[0];
							this.position[0] -= this.speed[0];
							this.speed[0] *= 0.1f;
							RenderView.cameraX = this.position[0];
							RenderView.cameraY = this.position[1];
						}
						else {
							if (acceleration[1] < 0) {
								this.speed[1] += -2 * this.speed[1];
							}
							else {
								float dax = (r.dstRect.left + r.dstRect.width() / 2) - position[0];
								float day = (r.dstRect.top + r.dstRect.height() / 2) - position[1];
								float dan = FloatMath.sqrt(dax * dax + day * day);
								if (dan == 0)
									dan = 1f;
								day /= dan;
								this.acceleration[1] -= day;
								this.speed[1] += this.acceleration[1];
								this.position[1] -= this.speed[1];
								this.speed[1] *= 0.1f;
							}
							this.speed[0] += this.acceleration[0];
							this.position[0] -= this.speed[0];
							this.speed[0] *= 0.1f;
							RenderView.cameraX = this.position[0];
							RenderView.cameraY = this.position[1];
						}
					}
					else if (position[1] + radius < newBottom && position[1] - radius > newTop) {
						// Region B
						float dax = (r.dstRect.left + r.dstRect.width() / 2) - position[0];
						float day = (r.dstRect.top + r.dstRect.height() / 2) - position[1];
						float dan = FloatMath.sqrt(dax * dax + day * day);
						if (dan == 0)
							dan = 1f;
						day /= dan;
						this.acceleration[1] -= day;
						reflectResponse();
					}
				}
				else
					//Catch everything else.
					reflectResponse();
				break;
			case RIGHT:
				if (Math.abs(position[0] - r.dstRect.left) < halfWidth) {
					//Region A
					//Curve Region
					if (d1 < pipeRadius + radius) {
						if (d1 == 0)
							d1 = 1;
						dx1 /= d1;
						dy1 /= d1;
						float dotProduct = (speed[0] * dx1 + speed[1] * dy1);
						speed[0] += (-2 * dotProduct * dx1);
						speed[1] += (-2 * dotProduct * dy1);
						position[0] -= speed[0] * 10;
						position[1] -= speed[1] * 10;
					}
					else if (d2 < pipeRadius + radius) {
						if (d2 == 0)
							d2 = 1;
						dx2 /= d2;
						dy2 /= d2;
						float dotProduct = (speed[0] * dx2 + speed[1] * dy2);
						speed[0] += (-2 * dotProduct * dx2);
						speed[1] += (-2 * dotProduct * dy2);
						position[0] -= speed[0] * 10;
						position[1] -= speed[1] * 10;
						
					}
					else {
						//Non-curve Region
						reflectResponse();
					}
				}
				else if (Math.abs(position[0] - r.dstRect.left) > halfWidth) {
					//Region B
					//MUST NOT ALLOW CUE TO GO ON PIPE AT ALL. CANNOT ALLOW CUE TO MOVE UP/DOWN WHEN
					//INSIDE RAMP ONLY!
					
					//Split into 5 groups of Pipe.
					float newTop = r.dstRect.top + pipeRadius;
					float newBottom = r.dstRect.bottom - pipeRadius;
					if (position[1] + radius > newTop && position[1] - radius < newTop) {
						if (position[1] < newTop) {
							if (acceleration[1] > 0) {
								this.speed[1] += this.acceleration[1];
								this.position[1] -= this.speed[1];
								this.speed[1] *= 0.1f;
							}
							else
								this.speed[1] += -2 * this.speed[1];
							this.speed[0] += this.acceleration[0];
							this.position[0] -= this.speed[0];
							this.speed[0] *= 0.1f;
							RenderView.cameraX = this.position[0];
							RenderView.cameraY = this.position[1];
						}
						else {
							if (acceleration[1] > 0) {
								this.speed[1] += -2 * this.speed[1];
							}
							else {
								float dax = (r.dstRect.left + r.dstRect.width() / 2) - position[0];
								float day = (r.dstRect.top + r.dstRect.height() / 2) - position[1];
								float dan = FloatMath.sqrt(dax * dax + day * day);
								if (dan == 0)
									dan = 1f;
								day /= dan;
								this.acceleration[1] -= day;
								this.speed[1] += this.acceleration[1];
								this.position[1] -= this.speed[1];
								this.speed[1] *= 0.1f;
							}
							this.speed[0] += this.acceleration[0];
							this.position[0] -= this.speed[0];
							this.speed[0] *= 0.1f;
							RenderView.cameraX = this.position[0];
							RenderView.cameraY = this.position[1];
						}
					}
					else if (position[1] - radius < newBottom && position[1] + radius > newBottom) {
						if (position[1] > newBottom) {
							if (acceleration[1] < 0) {
								this.speed[1] += this.acceleration[1];
								this.position[1] -= this.speed[1];
								this.speed[1] *= 0.1f;
							}
							else
								this.speed[1] += -2 * this.speed[1];
							this.speed[0] += this.acceleration[0];
							this.position[0] -= this.speed[0];
							this.speed[0] *= 0.1f;
							RenderView.cameraX = this.position[0];
							RenderView.cameraY = this.position[1];
						}
						else {
							if (acceleration[1] < 0) {
								this.speed[1] += -2 * this.speed[1];
							}
							else {
								float dax = (r.dstRect.left + r.dstRect.width() / 2) - position[0];
								float day = (r.dstRect.top + r.dstRect.height() / 2) - position[1];
								float dan = FloatMath.sqrt(dax * dax + day * day);
								if (dan == 0)
									dan = 1f;
								day /= dan;
								this.acceleration[1] -= day;
								this.speed[1] += this.acceleration[1];
								this.position[1] -= this.speed[1];
								this.speed[1] *= 0.1f;
							}
							this.speed[0] += this.acceleration[0];
							this.position[0] -= this.speed[0];
							this.speed[0] *= 0.1f;
							RenderView.cameraX = this.position[0];
							RenderView.cameraY = this.position[1];
						}
					}
					else if (position[1] + radius < newBottom && position[1] - radius > newTop) {
						// Region B
						float dax = (r.dstRect.left + r.dstRect.width() / 2) - position[0];
						float day = (r.dstRect.top + r.dstRect.height() / 2) - position[1];
						float dan = FloatMath.sqrt(dax * dax + day * day);
						if (dan == 0)
							dan = 1f;
						day /= dan;
						this.acceleration[1] -= day;
						reflectResponse();
					}
				}
				else
					//Catch everything else.
					reflectResponse();
				break;
			case UP:
				if (Math.abs(position[1] - r.dstRect.top) > halfHeight) {
					//Region A
					//Curve Region
					if (d1 < pipeRadius + radius) {
						if (d1 == 0)
							d1 = 1;
						dx1 /= d1;
						dy1 /= d1;
						float dotProduct = (speed[0] * dx1 + speed[1] * dy1);
						speed[0] += (-2 * dotProduct * dx1);
						speed[1] += (-2 * dotProduct * dy1);
						position[0] -= speed[0] * 10;
						position[1] -= speed[1] * 10;
					}
					else if (d2 < pipeRadius + radius) {
						if (d2 == 0)
							d2 = 1;
						dx2 /= d2;
						dy2 /= d2;
						float dotProduct = (speed[0] * dx2 + speed[1] * dy2);
						speed[0] += (-2 * dotProduct * dx2);
						speed[1] += (-2 * dotProduct * dy2);
						position[0] -= speed[0] * 10;
						position[1] -= speed[1] * 10;
						
					}
					else {
						//Non-curve Region
						reflectResponse();
					}
				}
				else if (Math.abs(position[1] - r.dstRect.top) < halfHeight) {
					//Region B
					//MUST NOT ALLOW CUE TO GO ON PIPE AT ALL. CANNOT ALLOW CUE TO MOVE UP/DOWN WHEN
					//INSIDE RAMP ONLY!
					
					//Split into 5 groups of Pipe.
					float newLeft = r.dstRect.left + pipeRadius;
					float newRight = r.dstRect.right - pipeRadius;
					if (position[0] + radius > newLeft && position[0] - radius < newLeft) {
						if (position[0] < newLeft) {
							if (acceleration[0] > 0) {
								this.speed[0] += this.acceleration[0];
								this.position[0] -= this.speed[0];
								this.speed[0] *= 0.1f;
							}
							else
								this.speed[0] += -2 * this.speed[0];
							this.speed[1] += this.acceleration[1];
							this.position[1] -= this.speed[1];
							this.speed[1] *= 0.1f;
							RenderView.cameraX = this.position[0];
							RenderView.cameraY = this.position[1];
						}
						else {
							if (acceleration[0] > 0) {
								this.speed[0] += -2 * this.speed[0];
							}
							else {
								float dax = (r.dstRect.left + r.dstRect.width() / 2) - position[0];
								float day = (r.dstRect.top + r.dstRect.height() / 2) - position[1];
								float dan = FloatMath.sqrt(dax * dax + day * day);
								if (dan == 0)
									dan = 1f;
								dax /= dan;
								this.acceleration[0] -= dax;
								this.speed[0] += this.acceleration[0];
								this.position[0] -= this.speed[0];
								this.speed[0] *= 0.1f;
							}
							this.speed[1] += this.acceleration[1];
							this.position[1] -= this.speed[1];
							this.speed[1] *= 0.1f;
							RenderView.cameraX = this.position[0];
							RenderView.cameraY = this.position[1];
						}
					}
					else if (position[0] - radius < newRight && position[0] + radius > newRight) {
						if (position[0] > newRight) {
							if (acceleration[0] < 0) {
								this.speed[0] += this.acceleration[0];
								this.position[0] -= this.speed[0];
								this.speed[0] *= 0.1f;
							}
							else
								this.speed[0] += -2 * this.speed[0];
							this.speed[1] += this.acceleration[1];
							this.position[1] -= this.speed[1];
							this.speed[1] *= 0.1f;
							RenderView.cameraX = this.position[0];
							RenderView.cameraY = this.position[1];
						}
						else {
							if (acceleration[0] < 0) {
								this.speed[0] += -2 * this.speed[0];
							}
							else {
								float dax = (r.dstRect.left + r.dstRect.width() / 2) - position[0];
								float day = (r.dstRect.top + r.dstRect.height() / 2) - position[1];
								float dan = FloatMath.sqrt(dax * dax + day * day);
								if (dan == 0)
									dan = 1f;
								dax /= dan;
								this.acceleration[0] -= dax;
								this.speed[0] += this.acceleration[0];
								this.position[0] -= this.speed[0];
								this.speed[0] *= 0.1f;
							}
							this.speed[1] += this.acceleration[1];
							this.position[1] -= this.speed[1];
							this.speed[1] *= 0.1f;
							RenderView.cameraX = this.position[0];
							RenderView.cameraY = this.position[1];
						}
					}
					else if (position[0] + radius < newRight && position[0] - radius > newLeft) {
						// Region B
						float dax = (r.dstRect.left + r.dstRect.width() / 2) - position[0];
						float day = (r.dstRect.top + r.dstRect.height() / 2) - position[1];
						float dan = FloatMath.sqrt(dax * dax + day * day);
						if (dan == 0)
							dan = 1f;
						dax /= dan;
						this.acceleration[0] -= dax;
						reflectResponse();
					}
				}
				else
					//Catch everything else.
					reflectResponse();
				break;
			case DOWN:
				if (Math.abs(position[1] - r.dstRect.top) < halfHeight) {
					//Region A
					//Curve Region
					if (d1 < pipeRadius + radius) {
						if (d1 == 0)
							d1 = 1;
						dx1 /= d1;
						dy1 /= d1;
						float dotProduct = (speed[0] * dx1 + speed[1] * dy1);
						speed[0] += (-2 * dotProduct * dx1);
						speed[1] += (-2 * dotProduct * dy1);
						position[0] -= speed[0] * 10;
						position[1] -= speed[1] * 10;
					}
					else if (d2 < pipeRadius + radius) {
						if (d2 == 0)
							d2 = 1;
						dx2 /= d2;
						dy2 /= d2;
						float dotProduct = (speed[0] * dx2 + speed[1] * dy2);
						speed[0] += (-2 * dotProduct * dx2);
						speed[1] += (-2 * dotProduct * dy2);
						position[0] -= speed[0] * 10;
						position[1] -= speed[1] * 10;
						
					}
					else {
						//Non-curve Region
						reflectResponse();
					}
				}
				else if (Math.abs(position[1] - r.dstRect.top) > halfHeight) {
					//Region B
					//MUST NOT ALLOW CUE TO GO ON PIPE AT ALL. CANNOT ALLOW CUE TO MOVE UP/DOWN WHEN
					//INSIDE RAMP ONLY!
					
					//Split into 5 groups of Pipe.
					float newLeft = r.dstRect.left + pipeRadius;
					float newRight = r.dstRect.right - pipeRadius;
					if (position[0] + radius > newLeft && position[0] - radius < newLeft) {
						if (position[0] < newLeft) {
							if (acceleration[0] > 0) {
								this.speed[0] += this.acceleration[0];
								this.position[0] -= this.speed[0];
								this.speed[0] *= 0.1f;
							}
							else
								this.speed[0] += -2 * this.speed[0];
							this.speed[1] += this.acceleration[1];
							this.position[1] -= this.speed[1];
							this.speed[1] *= 0.1f;
							RenderView.cameraX = this.position[0];
							RenderView.cameraY = this.position[1];
						}
						else {
							if (acceleration[0] > 0) {
								this.speed[0] += -2 * this.speed[0];
							}
							else {
								float dax = (r.dstRect.left + r.dstRect.width() / 2) - position[0];
								float day = (r.dstRect.top + r.dstRect.height() / 2) - position[1];
								float dan = FloatMath.sqrt(dax * dax + day * day);
								if (dan == 0)
									dan = 1f;
								dax /= dan;
								this.acceleration[0] -= dax;
								this.speed[0] += this.acceleration[0];
								this.position[0] -= this.speed[0];
								this.speed[0] *= 0.1f;
							}
							this.speed[1] += this.acceleration[1];
							this.position[1] -= this.speed[1];
							this.speed[1] *= 0.1f;
							RenderView.cameraX = this.position[0];
							RenderView.cameraY = this.position[1];
						}
					}
					else if (position[0] - radius < newRight && position[0] + radius > newRight) {
						if (position[0] > newRight) {
							if (acceleration[0] < 0) {
								this.speed[0] += this.acceleration[0];
								this.position[0] -= this.speed[0];
								this.speed[0] *= 0.1f;
							}
							else
								this.speed[0] += -2 * this.speed[0];
							this.speed[1] += this.acceleration[1];
							this.position[1] -= this.speed[1];
							this.speed[1] *= 0.1f;
							RenderView.cameraX = this.position[0];
							RenderView.cameraY = this.position[1];
						}
						else {
							if (acceleration[0] < 0) {
								this.speed[0] += -2 * this.speed[0];
							}
							else {
								float dax = (r.dstRect.left + r.dstRect.width() / 2) - position[0];
								float day = (r.dstRect.top + r.dstRect.height() / 2) - position[1];
								float dan = FloatMath.sqrt(dax * dax + day * day);
								if (dan == 0)
									dan = 1f;
								dax /= dan;
								this.acceleration[0] -= dax;
								this.speed[0] += this.acceleration[0];
								this.position[0] -= this.speed[0];
								this.speed[0] *= 0.1f;
							}
							this.speed[1] += this.acceleration[1];
							this.position[1] -= this.speed[1];
							this.speed[1] *= 0.1f;
							RenderView.cameraX = this.position[0];
							RenderView.cameraY = this.position[1];
						}
					}
					else if (position[0] + radius < newRight && position[0] - radius > newLeft) {
						// Region B
						float dax = (r.dstRect.left + r.dstRect.width() / 2) - position[0];
						float day = (r.dstRect.top + r.dstRect.height() / 2) - position[1];
						float dan = FloatMath.sqrt(dax * dax + day * day);
						if (dan == 0)
							dan = 1f;
						dax /= dan;
						this.acceleration[0] -= dax;
						reflectResponse();
					}
				}
				else
					//Catch everything else.
					reflectResponse();
				break;
		}
		
		RenderView.cameraX = this.position[0];
		RenderView.cameraY = this.position[1];
	}
}
