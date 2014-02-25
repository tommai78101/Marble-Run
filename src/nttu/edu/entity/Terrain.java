package nttu.edu.entity;


public abstract class Terrain extends Entity {
	public float x;
	public float y;
	protected float defaultX;
	protected float defaultY;
	
	public Terrain(float x, float y){
		this.x = x;
		this.y = y;
		this.defaultX = x;
		this.defaultY = y;
	}
	
	public void reset(){
		this.x = defaultX;
		this.y = defaultY;
	}
}
