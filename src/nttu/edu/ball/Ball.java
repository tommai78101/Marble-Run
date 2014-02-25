package nttu.edu.ball;

import nttu.edu.entity.Bumper;
import nttu.edu.entity.Connector;
import nttu.edu.entity.CurvePipe;
import nttu.edu.entity.Entity;
import nttu.edu.entity.Funnel;
import nttu.edu.entity.Hole;
import nttu.edu.entity.Path;
import nttu.edu.entity.Pipe;
import nttu.edu.entity.Ramp;
import nttu.edu.graphics.RenderView;
import nttu.edu.level.Stage;
import android.util.FloatMath;

public abstract class Ball extends Entity {
	
	public float[] position = new float[3];
	public float[] speed = new float[3];
	public float[] acceleration = new float[3];
	public float radius;
	public float screenWidth;
	public float screenHeight;
	public boolean insideHole;
	
	//TODO: May want to use polymorphism.
	//Really, desperately in need of a rewrite.
	public CurvePipe curvePipe;
	public Pipe pipe;
	public Bumper bumper;
	public Funnel funnel;
	public Ramp ramp;
	public Connector connector;
	
	public Ball() {
		super();
		for (int i = 0; i <= 2; i++) {
			position[i] = 0f;
			speed[i] = 0f;
			acceleration[i] = 0f;
		}
		radius = 0f;
		insideHole = false;
		curvePipe = null;
	}
	
	public void setPosition(float x, float y, float z) {
		position[0] = x;
		position[1] = y;
		position[2] = z;
	}
	
	public void setSpeed(float vx, float vy, float vz) {
		speed[0] = vx;
		speed[1] = vy;
		speed[2] = vz;
	}
	
	public void setAcceleration(float ax, float ay, float az) {
		//TODO: Add SettingsActivity and use static variables to switch around these.
		acceleration[0] = ax;
		acceleration[1] = ay;
		acceleration[2] = az;
	}
	
	public void addAcceleration(float x, float y, float z) {
		acceleration[0] += x;
		acceleration[1] += y;
		acceleration[2] = z;
	}
	
	public void setRadius(float value) {
		radius = value;
	}
	
	public void setBoundary(int width, int height) {
		this.screenHeight = height;
		this.screenWidth = width;
	}
	
	public boolean checkCollision(Ball b) {
		// Return true if collision is to occur.
		// Return false otherwise.
		double distance = radius + b.radius;
		double dx = (this.position[0]) - (b.position[0]);
		double dy = (this.position[1]) - (b.position[1]);
		double dist = Math.hypot(dx, dy);
		return (distance > dist);
	}
	
	public void collisionResponse(Ball b) {
		double dx = this.position[0] - b.position[0];
		double dy = this.position[1] - b.position[1];
		double dist = Math.hypot(dx, dy);
		double penetration = Math.max(0, radius + b.radius - dist);
		float radiusA = (float) (penetration * dx / (dist * 2));
		float radiusB = (float) (penetration * dy / (dist * 2));
		this.position[0] += radiusA;
		this.position[1] += radiusB;
		b.position[0] -= radiusA;
		b.position[1] -= radiusB;
	}
	
	public boolean checkCollision(Hole h) {
		float dx = (this.position[0]) - (h.x);
		float dy = (this.position[1]) - (h.y);
		double distance = Math.hypot(dx, dy);
		if (distance < 7.5 * RenderView.AspectRatio)
			this.insideHole = true;
		return distance < 8.5 * RenderView.AspectRatio;
	}
	
	public void checkCollisionFlag(Hole h) {
		float dx = (this.position[0]) - (h.x);
		float dy = (this.position[1]) - (h.y);
		double distance = Math.hypot(dx, dy);
		if (distance < 7.5 * RenderView.AspectRatio)
			this.insideHole = true;
	}
	
	public void reflect(Hole h) {
		// R = -2*(V dot N)*N + V
		// N is normalized.
		
		if (this.insideHole) {
			double nx = (this.position[0]) - (h.x);
			double ny = (this.position[1]) - (h.y);
			double nd = Math.hypot(nx, ny);
			if (nd > 8.0 * RenderView.AspectRatio) {
				if (nd == 0)
					nd = 1;
				nx /= nd;
				ny /= nd;
				double dotProduct = this.speed[0] * nx + this.speed[1] * ny;
				this.speed[0] += (float) (-2 * dotProduct * nx);
				this.speed[1] += (float) (-2 * dotProduct * ny);
			}
		}
	}
	
	public abstract void reflectResponse();
	
	public void resolveCollision(Ball b) {
		float xVelocity = this.speed[0] - b.speed[0];
		float yVelocity = this.speed[1] - b.speed[1];
		float xDist = this.position[0] - b.position[0];
		float yDist = this.position[1] - b.position[1];
		float dotProduct = xDist * xVelocity + yDist * yVelocity;
		if (dotProduct > 0) {
			float distSquared = xDist * xDist + yDist * yDist;
			float collisionScale = dotProduct / distSquared;
			float xCollision = xDist * collisionScale;
			float yCollision = yDist * collisionScale;
			b.speed[0] += xCollision;
			b.speed[1] += yCollision;
			this.speed[0] -= xCollision;
			this.speed[1] -= yCollision;
		}
	}
	
	public void gravityPull(Hole h) {
		double dx = ((this.position[0]) - (h.x));
		double dy = ((this.position[1]) - (h.y));
		dx *= 0.0003;
		dy *= 0.0003;
		this.speed[0] += (float) dx;
		this.speed[1] += (float) dy;
	}
	
