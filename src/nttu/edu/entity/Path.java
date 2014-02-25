package nttu.edu.entity;

public abstract class Path extends Entity {
	public static enum Orientation {
		//X axis to Y axis.
		HORIZONTAL, VERTICAL, LEFT_TO_UP, LEFT_TO_DOWN, RIGHT_TO_UP, RIGHT_TO_DOWN
	}
	
	public Orientation orientation;
	public final int PIPE_SIZE = 2;
	public float ratio;
	
	private float defaultX, defaultY, defaultZ;
	
	//Positions are marked from the top left corner (Since they are squares.)
	//Z axis is for height;
	public float[] position = new float[3];
	
	public void setOrientation(Orientation o) {
		this.orientation = o;
		switch (this.orientation) {
			case HORIZONTAL:
			default:
				srcRect.set(16, 8, 24, 16);
				break;
			case VERTICAL:
				srcRect.set(16, 0, 24, 8);
				break;
			case LEFT_TO_UP:
				srcRect.set(32, 8, 40, 16);
				break;
			case LEFT_TO_DOWN:
				srcRect.set(32, 0, 40, 8);
				break;
			case RIGHT_TO_UP:
				srcRect.set(24, 8, 32, 16);
				break;
			case RIGHT_TO_DOWN:
				srcRect.set(24, 0, 32, 8);
				break;
		}
	}
	
	public Orientation getOrientation() {
		return orientation;
	}
	
	public void setPlacement(float x, float y, float z) {
		// Z for above/below placements.
		//(x, y) is the center of a single pipe grid.
		position[0] = x;
		position[1] = y;
		position[2] = z;
		defaultX = x;
		defaultY = y;
		defaultZ = z;
		
	}
	
	public void setAspectRatio(float aspectRatio) {
		ratio = aspectRatio;
	}
	
	public abstract void place();
	
	@Override
	public void reset() {
		position[0] = defaultX;
		position[1] = defaultY;
		position[2] = defaultZ;
	}
}