	public boolean checkAreaGravity(Hole h) {
		double dx = ((this.position[0]) - (h.x));
		double dy = ((this.position[1]) - (h.y));
		double dist = Math.hypot(dx, dy);
		return radius + 23 >= dist;
	}
	
	// Work more on obtaining current position data.
	// Note to Self: This is currently useless. I have no idea what it is
	// supposed to be for.
	public int getCurrentPositionData(Stage stage) {
		int[] data = stage.data;
		int w = (int) (position[0] * stage.width);
		int h = (int) position[1];
		return data[w + h];
	}
	
	public void setCurvePipe(CurvePipe p) {
		curvePipe = p;
	}
	
	public CurvePipe getCurvePipe() {
		return curvePipe;
	}
	
	// TODO: Fix the collision strength when two balls are colliding while
	// reflecting.
	public void reflect(CurvePipe p) {
		if (p != null) {
			double nx = (this.position[0]) - (p.x);
			double ny = (this.position[1]) - (p.y);
			double nd = Math.hypot(nx, ny);
			if (nd > p.PIPE_SIZE * 4 && nd < p.PIPE_SIZE * 4 + radius * 2) {
				if (nd == 0)
					nd = 1;
				nx /= nd;
				ny /= nd;
				double dotProduct = this.speed[0] * nx + this.speed[1] * ny;
				this.speed[0] += (float) (-2 * dotProduct * nx);
				this.speed[1] += (float) (-2 * dotProduct * ny);
				reflectResponse();
			}
			
		}
	}
	
	public abstract void reflect(Bumper b);
	
	public void setPipe(Pipe p) {
		pipe = p;
	}
	
	public Pipe getPipe() {
		return pipe;
	}
	
	public abstract void reflect(Pipe p);
	
	public void setBumper(Bumper b) {
		bumper = b;
	}
	
	public Bumper getBumper() {
		return bumper;
	}
	
	public void resolveCollision(Bumper b) {
		float xDist = this.position[0] - b.x;
		float yDist = this.position[1] - b.y;
		float dotProduct = xDist * speed[0] + yDist * speed[1];
		if (dotProduct > 0) {
			float distSquared = xDist * xDist + yDist * yDist;
			float collisionScale = dotProduct / distSquared;
			float xCollision = xDist * collisionScale;
			float yCollision = yDist * collisionScale;
			speed[0] += xCollision;
			speed[1] += yCollision;
			position[0] -= speed[0];
			position[1] -= speed[0];
		}
	}
	
	public void setFunnel(Funnel f) {
		funnel = f;
	}
	
	public Funnel getFunnel() {
		return funnel;
	}
	
	public void setRamp(Ramp r) {
		ramp = r;
	}
	
	public Ramp getRamp() {
		return ramp;
	}
	
	public boolean funnelDetector = false;
	public boolean funnelFlag1 = false;
	public boolean funnelFlag2 = false;
	
	public void reflect(Funnel f) {
		if (f != null) {
			switch (f.direction) {
				case LEFT:
				default:
					if (funnelDetector == false && this.position[0] > f.dstRect.left + f.dstRect.width() / 2)
						funnelDetector = true;
					break;
				case RIGHT:
					if (funnelDetector == false && this.position[0] < f.dstRect.left + f.dstRect.width() / 2)
						funnelDetector = true;
					break;
				case UP:
					if (funnelDetector == false && this.position[1] > f.dstRect.top + f.dstRect.height() / 2)
						funnelDetector = true;
					break;
				case DOWN:
					if (funnelDetector == false && this.position[1] < f.dstRect.top + f.dstRect.height() / 2)
						funnelDetector = true;
					break;
			}
			float dx1 = (this.position[0] - f.x1);
			float dy1 = (this.position[1] - f.y1);
			float dx2 = (this.position[0] - f.x2);
			float dy2 = (this.position[1] - f.y2);
			final float r = f.PIPE_SIZE * 12;
			float dist1 = FloatMath.sqrt(dx1 * dx1 + dy1 * dy1);
			float dist2 = FloatMath.sqrt(dx2 * dx2 + dy2 * dy2);
			
			if (funnelDetector) {
				if (dist1 < r) {
					funnelFlag1 = true;
				}
				else if (dist2 < r) {
					funnelFlag2 = true;
				}
				else
					reflectResponse();
			}
			else {
				if (dist1 < r) {
					funnelResponse(dx1, dy1, dist1);
				}
				else if (dist2 < r) {
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
	}
	
	protected void funnelResponse(float x, float y, float distance) {
		if (distance == 0)
			distance = 1;
		x /= distance;
		y /= distance;
		float dotProduct = speed[0] * x + speed[1] * y;
		speed[0] += (-2 * dotProduct * x);
		speed[1] += (-2 * dotProduct * y);
		position[0] -= speed[0] * 10;
		position[1] -= speed[1] * 10;
	}
	
	public abstract void reflect(Ramp r);
	
	public void setConnector(Connector c) {
		connector = c;
	}
	
	public Path getConnector() {
		return connector;
	}
	
	public void reflect(Connector p) {
		if (p != null) {
			float cx = Math.abs(position[0] - p.position[0]);
			float cy = Math.abs(position[1] - p.position[1]);
			float r = this.radius + p.radius;
			float ox = Math.abs(cx - r);
			float oy = Math.abs(cy - r);
			if (ox > oy) {
				speed[1] += -2 * speed[1];
				position[1] -= speed[1] * 10;
			}
			else if (ox < oy) {
				speed[0] += -2 * speed[0];
				position[0] -= speed[0] * 10;
			}
			else {
				reflectResponse();
			}
		}
	}
}
